/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.announce.web.includes;

import fr.paris.lutece.plugins.announce.business.AnnounceSearchFilter;
import fr.paris.lutece.plugins.announce.service.AnnounceService;
import fr.paris.lutece.plugins.announce.web.AnnounceApp;
import fr.paris.lutece.portal.service.content.PageData;
import fr.paris.lutece.portal.service.includes.PageInclude;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * Page include for announce search form
 */
public class AnnounceSearchFormPageInclude implements PageInclude
{
    private static final String TEMPLATE_ANNOUNCE_SEARCH_FORM = "skin/plugins/announce/search_form.html";

    private static final String MARK_FILTER_DATE_MIN = "filter_date_min";
    private static final String MARK_FILTER_DATE_MAX = "filter_date_max";
    private static final String MARK_FILTER = "filter";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_LIST_SECTORS = "list_sectors";
    private static final String MARK_USER = "user";
    private static final String MARK_ANNOUNCE_SEARCH_FORM_INCLUDE = "pageinclude_announcesearchform";

    /**
     * {@inheritDoc}
     */
    @Override
    public void fillTemplate( Map<String, Object> rootModel, PageData data, int nMode, HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<String, Object>( );

        AnnounceSearchFilter filter = AnnounceApp.getAnnounceFilterFromRequest( request );

        DateFormat dateFormat = AnnounceService.getDateFormat( );

        model.put( MARK_LIST_SECTORS, AnnounceApp.getSectorList( ) );
        model.put( MARK_FILTER_DATE_MIN, filter.getDateMin( ) != null ? dateFormat.format( filter.getDateMin( ) )
                : null );
        model.put( MARK_FILTER_DATE_MAX, filter.getDateMax( ) != null ? dateFormat.format( filter.getDateMax( ) )
                : null );
        model.put( MARK_FILTER, filter );
        Locale locale = request == null ? Locale.getDefault( ) : request.getLocale( );

        if ( request != null && SecurityService.isAuthenticationEnable( ) )
        {
            model.put( MARK_USER, SecurityService.getInstance( ).getRegisteredUser( request ) );
        }
        model.put( MARK_LOCALE, locale );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_ANNOUNCE_SEARCH_FORM, locale, model );

        rootModel.put( MARK_ANNOUNCE_SEARCH_FORM_INCLUDE, template.getHtml( ) );
    }

}
