/*
 * Copyright (c) 2002-2026, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.announce.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import fr.paris.lutece.plugins.announce.business.AnnounceSearchFilter;
import fr.paris.lutece.plugins.announce.business.AnnounceSearchFilterHome;
import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * Utility class for building an {@link AnnounceSearchFilter} from an HTTP request
 */
public final class AnnounceFilterService
{
    public static final String SESSION_KEY_ANNOUNCE_FILTER = "announce.session.announceSearchFilter";

    private static final String PARAMETER_SECTOR_ID = "sector_id";
    private static final String PARAMETER_CATEGORY_ID = "category_id";
    private static final String PARAMETER_KEYWORDS = "keywords";
    private static final String PARAMETER_DATE_MIN = "date_min";
    private static final String PARAMETER_DATE_MAX = "date_max";
    private static final String PARAMETER_PRICE_MIN = "price_min";
    private static final String PARAMETER_PRICE_MAX = "price_max";
    private static final String PARAMETER_HAS_FILTER = "hasFilter";
    private static final String PARAMETER_ID_FILTER = "id_filter";

    private static final String CONSTANT_BLANK_SPACE = " ";
    private static final String CONSTANT_COMA = ",";
    private static final String CONSTANT_POINT = ".";

    /**
     * Private constructor
     */
    private AnnounceFilterService( )
    {
    }

    /**
     * Get the announce search filter with data contained in an HTTP request.
     * If the request contains a filter id, the corresponding saved filter is loaded.
     * If the request contains filter parameters, a new filter is built.
     * Otherwise, the filter stored in session is returned (or a new empty filter).
     *
     * @param request
     *            The request
     * @return The search filter. If the request contains no filter data, then the returned search filter is empty but never null.
     */
    public static AnnounceSearchFilter getAnnounceFilterFromRequest( HttpServletRequest request )
    {
        if ( request == null )
        {
            return new AnnounceSearchFilter( );
        }

        String strIdFilter = request.getParameter( PARAMETER_ID_FILTER );

        if ( StringUtils.isNotEmpty( strIdFilter ) && StringUtils.isNumeric( strIdFilter ) )
        {
            int nIdFilter = Integer.parseInt( strIdFilter );
            AnnounceSearchFilter filter = AnnounceSearchFilterHome.findByPrimaryKey( nIdFilter );
            request.getSession( ).setAttribute( SESSION_KEY_ANNOUNCE_FILTER, filter );

            return filter;
        }

        if ( Boolean.parseBoolean( request.getParameter( PARAMETER_HAS_FILTER ) ) )
        {
            String strKeywords = request.getParameter( PARAMETER_KEYWORDS );
            String strIdSector = request.getParameter( PARAMETER_SECTOR_ID );
            String strIdCategory = request.getParameter( PARAMETER_CATEGORY_ID );
            String strDateMin = request.getParameter( PARAMETER_DATE_MIN );
            String strDateMax = request.getParameter( PARAMETER_DATE_MAX );
            String strPriceMin = request.getParameter( PARAMETER_PRICE_MIN );
            String strPriceMax = request.getParameter( PARAMETER_PRICE_MAX );
            strKeywords = ( strKeywords == null ) ? StringUtils.EMPTY : strKeywords;

            Date formatedDateMin = null;
            Date formatedDateMax = null;

            DateFormat dateFormat = AnnounceService.getDateFormat( );

            if ( StringUtils.isNotEmpty( strDateMin ) )
            {
                try
                {
                    formatedDateMin = dateFormat.parse( strDateMin.trim( ) );
                }
                catch( ParseException e )
                {
                    AppLogService.error( e );
                }
            }

            if ( StringUtils.isNotEmpty( strDateMax ) )
            {
                try
                {
                    formatedDateMax = dateFormat.parse( strDateMax.trim( ) );
                }
                catch( ParseException e )
                {
                    AppLogService.error( e );
                }
            }

            AnnounceSearchFilter filter = new AnnounceSearchFilter( );
            filter.setKeywords( strKeywords );

            if ( StringUtils.isNotEmpty( strIdSector ) && StringUtils.isNumeric( strIdSector ) )
            {
                filter.setIdSector( Integer.parseInt( strIdSector ) );
            }

            if ( StringUtils.isNotEmpty( strIdCategory ) && StringUtils.isNumeric( strIdCategory ) )
            {
                filter.setIdCategory( Integer.parseInt( strIdCategory ) );
            }

            filter.setDateMin( formatedDateMin );
            filter.setDateMax( formatedDateMax );

            if ( StringUtils.isNotEmpty( strPriceMin ) )
            {
                strPriceMin = strPriceMin.replace( CONSTANT_BLANK_SPACE, StringUtils.EMPTY ).trim( );

                if ( Strings.CS.contains( strPriceMin, CONSTANT_COMA ) )
                {
                    strPriceMin = strPriceMin.substring( 0, strPriceMin.indexOf( CONSTANT_COMA ) );
                }

                if ( Strings.CS.contains( strPriceMin, CONSTANT_POINT ) )
                {
                    strPriceMin = strPriceMin.substring( 0, strPriceMin.indexOf( CONSTANT_POINT ) );
                }

                if ( StringUtils.isNumeric( strPriceMin ) )
                {
                    filter.setPriceMin( Integer.parseInt( strPriceMin ) );
                }
            }

            if ( StringUtils.isNotEmpty( strPriceMax ) )
            {
                strPriceMax = strPriceMax.replace( CONSTANT_BLANK_SPACE, StringUtils.EMPTY ).trim( );

                if ( Strings.CS.contains( strPriceMax, CONSTANT_COMA ) )
                {
                    strPriceMax = strPriceMax.substring( 0, strPriceMax.indexOf( CONSTANT_COMA ) );
                }

                if ( Strings.CS.contains( strPriceMax, CONSTANT_POINT ) )
                {
                    strPriceMax = strPriceMax.substring( 0, strPriceMax.indexOf( CONSTANT_POINT ) );
                }

                if ( StringUtils.isNumeric( strPriceMax ) )
                {
                    filter.setPriceMax( Integer.parseInt( strPriceMax ) );
                }
            }

            request.getSession( ).setAttribute( SESSION_KEY_ANNOUNCE_FILTER, filter );

            return filter;
        }

        AnnounceSearchFilter filter = (AnnounceSearchFilter) request.getSession( ).getAttribute( SESSION_KEY_ANNOUNCE_FILTER );

        if ( filter == null )
        {
            filter = new AnnounceSearchFilter( );
        }

        return filter;
    }
}
