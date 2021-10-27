/*
 * Copyright (c) 2002-2021, City of Paris
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.announce.business.Announce;
import fr.paris.lutece.plugins.announce.business.AnnounceHome;
import fr.paris.lutece.plugins.announce.business.AnnounceNotify;
import fr.paris.lutece.plugins.announce.business.AnnounceNotifyHome;
import fr.paris.lutece.plugins.announce.business.AnnounceSort;
import fr.paris.lutece.plugins.announce.business.Category;
import fr.paris.lutece.plugins.announce.business.CategoryHome;
import fr.paris.lutece.plugins.announce.service.AnnounceResourceIdService;
import fr.paris.lutece.plugins.announce.utils.AnnounceUtils;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.portal.web.util.LocalizedDelegatePaginator;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage announce features ( manage, create, modify, remove )
 */
public class AnnounceJspBean extends PluginAdminPageJspBean
{
    /**
     * Right to manage this feature
     */
    public static final String RIGHT_MANAGE_ANNOUNCE = "ANNOUNCE_MANAGEMENT";
    private static final long serialVersionUID = 6293138267315605660L;

    /* parameter */
    private static final String PARAMETER_ANNOUNCE_ID = "announce_id";
    private static final String SESSION_SORT = "announce.sessionAnnounceSort";

    /* properties */
    private static final String PROPERTY_PAGE_TITLE_MANAGE_ANNOUNCES = "announce.manage_announces.pageTitle";
    private static final String PROPERTY_DEFAULT_LIST_ANNOUNCE_PER_PAGE = "announce.announce.itemsPerPage";
    private static final String PROPERTY_PAGE_TITLE_PREVIEW_ANNOUNCE = "announce.preview_announce.pageTitle";

    /* templates */
    private static final String TEMPLATE_MANAGE_ANNOUNCES = "admin/plugins/announce/manage_announces.html";
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
    private static final String MARK_RESOURCE_HISTORY = "resource_history";
    private static final String MARK_RIGHT_DELETE = "right_delete_announce";
    private static final String MARK_RIGHT_PUBLISH = "right_publish_announce";
    private static final String MARK_RIGHT_SUSPEND = "right_suspend_announce";
    private static final String MARK_RIGHT_WORKFLOW_ACTION = "right_execute_workflow_action";
    private static final String MARK_ENTRY_LIST_GEOLOCATION = "admList_entryTypeGeolocation";

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
     * @param request
     *            The HTTP request
     * @return the announces list
     */
    public String getManageAnnounces( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_ANNOUNCES );

        _strCurrentPageIndex = AbstractPaginator.getPageIndex( request, AbstractPaginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );

