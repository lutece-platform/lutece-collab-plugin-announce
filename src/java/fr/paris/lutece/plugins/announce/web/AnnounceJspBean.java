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
package fr.paris.lutece.plugins.announce.web;

import fr.paris.lutece.plugins.announce.business.Announce;
import fr.paris.lutece.plugins.announce.business.AnnounceHome;
import fr.paris.lutece.plugins.announce.utils.AnnounceUtils;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.util.LocalizedDelegatePaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage announce features ( manage,
 * create, modify, remove )
 */
public class AnnounceJspBean extends PluginAdminPageJspBean
{
    /**
     * Right to manage this feature
     */
    public static final String RIGHT_MANAGE_ANNOUNCE = "ANNOUNCE_MANAGEMENT";
    private static final long serialVersionUID = 6293138267315605660L;

    /* parameter */
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_ANNOUNCE_ID = "announce_id";

    /* properties */
    private static final String PROPERTY_PAGE_TITLE_MANAGE_ANNOUNCES = "announce.manage_announces.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_ANNOUNCE = "announce.modify_announce.pageTitle";
    private static final String PROPERTY_DEFAULT_LIST_ANNOUNCE_PER_PAGE = "announce.announce.itemsPerPage";
    private static final String PROPERTY_PAGE_TITLE_PREVIEW_ANNOUNCE = "announce.preview_announce.pageTitle";

    /* templates */
    private static final String TEMPLATE_MANAGE_ANNOUNCES = "admin/plugins/announce/manage_announces.html";
    private static final String TEMPLATE_MODIFY_ANNOUNCE = "admin/plugins/announce/modify_announce.html";
    private static final String TEMPLATE_PREVIEW_ANNOUNCE = "admin/plugins/announce/preview_announce.html";

    /* Jsp Definition */
    private static final String JSP_DO_REMOVE_ANNOUNCE = "jsp/admin/plugins/announce/DoRemoveAnnounce.jsp";
    private static final String JSP_MANAGE_ANNOUNCES = "jsp/admin/plugins/announce/ManageAnnounces.jsp";
    private static final String JSP_REDIRECT_TO_MANAGE_ANNOUNCES = "ManageAnnounces.jsp";

    /* Messages */
    private static final String MESSAGE_CONFIRM_REMOVE_ANNOUNCE = "announce.message.confirmRemoveAnnounce";

    /* Markers */
    private static final String MARK_ANNOUNCE = "announce";
    private static final String MARK_ANNOUNCE_LIST = "list_announces";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_LIST_RESPONSES = "list_responses";

    /* Variables */
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;

    /**
     * {@inheritDoc}
     */
    @Override
    public Plugin getPlugin( )
    {
        Plugin plugin = super.getPlugin( );

        if ( plugin == null )
        {
            plugin = AnnounceUtils.getPlugin( );
        }

        return plugin;
    }

