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

import fr.paris.lutece.plugins.announce.business.Category;
import fr.paris.lutece.plugins.announce.business.CategoryHome;
import fr.paris.lutece.plugins.announce.business.SectorHome;
import fr.paris.lutece.plugins.announce.service.AnnounceService;
import fr.paris.lutece.plugins.announce.service.CategoryResourceIdService;
import fr.paris.lutece.plugins.announce.service.EntryTypeService;
import fr.paris.lutece.plugins.announce.utils.AnnounceUtils;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage category features ( manage,
 * create, modify, remove )
 */
public class CategoryJspBean extends PluginAdminPageJspBean
{
    /**
     * Right to manage this feature
     */
    public static final String RIGHT_MANAGE_ANNOUNCE = "ANNOUNCE_MANAGEMENT";
    private static final long serialVersionUID = 5116073608003926398L;

    /* Misc */
    private static final String REGEX_ID = "^[\\d]+$";
    private static final String ANCHOR_NAME = "entry_list";

    // Parameters
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_CATEGORY_ID = "category_id";
    private static final String PARAMETER_CATEGORY_LABEL = "category_label";
    private static final String PARAMETER_CATEGORY_SECTOR_ID = "category_sector_id";
    private static final String PARAMETER_CATEGORY_ANNOUNCES_VALIDATION = "category_announces_validation";
    private static final String PARAMETER_DISPLAY_PRICE = "display_price";
    private static final String PARAMETER_MAILING_LIST_ID = "mailing_list_id";

    /* properties */
    private static final String PROPERTY_PAGE_TITLE_MANAGE_CATEGORIES = "announce.manage_categories.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_CATEGORY = "announce.create_category.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_CATEGORY = "announce.modify_category.pageTitle";
    private static final String PROPERTY_DEFAULT_LIST_CATEGORY_PER_PAGE = "announce.category.itemsPerPage";
    private static final String PROPERTY_GLOBAL_PARAMETER = "announce.globalParameter";
    private static final String PROPERTY_CREATE_CATEGORY_YES = "announce.create_category_yes";
    private static final String PROPERTY_CREATE_CATEGORY_NO = "announce.create_category_no";
    private static final String PROPERTY_MODIFY_CATEGORY_YES = "announce.modify_category_yes";
    private static final String PROPERTY_MODIFY_CATEGORY_NO = "announce.modify_category_no";
    private static final String PROPERTY_PAGE_TITLE_VIEW_FORM_EXAMPLE = "announce.view_form_example.pageTitle";
    private static final String PROPERTY_NOTHING = "announce.mailing_list.nothing";

    /* templates */
    private static final String TEMPLATE_MANAGE_CATEGORIES = "admin/plugins/announce/manage_categories.html";
    private static final String TEMPLATE_CREATE_CATEGORY = "admin/plugins/announce/create_category.html";
    private static final String TEMPLATE_MODIFY_CATEGORY = "admin/plugins/announce/modify_category.html";

    /* Jsp Definition */
    private static final String JSP_DO_REMOVE_CATEGORY = "jsp/admin/plugins/announce/DoRemoveCategory.jsp";
    private static final String JSP_MANAGE_CATEGORIES = "jsp/admin/plugins/announce/ManageCategories.jsp";
    private static final String JSP_URL_MODIFY = "ModifyCategory.jsp";

    /* Jsp Redirect */
    private static final String JSP_REDIRECT_TO_MANAGE_CATEGORIES = "ManageCategories.jsp";
    private static final String JSP_MODIFY_CATEGORY = "jsp/admin/plugins/announce/ModifyCategory.jsp";

    /* Messages */
    private static final String MESSAGE_CONFIRM_REMOVE_CATEGORY = "announce.message.confirmRemoveCategory";
    private static final String MESSAGE_PLEASE_REMOVE_ANNOUCES = "announce.message.pleaseRemoveAnnounces";
    private static final String MESSAGE_PLEASE_REMOVE_ENTRIES = "announce.message.pleaseRemoveEntries";

    /* Markers */
    private static final String MARK_CATEGORY = "category";
    private static final String MARK_LIST_CATEGORIES = "list_categories";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_LIST_FIELDS = "list_sectors";
    private static final String MARK_LIST_ANNOUNCES_VALIDATION = "list_announces_validation";
    private static final String MARK_ENTRY_LIST = "entry_list";
    private static final String MARK_ENTRY_TYPE_LIST = "entry_type_list";
    private static final String MARK_PLUGIN = "plugin";
    private static final String MARK_MAILING_LIST_LIST = "mailing_list_list";
    private static final String MARK_GROUP_ENTRY_LIST = "entry_group_list";
    private static final String MARK_LIST_ORDER_FIRST_LEVEL = "listOrderFirstLevel";

