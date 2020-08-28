/*
 * Copyright (c) 2002-2020, City of Paris
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

import fr.paris.lutece.plugins.announce.business.Sector;
import fr.paris.lutece.plugins.announce.business.SectorHome;
import fr.paris.lutece.plugins.announce.service.SectorResourceIdService;
import fr.paris.lutece.plugins.announce.utils.AnnounceUtils;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage sector features ( manage, create, modify, remove )
 */
public class SectorJspBean extends PluginAdminPageJspBean
{
    /**
     * Right to manage this feature
     */
    public static final String RIGHT_MANAGE_ANNOUNCE = "ANNOUNCE_MANAGEMENT";
    private static final long serialVersionUID = 4236427473368477581L;

    /* parameter */
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_FIELD_ID = "sector_id";
    private static final String PARAMETER_FIELD_LABEL = "sector_label";
    private static final String PARAMETER_FIELD_DESCRIPTION = "sector_description";
    private static final String PARAMETER_FIELD_ANNOUNCES_VALIDATION = "sector_announces_validation";
    private static final String CHECKBOX_ON = "on";
    private static final String PARAMETER_FIELD_ORDER = "sector_order";
    private static final String PARAMETER_TAGS = "tags";

    /* properties */
    private static final String PROPERTY_PAGE_TITLE_MANAGE_FIELDS = "announce.manage_sector.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_FIELD = "announce.create_sector.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_FIELD = "announce.modify_sector.pageTitle";
    private static final String PROPERTY_DEFAULT_LIST_FIELD_PER_PAGE = "announce.sector.itemsPerPage";

    /* templates */
    private static final String TEMPLATE_MANAGE_FIELDS = "admin/plugins/announce/manage_sectors.html";
    private static final String TEMPLATE_CREATE_FIELD = "admin/plugins/announce/create_sector.html";
    private static final String TEMPLATE_MODIFY_FIELD = "admin/plugins/announce/modify_sector.html";

    /* Jsp Definition */
    private static final String JSP_DO_REMOVE_FIELD = "jsp/admin/plugins/announce/DoRemoveSector.jsp";
    private static final String JSP_MANAGE_FIELDS = "jsp/admin/plugins/announce/ManageSectors.jsp";
    private static final String JSP_REDIRECT_TO_MANAGE_FIELDS = "ManageSectors.jsp";

    /* Messages */
    private static final String MESSAGE_CONFIRM_REMOVE_FIELD = "announce.message.confirmRemoveSector";
    private static final String MESSAGE_CANNOT_REMOVE_FIELD = "announce.message.cannotRemoveSector";

    /* Markers */
    private static final String MARK_FIELD = "sector";
    private static final String MARK_LIST_FIELDS = "list_sectors";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_FIELD_ORDER_LIST = "sector_order_list";

