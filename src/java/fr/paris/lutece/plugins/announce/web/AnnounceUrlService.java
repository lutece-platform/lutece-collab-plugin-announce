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
package fr.paris.lutece.plugins.announce.web;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.announce.utils.AnnounceUtils;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.util.url.UrlItem;

/**
 * Utility class for building announce-related URLs
 */
public final class AnnounceUrlService
{
    private static final String PARAMETER_PAGE = "page";
    private static final String PARAMETER_USERNAME = "username";
    private static final String PARAMETER_CATEGORY_ID = "category_id";
    private static final String PARAMETER_ANNOUNCE_ID = "announce_id";
    private static final String PARAMETER_TITLE_ANNOUNCE = "title_announce";
    private static final String PARAMETER_DESCRIPTION_ANNOUNCE = "description_announce";
    private static final String PARAMETER_PRICE_ANNOUNCE = "price_announce";
    private static final String PARAMETER_ID_FILTER = "id_filter";
    private static final String PARAMETER_SORT_BY = "sortBy";
    private static final String PARAMETER_HAS_FILTER = "hasFilter";

    private static final String ACTION_VIEW_ANNOUNCE = "view_announce";
    private static final String ACTION_VIEW_ANNOUNCES = "view_announces";
    private static final String ACTION_SEARCH = "search";

    /**
     * Private constructor
     */
    private AnnounceUrlService( )
    {
    }

    /**
     * Get the URL to search for announces sorted by the given criterion
     *
     * @param request
     *            The request
     * @param nSort
     *            The sort type
     * @return The URL to search announces
     */
    public static String getUrlSearchAnnounceSort( HttpServletRequest request, int nSort )
    {
        return getUrlSearchAnnounceSort( request, 0, nSort );
    }

    /**
     * Get the URL to search for announces sorted by the given criterion
     *
     * @param request
     *            The request
     * @param nIdFilter
     *            The id of the filter to load, or 0 to use the filter stored in session if any
     * @param nSort
     *            The sort type
     * @return The URL to search announces
     */
    public static String getUrlSearchAnnounceSort( HttpServletRequest request, int nIdFilter, int nSort )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + AppPathService.getPortalUrl( ) );
        urlItem.addParameter( PARAMETER_PAGE, AnnounceUtils.PARAMETER_PAGE_ANNOUNCE );
        urlItem.addParameter( MVCUtils.PARAMETER_ACTION, ACTION_SEARCH );

        if ( nIdFilter > 0 )
        {
            urlItem.addParameter( PARAMETER_ID_FILTER, nIdFilter );
        }
        if ( nSort == 0 )
        {
            urlItem.addParameter( PARAMETER_SORT_BY, "date_creation" );
        }
        if ( nSort == 1 )
        {
            urlItem.addParameter( PARAMETER_SORT_BY, "date_modification" );
        }
        if ( nSort == 2 )
        {
            urlItem.addParameter( PARAMETER_SORT_BY, PARAMETER_TITLE_ANNOUNCE );
        }
        if ( nSort == 3 )
        {
            urlItem.addParameter( PARAMETER_SORT_BY, PARAMETER_PRICE_ANNOUNCE );
        }
        if ( nSort == 4 )
        {
            urlItem.addParameter( PARAMETER_SORT_BY, PARAMETER_DESCRIPTION_ANNOUNCE );
        }
        if ( nSort == 5 )
        {
            urlItem.addParameter( PARAMETER_SORT_BY, "date_publication" );
        }

        return urlItem.getUrl( );
    }

    /**
     * Get the URL to search for announces
     *
     * @param request
     *            The request
     * @return The URL to search announces
     */
    public static String getUrlSearchAnnounce( HttpServletRequest request )
    {
        return getUrlSearchAnnounce( request, 0 );
    }

    /**
     * Get the URL to search for announces
     *
     * @param request
     *            The request
     * @param nIdFilter
     *            The id of the filter to load, or 0 to use the filter stored in session if any
     * @return The URL to search announces
     */
    public static String getUrlSearchAnnounce( HttpServletRequest request, int nIdFilter )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + AppPathService.getPortalUrl( ) );
        urlItem.addParameter( PARAMETER_PAGE, AnnounceUtils.PARAMETER_PAGE_ANNOUNCE );
        urlItem.addParameter( MVCUtils.PARAMETER_ACTION, ACTION_SEARCH );

        if ( nIdFilter > 0 )
        {
            urlItem.addParameter( PARAMETER_ID_FILTER, nIdFilter );
        }

        return urlItem.getUrl( );
    }

    /**
     * Get the relative URL to view an announce
     *
     * @param nIdAnnounce
     *            The id of the announce to view
     * @return The relative URL to view the announce
     */
    public static String getRelativeUrlViewAnnounce( int nIdAnnounce )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getPortalUrl( ) );
        urlItem.addParameter( PARAMETER_PAGE, AnnounceUtils.PARAMETER_PAGE_ANNOUNCE );
        urlItem.addParameter( MVCUtils.PARAMETER_ACTION, ACTION_VIEW_ANNOUNCE );
        urlItem.addParameter( PARAMETER_ANNOUNCE_ID, nIdAnnounce );

        return urlItem.getUrl( );
    }

    /**
     * Get the URL to search for a given category
     *
     * @param request
     *            The request
     * @param nIdCategory
     *            The id of the category to search for
     * @return The URL
     */
    public static String getUrlViewCategory( HttpServletRequest request, int nIdCategory )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + AppPathService.getPortalUrl( ) );
        urlItem.addParameter( PARAMETER_PAGE, AnnounceUtils.PARAMETER_PAGE_ANNOUNCE );
        urlItem.addParameter( MVCUtils.PARAMETER_ACTION, ACTION_SEARCH );
        urlItem.addParameter( PARAMETER_CATEGORY_ID, nIdCategory );
        urlItem.addParameter( PARAMETER_HAS_FILTER, Boolean.TRUE.toString( ) );

        return urlItem.getUrl( );
    }

    /**
     * Get the URL to view announces of a user
     *
     * @param request
     *            The request
     * @param strUserName
     *            The name of the user to view announces of
     * @return The URL
     */
    public static String getUrlViewUserAnnounces( HttpServletRequest request, String strUserName )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + AppPathService.getPortalUrl( ) );
        urlItem.addParameter( PARAMETER_PAGE, AnnounceUtils.PARAMETER_PAGE_ANNOUNCE );
        urlItem.addParameter( MVCUtils.PARAMETER_ACTION, ACTION_VIEW_ANNOUNCES );
        urlItem.addParameter( PARAMETER_USERNAME, strUserName );

        return urlItem.getUrl( );
    }
}