        if ( _nDefaultItemsPerPage == 0 )
        {
            _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ANNOUNCE_PER_PAGE, 50 );
        }

        _nItemsPerPage = AbstractPaginator.getItemsPerPage( request, AbstractPaginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, _nDefaultItemsPerPage );

        String strSort = request.getParameter( Parameters.SORTED_ATTRIBUTE_NAME );
        boolean bSortAsc = Boolean.parseBoolean( request.getParameter( Parameters.SORTED_ASC ) );
        AnnounceSort announceSort;

        if ( strSort == null )
        {
            announceSort = (AnnounceSort) request.getSession( ).getAttribute( SESSION_SORT );

            if ( announceSort == null )
            {
                announceSort = AnnounceSort.DEFAULT_SORT;
            }
        }
        else
        {
            announceSort = AnnounceSort.getAnnounceSort( strSort, bSortAsc );
            request.getSession( ).setAttribute( SESSION_SORT, announceSort );
        }

        List<Integer> listIdAnnounces = AnnounceHome.findAll( announceSort );

        Paginator<Integer> paginatorId = new Paginator<>( listIdAnnounces, _nItemsPerPage, StringUtils.EMPTY, AbstractPaginator.PARAMETER_PAGE_INDEX,
                _strCurrentPageIndex );

        User user = getUser( );
        List<Announce> listAnnounces = AnnounceHome.findByListId( paginatorId.getPageItems( ), announceSort );
        boolean bCanExecuteWorkflowAction = false;

        if ( WorkflowService.getInstance( ).isAvailable( ) )
        {
            bCanExecuteWorkflowAction = RBACService.isAuthorized( Announce.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    AnnounceResourceIdService.PERMISSION_EXECUTE_WORKFLOW_ACTION, user );

            for ( Announce announce : listAnnounces )
            {
                announce.setCategory( CategoryHome.findByPrimaryKey( announce.getCategory( ).getId( ) ) );

                if ( bCanExecuteWorkflowAction )
                {
                    announce.setListWorkflowActions( WorkflowService.getInstance( ).getActions( announce.getId( ), Announce.RESOURCE_TYPE,
                            announce.getCategory( ).getIdWorkflow( ), user ) );
                }
            }
        }

        Map<String, Object> model = new HashMap<>( );

        LocalizedDelegatePaginator<Announce> paginator = new LocalizedDelegatePaginator<>( listAnnounces, _nItemsPerPage, getURLManageAnnounces( request ),
                AbstractPaginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex, listIdAnnounces.size( ), getLocale( ) );

        model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( _nItemsPerPage ) );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_ANNOUNCE_LIST, paginator.getPageItems( ) );

        model.put( MARK_RIGHT_DELETE,
                RBACService.isAuthorized( Announce.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, AnnounceResourceIdService.PERMISSION_DELETE, user ) );
        model.put( MARK_RIGHT_PUBLISH,
                RBACService.isAuthorized( Announce.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, AnnounceResourceIdService.PERMISSION_PUBLISH, user ) );
        model.put( MARK_RIGHT_SUSPEND,
                RBACService.isAuthorized( Announce.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, AnnounceResourceIdService.PERMISSION_SUSPEND, user ) );
        model.put( MARK_RIGHT_WORKFLOW_ACTION, bCanExecuteWorkflowAction );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_ANNOUNCES, getLocale( ), model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Get the page to display the preview of an announce
     * 
     * @param request
     *            The request
     * @return The HTML to display
     */
    public String getPreviewAnnounce( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_PREVIEW_ANNOUNCE );

        int nIdAnnounce = Integer.parseInt( request.getParameter( PARAMETER_ANNOUNCE_ID ) );
        Announce announce = AnnounceHome.findByPrimaryKey( nIdAnnounce );
        Collection<Entry> listGeolocalisation = new ArrayList<>( );
        User user = getUser( );

        Collection<Response> listResponses = AnnounceHome.findListResponse( announce.getId( ), false );
        for ( Response response : listResponses )
        {

            if ( response.getEntry( ) != null && response.getEntry( ).getEntryType( ) != null
                    && "announce.entryTypeGeolocation".equals( response.getEntry( ).getEntryType( ).getBeanName( ) ) )
            {
                Entry entry = EntryHome.findByPrimaryKey( response.getEntry( ).getIdEntry( ) );
                for ( Field filed : entry.getFields( ) )
                {

                    if ( response.getField( ) != null && filed.getIdField( ) == response.getField( ).getIdField( ) )
                    {
                        response.setField( filed );
                    }
                }

                boolean bool = true;

                for ( Entry ent : listGeolocalisation )
                {
                    if ( ent.getIdEntry( ) == ( entry.getIdEntry( ) ) )
                    {
                        bool = false;
                    }
                }
                if ( bool )
                {
                    listGeolocalisation.add( entry );
                }
            }

        }

        HashMap<String, Object> model = new HashMap<>( );
        model.put( MARK_ENTRY_LIST_GEOLOCATION, listGeolocalisation );
        model.put( MARK_LIST_RESPONSES, listResponses );
        model.put( MARK_ANNOUNCE, announce );

        Category category = CategoryHome.findByPrimaryKey( announce.getCategory( ).getId( ) );
        announce.setCategory( category );

        if ( ( category.getIdWorkflow( ) > 0 ) && WorkflowService.getInstance( ).isAvailable( ) )
        {
            model.put( MARK_RESOURCE_HISTORY, WorkflowService.getInstance( ).getDisplayDocumentHistory( nIdAnnounce, Announce.RESOURCE_TYPE,
                    category.getIdWorkflow( ), request, getLocale( ), user ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PREVIEW_ANNOUNCE, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Manages the removal form of a announce whose identifier is in the http request
     *
     * @param request
     *            The HTTP request
     * @return the HTML code to confirm
     * @throws AccessDeniedException
     *             Id the user is not authorized to access this feature
     */
    public String getConfirmRemoveAnnounce( HttpServletRequest request ) throws AccessDeniedException
    {
        String strAnnounceId = request.getParameter( PARAMETER_ANNOUNCE_ID );
        User user = getUser( );

        if ( !RBACService.isAuthorized( Announce.RESOURCE_TYPE, strAnnounceId, AnnounceResourceIdService.PERMISSION_DELETE, user ) )
        {
            throw new AccessDeniedException(
                    "User '" + user.getFirstName( ) + " " + user.getLastName( ) + "' is not authorized to remove announce with id " + strAnnounceId );
        }

        int nIdAnnounce = Integer.parseInt( strAnnounceId );

        UrlItem url = new UrlItem( JSP_DO_REMOVE_ANNOUNCE );
        url.addParameter( PARAMETER_ANNOUNCE_ID, nIdAnnounce );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_ANNOUNCE, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Treats the removal form of a announce
     *
     * @param request
     *            The HTTP request
     * @return the JSP URL to display the form to manage announces
     * @throws AccessDeniedException
     *             Id the user is not authorized to access this feature
     */
    public String doRemoveAnnounce( HttpServletRequest request ) throws AccessDeniedException
    {
        String strAnnounceId = request.getParameter( PARAMETER_ANNOUNCE_ID );
        User user = getUser( );

        if ( !RBACService.isAuthorized( Announce.RESOURCE_TYPE, strAnnounceId, AnnounceResourceIdService.PERMISSION_DELETE, user ) )
        {
            throw new AccessDeniedException(
                    "User '" + user.getFirstName( ) + " " + user.getLastName( ) + "' is not authorized to remove announce with id " + strAnnounceId );
        }

        int nIdAnnounce = Integer.parseInt( strAnnounceId );

        AnnounceHome.remove( nIdAnnounce );

        // if the operation occurred well, redirects towards the list
        return JSP_REDIRECT_TO_MANAGE_ANNOUNCES;
    }

    /**
     * Publish a category
     *
     * @param request
     *            The Http request
     * @param bPublished
     *            True to publish the announce, false to unpublish it
     * @return the jsp URL to display the form to manage categories
     * @throws AccessDeniedException
     *             Id the user is not authorized to access this feature
     */
    public String doPublishAnnounce( HttpServletRequest request, boolean bPublished ) throws AccessDeniedException
    {
        String strAnnounceId = request.getParameter( PARAMETER_ANNOUNCE_ID );
        User user = getUser( );

        if ( !RBACService.isAuthorized( Announce.RESOURCE_TYPE, strAnnounceId, AnnounceResourceIdService.PERMISSION_PUBLISH, user ) )
        {
            throw new AccessDeniedException( "User '" + user.getFirstName( ) + " " + user.getLastName( )
                    + "' is not authorized to publish/unpublish announce with id " + strAnnounceId );
        }

        if ( StringUtils.isNumeric( strAnnounceId ) )
        {
            int nIdAnnounce = Integer.parseInt( strAnnounceId );
            Announce announce = AnnounceHome.findByPrimaryKey( nIdAnnounce );
            if ( announce.getDatePublication( ).getTime( ) == new Timestamp( 0 ).getTime( ) )
            {
                AnnounceNotify announceNotify = new AnnounceNotify( );
                announceNotify.setIdAnnounce( announce.getId( ) );
                AnnounceNotifyHome.create( announceNotify );
            }
            announce.setDateCreation( new Timestamp( Calendar.getInstance( ).getTimeInMillis( ) ) );
            announce.setPublished( bPublished );
            AnnounceHome.setPublished( announce );
        }
        // if the operation occurred well, redirects towards the list
        return JSP_REDIRECT_TO_MANAGE_ANNOUNCES;
    }

    /**
     * enables an announce
     * 
     * @param request
     *            the httpRequest
     * @return template redirection
     * @throws AccessDeniedException
     *             Id the user is not authorized to access this feature
     */
    public String doEnableAnnounce( HttpServletRequest request ) throws AccessDeniedException
    {
        String strAnnounceId = request.getParameter( PARAMETER_ANNOUNCE_ID );
        User user = getUser( );

        if ( !RBACService.isAuthorized( Announce.RESOURCE_TYPE, strAnnounceId, AnnounceResourceIdService.PERMISSION_SUSPEND, user ) )
        {
            throw new AccessDeniedException(
                    "User '" + user.getFirstName( ) + " " + user.getLastName( ) + "' is not authorized to enable/disable announce with id " + strAnnounceId );
        }

        int nIdAnnounce = Integer.parseInt( strAnnounceId );
        Announce announce = AnnounceHome.findByPrimaryKey( nIdAnnounce );
        announce.setSuspended( false );
        AnnounceHome.setSuspended( announce );

        // if the operation occurred well, redirects towards the list
        return JSP_REDIRECT_TO_MANAGE_ANNOUNCES;
    }

    /**
     * Publish a category
     *
     * @param request
     *            The HTTP request
     * @return the JSP URL to display the form to manage categories
     * @throws AccessDeniedException
     *             Id the user is not authorized to access this feature
     */
    public String doSuspendAnnounce( HttpServletRequest request ) throws AccessDeniedException
    {
        String strAnnounceId = request.getParameter( PARAMETER_ANNOUNCE_ID );
        User user = getUser( );

        if ( !RBACService.isAuthorized( Announce.RESOURCE_TYPE, strAnnounceId, AnnounceResourceIdService.PERMISSION_SUSPEND, user ) )
        {
            throw new AccessDeniedException(
                    "User '" + user.getFirstName( ) + " " + user.getLastName( ) + "' is not authorized to enable/disable announce with id " + strAnnounceId );
        }

        int nIdAnnounce = Integer.parseInt( strAnnounceId );
        Announce announce = AnnounceHome.findByPrimaryKey( nIdAnnounce );
        announce.setSuspended( true );
        AnnounceHome.setSuspended( announce );

        // if the operation occurred well, redirects towards the list
        return JSP_REDIRECT_TO_MANAGE_ANNOUNCES;
    }

    /**
     * Get the URL to manage announces
     * 
     * @param request
     *            The request
     * @return The URL to manage announces.
     */
    public static String getURLManageAnnounces( HttpServletRequest request )
    {
        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_ANNOUNCES );

        return url.getUrl( );
    }
}