    /* Misc */
    private static final String REGEX_ID = "^[\\d]+$";

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
     * Returns the list of sector
     *
     * @param request
     *            The Http request
     * @return the sectors list
     */
    public String getManageSectors( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_FIELDS );

        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_FIELD_PER_PAGE, 50 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, _nDefaultItemsPerPage );

        Collection<Sector> listSectors = SectorHome.findAll( );

        // listSectors = AdminWorkgroupService.getAuthorizedCollection( listSectors, getUser( ) );
        Paginator<Sector> paginator = new Paginator<Sector>( (List<Sector>) listSectors, _nItemsPerPage, getUrlPage( ), PARAMETER_PAGE_INDEX,
                _strCurrentPageIndex );

        Map<String, Object> model = new HashMap<String, Object>( );

        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_LIST_FIELDS, paginator.getPageItems( ) );
        model.put( MARK_FIELD_ORDER_LIST, getSectorOrderList( ) );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_FIELDS, getLocale( ), model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Returns the form to create a sector
     * 
     * @return the html code of the sector form
     * @param request
     *            The Http request
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException
     *             access denied exception
     */
    public String getCreateSector( HttpServletRequest request ) throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( Sector.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, SectorResourceIdService.PERMISSION_CREATE, getUser( ) ) )
        {
            throw new AccessDeniedException( );
        }

        setPageTitleProperty( PROPERTY_PAGE_TITLE_CREATE_FIELD );

        Collection<Sector> listSectors = SectorHome.findAll( );

        HashMap<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_LIST_FIELDS, listSectors );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_FIELD, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Process the data capture form of a new sector
     * 
     * @return The Jsp URL of the process result
     * @param request
     *            The Http Request
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException
     *             access denied exception
     */
    public String doCreateSector( HttpServletRequest request ) throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( Sector.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, SectorResourceIdService.PERMISSION_CREATE, getUser( ) ) )
        {
            throw new AccessDeniedException( );
        }

        String strSectorLabel = request.getParameter( PARAMETER_FIELD_LABEL );
        String strSectorDescription = request.getParameter( PARAMETER_FIELD_DESCRIPTION );
        String strTags = request.getParameter( PARAMETER_TAGS );

        String strAnnouncesValidation = request.getParameter( PARAMETER_FIELD_ANNOUNCES_VALIDATION );

        Sector sector = new Sector( );
        sector.setLabel( strSectorLabel );
        sector.setDescription( strSectorDescription );
        sector.setTags( strTags );

        if ( strAnnouncesValidation == null )
        {
            sector.setAnnouncesValidation( false );
        }
        else
            if ( strAnnouncesValidation.equals( CHECKBOX_ON ) )
            {
                sector.setAnnouncesValidation( true );
            }

        // Mandatory sectors
        if ( ( strSectorLabel == null ) || ( strSectorDescription == null ) || strSectorDescription.equals( "" ) || strSectorLabel.equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        SectorHome.create( sector, getPlugin( ) );

        // if the operation occurred well, redirects towards the list
        return JSP_REDIRECT_TO_MANAGE_FIELDS;
    }

    /**
     * Returns the form to update info about a sector
     * 
     * @return The HTML form to update info
     * @param request
     *            The Http request
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException
     *             access denied exception
     */
    public String getModifySector( HttpServletRequest request ) throws AccessDeniedException
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MODIFY_FIELD );

        Sector sector = getAuthorizedSector( request, SectorResourceIdService.PERMISSION_MODIFY );
        HashMap<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_FIELD, sector );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_FIELD, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Process the change form of a sector
     * 
     * @return The Jsp URL of the process result
     * @param request
     *            The Http request
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException
     *             access denied exception
     */
    public String doModifySector( HttpServletRequest request ) throws AccessDeniedException
    {
        String strSectorLabel = request.getParameter( PARAMETER_FIELD_LABEL );
        String strSectorDescription = request.getParameter( PARAMETER_FIELD_DESCRIPTION );
        String strAnnouncesValidation = request.getParameter( PARAMETER_FIELD_ANNOUNCES_VALIDATION );
        String strTags = request.getParameter( PARAMETER_TAGS );

        // Mandatory sectors
        if ( StringUtils.isEmpty( strSectorLabel ) || StringUtils.isEmpty( strSectorDescription ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        Sector sector = getAuthorizedSector( request, SectorResourceIdService.PERMISSION_MODIFY );

        if ( strAnnouncesValidation == null )
        {
            sector.setAnnouncesValidation( false );
        }
        else
            if ( strAnnouncesValidation.equals( CHECKBOX_ON ) )
            {
                sector.setAnnouncesValidation( true );
            }

        sector.setLabel( strSectorLabel );
        sector.setDescription( strSectorDescription );
        sector.setTags( strTags );
        SectorHome.update( sector, getPlugin( ) );

        // if the operation occurred well, redirects towards the list
        return JSP_REDIRECT_TO_MANAGE_FIELDS;
    }

    /**
     * Manages the removal form of a sector whose identifier is in the http request
     * 
     * @return the html code to confirm
     * @param request
     *            The Http request
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException
     *             access denied exception
     */
    public String getConfirmRemoveSector( HttpServletRequest request ) throws AccessDeniedException
    {
        int nIdSector = Integer.parseInt( request.getParameter( PARAMETER_FIELD_ID ) );
        Sector sector = getAuthorizedSector( request, SectorResourceIdService.PERMISSION_DELETE );

        if ( sector.getNumberCategories( ) == 0 )
        {
            UrlItem url = new UrlItem( JSP_DO_REMOVE_FIELD );
            url.addParameter( PARAMETER_FIELD_ID, nIdSector );

            return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_FIELD, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
        }

        return AdminMessageService.getMessageUrl( request, MESSAGE_CANNOT_REMOVE_FIELD, AdminMessage.TYPE_STOP );
    }

    /**
     * Treats the removal form of a sector
     * 
     * @return the jsp URL to display the form to manage sectors
     * @param request
     *            The Http request
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException
     *             access denied exception
     */
    public String doDeleteSector( HttpServletRequest request ) throws AccessDeniedException
    {
        int nIdSector = Integer.parseInt( request.getParameter( PARAMETER_FIELD_ID ) );

        Sector sector = getAuthorizedSector( request, SectorResourceIdService.PERMISSION_DELETE );
        int nOrder = SectorHome.getOrderById( nIdSector );
        int nNewOrder = SectorHome.getMaxOrderSector( );
        modifySectorOrder( nOrder, nNewOrder, nIdSector );
        SectorHome.remove( sector, getPlugin( ) );

        // if the operation occurred well, redirects towards the list
        return JSP_REDIRECT_TO_MANAGE_FIELDS;
    }

    /**
     * Modifies the order in the list of contactLists
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     */
    public String doModifySectorOrder( HttpServletRequest request )
    {
        int nIdSector = Integer.parseInt( request.getParameter( PARAMETER_FIELD_ID ) );

        int nOrder = SectorHome.getOrderById( nIdSector );
        int nNewOrder = Integer.parseInt( request.getParameter( PARAMETER_FIELD_ORDER ) );
        modifySectorOrder( nOrder, nNewOrder, nIdSector );

        return JSP_REDIRECT_TO_MANAGE_FIELDS;
    }

    /**
     * Builts a list of sequence numbers
     * 
     * @return the list of sequence numbers
     */
    private ReferenceList getSectorOrderList( )
    {
        int nMax = SectorHome.getMaxOrderSector( );
        ReferenceList list = new ReferenceList( );

        for ( int i = 1; i < ( nMax + 1 ); i++ )
        {
            list.addItem( i, Integer.toString( i ) );
        }

        return list;
    }

    /**
     * Modify the place in the list for sector
     * 
     * @param nOrder
     *            the actual place in the list
     * @param nNewOrder
     *            the new place in the list
     * @param nIdSector
     *            the id of the sector
     */
    private void modifySectorOrder( int nOrder, int nNewOrder, int nIdSector )
    {
        if ( nNewOrder < nOrder )
        {
            for ( int i = nOrder - 1; i > ( nNewOrder - 1 ); i-- )
            {
                int nIdSectorOrder = SectorHome.getIdByOrder( i );
                SectorHome.updateOrder( i + 1, nIdSectorOrder );
            }

            SectorHome.updateOrder( nNewOrder, nIdSector );
        }
        else
        {
            for ( int i = nOrder; i < ( nNewOrder + 1 ); i++ )
            {
                int nIdSectorOrder = SectorHome.getIdByOrder( i );
                SectorHome.updateOrder( i - 1, nIdSectorOrder );
            }

            SectorHome.updateOrder( nNewOrder, nIdSector );
        }
    }

    /**
     * Return UrlPage Url
     * 
     * @return url
     */
    private String getUrlPage( )
    {
        UrlItem url = new UrlItem( JSP_MANAGE_FIELDS );

        return url.getUrl( );
    }

    /**
     * Get the authorized Sector
     *
     * @param request
     *            The {@link HttpServletRequest}
     * @param strPermissionType
     *            The type of permission (see {@link SectorResourceIdService} class)
     * @return The sector or null if user have no access
     * @throws AccessDeniedException
     *             If the user is not authorized to access this feature
     */
    private Sector getAuthorizedSector( HttpServletRequest request, String strPermissionType ) throws AccessDeniedException
    {
        String strIdSector = request.getParameter( PARAMETER_FIELD_ID );

        if ( ( strIdSector == null ) || !strIdSector.matches( REGEX_ID ) )
        {
            throw new AccessDeniedException( );
        }

        int nIdSector = Integer.parseInt( strIdSector );
        Sector sector = SectorHome.findByPrimaryKey( nIdSector );

        if ( ( sector == null ) || !RBACService.isAuthorized( Sector.RESOURCE_TYPE, String.valueOf( sector.getId( ) ), strPermissionType, getUser( ) ) )
        {
            throw new AccessDeniedException( );
        }

        return sector;
    }
}