    /* Variables */
    private AnnounceService _announceService = SpringContextService.getBean( AnnounceService.BEAN_NAME );
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
     * Returns the list of category
     * 
     * @param request The Http request
     * @return the categories list
     */
    public String getManageCategories( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_CATEGORIES );

        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_CATEGORY_PER_PAGE, 50 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        List<Category> listCategories = CategoryHome.findAll( getPlugin( ) );

        //listCategories = AdminWorkgroupService.getAuthorizedCollection( listCategories, getUser(  ) );
        Paginator<Category> paginator = new Paginator<Category>( listCategories, _nItemsPerPage, getUrlPage( ),
                PARAMETER_PAGE_INDEX, _strCurrentPageIndex );

        Map<String, Object> model = new HashMap<String, Object>( );

        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_LIST_CATEGORIES, paginator.getPageItems( ) );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_CATEGORIES, getLocale( ), model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Returns the form to create a category
     * @return the html code of the category form
     * @param request The Http request
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException access
     *             denied exception
     */
    public String getCreateCategory( HttpServletRequest request ) throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( Category.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                CategoryResourceIdService.PERMISSION_CREATE, getUser( ) ) )
        {
            throw new AccessDeniedException( );
        }

        setPageTitleProperty( PROPERTY_PAGE_TITLE_CREATE_CATEGORY );

        ReferenceList refMailingList = new ReferenceList( );
        AdminUser adminUser = getUser( );
        String strNothing = I18nService.getLocalizedString( PROPERTY_NOTHING, request.getLocale( ) );
        refMailingList.addItem( -1, strNothing );
        refMailingList.addAll( AdminMailingListService.getMailingLists( adminUser ) );

        ReferenceList listSectors = SectorHome.findLocaleReferenceList( request.getLocale( ) );

        ReferenceList listAnnouncesValidation = new ReferenceList( );
        listAnnouncesValidation.addItem( 0,
                I18nService.getLocalizedString( PROPERTY_GLOBAL_PARAMETER, request.getLocale( ) ) );
        listAnnouncesValidation.addItem( 1,
                I18nService.getLocalizedString( PROPERTY_CREATE_CATEGORY_YES, request.getLocale( ) ) );
        listAnnouncesValidation.addItem( 2,
                I18nService.getLocalizedString( PROPERTY_CREATE_CATEGORY_NO, request.getLocale( ) ) );

        HashMap<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_LIST_FIELDS, listSectors );
        model.put( MARK_MAILING_LIST_LIST, refMailingList );
        model.put( MARK_LIST_ANNOUNCES_VALIDATION, listAnnouncesValidation );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_CATEGORY, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Process the data capture form of a new category
     * @return The Jsp URL of the process result
     * @param request The Http Request
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException access
     *             denied exception
     */
    public String doCreateCategory( HttpServletRequest request ) throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( Category.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                CategoryResourceIdService.PERMISSION_CREATE, getUser( ) ) )
        {
            throw new AccessDeniedException( );
        }

        String strCategoryLabel = request.getParameter( PARAMETER_CATEGORY_LABEL );
        int nIdSector = Integer.parseInt( request.getParameter( PARAMETER_CATEGORY_SECTOR_ID ) );
        int nAnnouncesValidation = Integer.parseInt( request.getParameter( PARAMETER_CATEGORY_ANNOUNCES_VALIDATION ) );
        String strDisplayPrice = request.getParameter( PARAMETER_DISPLAY_PRICE );
        int nIdMailingList = Integer.parseInt( request.getParameter( PARAMETER_MAILING_LIST_ID ) );

        // Mandatory sectors
        if ( ( strCategoryLabel == null ) || ( nIdSector == 0 ) || strCategoryLabel.equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        Category category = new Category( );
        category.setLabel( strCategoryLabel );
        category.setIdSector( nIdSector );
        category.setIdMailingList( nIdMailingList );
        category.setAnnouncesValidation( nAnnouncesValidation );

        if ( strDisplayPrice != null )
        {
            category.setDisplayPrice( true );
        }
        else
        {
            category.setDisplayPrice( false );
        }

        CategoryHome.create( category, getPlugin( ) );

        // if the operation occurred well, redirects towards the list
        return JSP_REDIRECT_TO_MANAGE_CATEGORIES;
    }

    /**
     * Returns the form to update info about a category
     * @return The HTML form to update info
     * @param request The Http request
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException access
     *             denied exception
     */
    public String getModifyCategory( HttpServletRequest request ) throws AccessDeniedException
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MODIFY_CATEGORY );

        Category category = getAuthorizedCategory( request, CategoryResourceIdService.PERMISSION_MODIFY );

        ReferenceList listSectors = SectorHome.findLocaleReferenceList( request.getLocale( ) );

        ReferenceList refMailingList = new ReferenceList( );
        AdminUser adminUser = getUser( );
        String strNothing = I18nService.getLocalizedString( PROPERTY_NOTHING, request.getLocale( ) );
        refMailingList.addItem( -1, strNothing );
        refMailingList.addAll( AdminMailingListService.getMailingLists( adminUser ) );

        ReferenceList listAnnouncesValidation = new ReferenceList( );
        listAnnouncesValidation.addItem( 0,
                I18nService.getLocalizedString( PROPERTY_GLOBAL_PARAMETER, request.getLocale( ) ) );
        listAnnouncesValidation.addItem( 1,
                I18nService.getLocalizedString( PROPERTY_MODIFY_CATEGORY_YES, request.getLocale( ) ) );
        listAnnouncesValidation.addItem( 2,
                I18nService.getLocalizedString( PROPERTY_MODIFY_CATEGORY_NO, request.getLocale( ) ) );

        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdResource( category.getId( ) );
        entryFilter.setResourceType( Category.RESOURCE_TYPE );
        entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );

        List<Entry> listEntryFirstLevel = EntryHome.getEntryList( entryFilter );
        List<Entry> listEntry = new ArrayList<Entry>( listEntryFirstLevel.size( ) );

        List<Integer> listOrderFirstLevel = new ArrayList<Integer>( listEntryFirstLevel.size( ) );

        entryFilter = new EntryFilter( );
        entryFilter.setIdResource( category.getId( ) );
        entryFilter.setResourceType( Category.RESOURCE_TYPE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        for ( Entry entry : listEntryFirstLevel )
        {
            listEntry.add( entry );
            // If the entry is a group, we add entries associated with this group
            listOrderFirstLevel.add( listEntry.size( ) );

            if ( entry.getEntryType( ).getGroup( ) )
            {
                entryFilter.setIdEntryParent( entry.getIdEntry( ) );

                List<Entry> listEntryGroup = EntryHome.getEntryList( entryFilter );
                entry.setChildren( listEntryGroup );
                listEntry.addAll( listEntryGroup );
            }
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_GROUP_ENTRY_LIST, getRefListGroups( category.getId( ) ) );
        model.put( MARK_ENTRY_TYPE_LIST, EntryTypeService.getInstance( ).getEntryTypeReferenceList( ) );
        model.put( MARK_ENTRY_LIST, listEntry );
        model.put( MARK_LIST_ORDER_FIRST_LEVEL, listOrderFirstLevel );

        UrlItem url = new UrlItem( JSP_URL_MODIFY );
        url.addParameter( PARAMETER_CATEGORY_ID, category.getId( ) );

        model.put( MARK_CATEGORY, category );
        model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( _nItemsPerPage ) );
        model.put( MARK_LIST_FIELDS, listSectors );
        model.put( MARK_MAILING_LIST_LIST, refMailingList );
        model.put( MARK_LIST_ANNOUNCES_VALIDATION, listAnnouncesValidation );
        model.put( MARK_PLUGIN, getPlugin( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_CATEGORY, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Process the change form of a category
     * @return The Jsp URL of the process result
     * @param request The Http request
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException access
     *             denied exception
     */
    public String doModifyCategory( HttpServletRequest request ) throws AccessDeniedException
    {
        String strCategoryLabel = request.getParameter( PARAMETER_CATEGORY_LABEL );
        int nIdSector = Integer.parseInt( request.getParameter( PARAMETER_CATEGORY_SECTOR_ID ) );
        int nAnnouncesValidation = Integer.parseInt( request.getParameter( PARAMETER_CATEGORY_ANNOUNCES_VALIDATION ) );
        String strDisplayPrice = request.getParameter( PARAMETER_DISPLAY_PRICE );
        int nIdMailingList = Integer.parseInt( request.getParameter( PARAMETER_MAILING_LIST_ID ) );

        // Mandatory categories
        if ( StringUtils.isEmpty( strCategoryLabel ) || ( nIdSector == 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        Category category = getAuthorizedCategory( request, CategoryResourceIdService.PERMISSION_MODIFY );
        category.setLabel( strCategoryLabel );
        category.setIdSector( nIdSector );
        category.setAnnouncesValidation( nAnnouncesValidation );
        category.setIdMailingList( nIdMailingList );

        if ( strDisplayPrice != null )
        {
            category.setDisplayPrice( true );
        }
        else
        {
            category.setDisplayPrice( false );
        }

        CategoryHome.update( category, getPlugin( ) );

        // if the operation occurred well, redirects towards the list
        return JSP_REDIRECT_TO_MANAGE_CATEGORIES;
    }

    /**
     * Manages the removal form of a category whose identifier is in the http
     * request
     * @return the html code to confirm
     * @param request The Http request
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException access
     *             denied exception
     */
    public String getConfirmRemoveCategory( HttpServletRequest request ) throws AccessDeniedException
    {
        int nIdCategory = Integer.parseInt( request.getParameter( PARAMETER_CATEGORY_ID ) );
        Category category = getAuthorizedCategory( request, CategoryResourceIdService.PERMISSION_DELETE );

        if ( ( category.getNumberAnnounces( ) == 0 )
                && ( CategoryHome.countEntriesForCategory( category, getPlugin( ) ) == 0 ) )
        {
            UrlItem url = new UrlItem( JSP_DO_REMOVE_CATEGORY );
            url.addParameter( PARAMETER_CATEGORY_ID, nIdCategory );

            return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_CATEGORY, url.getUrl( ),
                    AdminMessage.TYPE_CONFIRMATION );
        }

        if ( category.getNumberAnnounces( ) != 0 )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_PLEASE_REMOVE_ANNOUCES, AdminMessage.TYPE_STOP );
        }

        if ( CategoryHome.countEntriesForCategory( category, getPlugin( ) ) != 0 )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_PLEASE_REMOVE_ENTRIES, AdminMessage.TYPE_STOP );
        }

        return null;
    }

    /**
     * Treats the removal form of a category
     * @return the jsp URL to display the form to manage categories
     * @param request The Http request
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException access
     *             denied exception
     */
    public String doRemoveCategory( HttpServletRequest request ) throws AccessDeniedException
    {
        Category category = getAuthorizedCategory( request, CategoryResourceIdService.PERMISSION_DELETE );
        CategoryHome.remove( category, getPlugin( ) );

        // TODO : remove entries, responses, fields, etc...

        // if the operation occurred well, redirects towards the list
        return JSP_REDIRECT_TO_MANAGE_CATEGORIES;
    }

    /**
     * gets the category form example
     * @param request the httpRequest
     * @return String of template for form example
     */
    public String getExampleForm( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_VIEW_FORM_EXAMPLE );

        int nIdCategory = Integer.parseInt( request.getParameter( PARAMETER_CATEGORY_ID ) );
        Category category = CategoryHome.findByPrimaryKey( nIdCategory, getPlugin( ) );

        return getAdminPage( _announceService.getHtmlAnnounceForm( null, category, getLocale( ), false, request ) );
    }

    /**
     * Return UrlPage Url
     * @return url
     */
    private String getUrlPage( )
    {
        UrlItem url = new UrlItem( JSP_MANAGE_CATEGORIES );

        return url.getUrl( );
    }

    /**
     * Get the authorized category
     * 
     * @param request The {@link HttpServletRequest}
     * @param strPermissionType The type of permission (see
     *            {@link CategoryResourceIdService} class)
     * @return The category or null if user have no access
     */
    private Category getAuthorizedCategory( HttpServletRequest request, String strPermissionType )
            throws AccessDeniedException
    {
        String strIdCategory = request.getParameter( PARAMETER_CATEGORY_ID );

        if ( ( strIdCategory == null ) || !strIdCategory.matches( REGEX_ID ) )
        {
            throw new AccessDeniedException( );
        }

        int nIdCategory = Integer.parseInt( strIdCategory );
        Category category = CategoryHome.findByPrimaryKey( nIdCategory, getPlugin( ) );

        if ( ( category == null )
                || !RBACService.isAuthorized( Category.RESOURCE_TYPE, String.valueOf( category.getId( ) ),
                        strPermissionType, getUser( ) ) )
        {
            throw new AccessDeniedException( );
        }

        return category;
    }

    /**
     * Get the URL to manage categories
     * @param request The request
     * @return The URL to manage categories
     */
    public static String getUrlManageCategories( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_CATEGORIES;
    }

    /**
     * Get the URL to modify a category
     * @param request The request
     * @param nIdCategory The id of the category to modify
     * @return The URL to modify a category
     */
    public static String getUrlModifyCategory( HttpServletRequest request, int nIdCategory )
    {
        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MODIFY_CATEGORY );
        url.addParameter( PARAMETER_CATEGORY_ID, nIdCategory );
        url.setAnchor( ANCHOR_NAME );
        return url.getUrl( );
    }

    /**
     * Get the reference list of groups
     * @param nIdCategory the id of the category
     * @return The reference list of groups of the given category
     */
    private static ReferenceList getRefListGroups( int nIdCategory )
    {
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdResource( nIdCategory );
        entryFilter.setResourceType( Category.RESOURCE_TYPE );
        entryFilter.setIdIsGroup( 1 );

        List<Entry> listEntry = EntryHome.getEntryList( entryFilter );

        ReferenceList refListGroups = new ReferenceList( );

        for ( Entry entry : listEntry )
        {
            refListGroups.addItem( entry.getIdEntry( ), entry.getTitle( ) );
        }

        return refListGroups;
    }
}