    /**
     * Returns the list of announce
     * 
     * @param request The HTTP request
     * @return the announces list
     */
    public String getManageAnnounces( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_ANNOUNCES );

        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        if ( _nDefaultItemsPerPage == 0 )
        {
            _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ANNOUNCE_PER_PAGE, 50 );
        }
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        List<Integer> listIdAnnounces = AnnounceHome.findAll( );

        Paginator<Integer> paginatorId = new Paginator<Integer>( listIdAnnounces, _nItemsPerPage, StringUtils.EMPTY,
                PARAMETER_PAGE_INDEX, _strCurrentPageIndex );

        List<Announce> listAnnounces = AnnounceHome.findByListId( paginatorId.getPageItems( ) );

        Map<String, Object> model = new HashMap<String, Object>( );

        LocalizedDelegatePaginator<Announce> paginator = new LocalizedDelegatePaginator<Announce>( listAnnounces,
                _nItemsPerPage, getUrlPage( ), Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex,
                listIdAnnounces.size( ),
                getLocale( ) );

        model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( _nItemsPerPage ) );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_ANNOUNCE_LIST, paginator.getPageItems( ) );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_ANNOUNCES, getLocale( ), model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Get the page to display the preview of an announce
     * @param request The request
     * @return The HTML to display
     */
    public String getPreviewAnnounce( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_PREVIEW_ANNOUNCE );

        int nIdAnnounce = Integer.parseInt( request.getParameter( PARAMETER_ANNOUNCE_ID ) );
        Announce announce = AnnounceHome.findByPrimaryKey( nIdAnnounce );

        Collection<Response> listResponses = AnnounceHome.findListResponse( announce.getId( ), false );

        HashMap<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_LIST_RESPONSES, listResponses );
        model.put( MARK_ANNOUNCE, announce );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PREVIEW_ANNOUNCE, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Returns the form to update info about a announce
     * 
     * @param request The Http request
     * @return The HTML form to update info
     */
    public String getModifyAnnounce( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MODIFY_ANNOUNCE );

        int nIdAnnounce = Integer.parseInt( request.getParameter( PARAMETER_ANNOUNCE_ID ) );
        Announce announce = AnnounceHome.findByPrimaryKey( nIdAnnounce );

        HashMap<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_ANNOUNCE, announce );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_ANNOUNCE, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Manages the removal form of a announce whose identifier is in the http
     * request
     * 
     * @param request The Http request
     * @return the html code to confirm
     */
    public String getConfirmRemoveAnnounce( HttpServletRequest request )
    {
        int nIdAnnounce = Integer.parseInt( request.getParameter( PARAMETER_ANNOUNCE_ID ) );

        UrlItem url = new UrlItem( JSP_DO_REMOVE_ANNOUNCE );
        url.addParameter( PARAMETER_ANNOUNCE_ID, nIdAnnounce );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_ANNOUNCE, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Treats the removal form of a announce
     * 
     * @param request The Http request
     * @return the jsp URL to display the form to manage announces
     */
    public String doRemoveAnnounce( HttpServletRequest request )
    {
        int nIdAnnounce = Integer.parseInt( request.getParameter( PARAMETER_ANNOUNCE_ID ) );

        // TODO : removed associated objects

        AnnounceHome.remove( nIdAnnounce );

        // if the operation occurred well, redirects towards the list
        return JSP_REDIRECT_TO_MANAGE_ANNOUNCES;
    }

    /**
     * Return UrlPage Url
     * @return url
     */
    private String getUrlPage( )
    {
        UrlItem url = new UrlItem( JSP_MANAGE_ANNOUNCES );

        return url.getUrl( );
    }

    /**
     * Publish a category
     * 
     * @param request The Http request
     * @param bPublished True to publish the announce, false to unpublish it
     * @return the jsp URL to display the form to manage categories
     */
    public String doPublishAnnounce( HttpServletRequest request, boolean bPublished )
    {
        int nIdAnnounce = Integer.parseInt( request.getParameter( PARAMETER_ANNOUNCE_ID ) );
        Announce announce = AnnounceHome.findByPrimaryKey( nIdAnnounce );
        announce.setDateCreation( new Timestamp( GregorianCalendar.getInstance( ).getTimeInMillis( ) ) );
        announce.setPublished( bPublished );
        AnnounceHome.setPublished( announce );

        // if the operation occurred well, redirects towards the list
        return JSP_REDIRECT_TO_MANAGE_ANNOUNCES;
    }

    /**
     * enables an announce
     * @param request the httpRequest
     * @return template redirection
     */
    public String doEnableAnnounce( HttpServletRequest request )
    {
        int nIdAnnounce = Integer.parseInt( request.getParameter( PARAMETER_ANNOUNCE_ID ) );
        Announce announce = AnnounceHome.findByPrimaryKey( nIdAnnounce );
        announce.setSuspended( false );
        AnnounceHome.setSuspended( announce );

        // if the operation occurred well, redirects towards the list
        return JSP_REDIRECT_TO_MANAGE_ANNOUNCES;
    }

    /**
     * Publish a category
     * 
     * @param request The Http request
     * @return the jsp URL to display the form to manage categories
     */
    public String doSuspendAnnounce( HttpServletRequest request )
    {
        int nIdAnnounce = Integer.parseInt( request.getParameter( PARAMETER_ANNOUNCE_ID ) );
        Announce announce = AnnounceHome.findByPrimaryKey( nIdAnnounce );
        announce.setSuspended( true );
        AnnounceHome.setSuspended( announce );

        // if the operation occurred well, redirects towards the list
        return JSP_REDIRECT_TO_MANAGE_ANNOUNCES;
    }
}
