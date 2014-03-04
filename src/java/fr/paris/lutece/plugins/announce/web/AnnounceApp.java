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
import fr.paris.lutece.plugins.announce.business.AnnounceDTO;
import fr.paris.lutece.plugins.announce.business.AnnounceHome;
import fr.paris.lutece.plugins.announce.business.Category;
import fr.paris.lutece.plugins.announce.business.CategoryHome;
import fr.paris.lutece.plugins.announce.business.Sector;
import fr.paris.lutece.plugins.announce.business.SectorHome;
import fr.paris.lutece.plugins.announce.service.AnnouncePlugin;
import fr.paris.lutece.plugins.announce.service.AnnounceService;
import fr.paris.lutece.plugins.announce.service.announcesearch.AnnounceSearchService;
import fr.paris.lutece.plugins.announce.service.upload.AnnounceAsynchronousUploadHandler;
import fr.paris.lutece.plugins.announce.utils.AnnounceUtils;
import fr.paris.lutece.plugins.announce.utils.JSONUtils;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.mailinglist.Recipient;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.portal.web.util.LocalizedDelegatePaginator;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.file.FileUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * This class manages Announce page.
 */
public class AnnounceApp implements XPageApplication
{
    private static final long serialVersionUID = 3586318619582357870L;
    private static final String PARAMETER_USERNAME = "username";
    private static final String PATTERN_DATE = "dd/MM/yy";

    //Jsp redirections
    private static final String JSP_PORTAL = "jsp/site/Portal.jsp";

    // Parameters
    private static final String PARAMETER_FORM_SEND = "form_send";
    private static final String PARAMETER_PAGE = "page";
    private static final String PARAMETER_CATEGORY_ID = "category_id";
    private static final String PARAMETER_TITLE_ANNOUNCE = "title_announce";
    private static final String PARAMETER_DESCRIPTION_ANNOUNCE = "description_announce";
    private static final String PARAMETER_PRICE_ANNOUNCE = "price_announce";
    private static final String PARAMETER_CONTACT_INFORMATION = "contact_information";
    private static final String PARAMETER_CONFIRM_REMOVE_ANNOUNCE = "confirm_remove";
    private static final String PARAMETER_CONFIRM_SUSPEND_ANNOUNCE = "confirm_suspend";
    private static final String PARAMETER_ANNOUNCE_ID = "announce_id";
    private static final String PARAMETER_KEYWORDS = "keywords";
    private static final String PARAMETER_DATE_MIN = "date_min";
    private static final String PARAMETER_DATE_MAX = "date_max";
    private static final String PARAMETER_ENTRY_VALUE_ID = "entry_value_id";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_TAGS = "tags";
    private static final String PARAMETER_ID_ENTRY = "id_entry";
    private static final String PARAMETER_FIELD_INDEX = "field_index";
    private static final String PARAMETER_ACTION_ADDNEW = "addnew";

    // Actions
    private static final String ACTION_VIEW_ANNOUNCE = "view_announce";
    private static final String ACTION_VIEW_ANNOUNCES = "view_announces";
    private static final String ACTION_CATEGORY_ANNOUNCES = "category_announces";
    private static final String ACTION_MY_ANNOUNCES = "my_announces";
    private static final String ACTION_MODIFY_ANNOUNCE = "modify_announce";
    private static final String ACTION_DELETE_ANNOUNCE = "delete_announce";
    private static final String ACTION_SUSPEND_ANNOUNCE_BY_USER = "suspend_by_user";
    private static final String ACTION_ENABLE_ANNOUNCE_BY_USER = "enable_by_user";
    private static final String ACTION_SEARCH = "search";
    private static final String ACTION_DOWNLOAD = "download";

    // Validation flags
    private static final int PARAMETER_ANNOUNCES_VALIDATION_GLOBAL_PARAMETERS = 0;
    private static final int PARAMETER_ANNOUNCES_VALIDATION_YES = 1;
    private static final int PARAMETER_ANNOUNCES_VALIDATION_NO = 2;

    //properties
    private static final String PROPERTY_NOT_AUTHORIZED = "announce.messages.notAuthorized";
    private static final String PROPERTY_QUOTA_EXCEEDED = "announce.messages.quotaExceeded";
    private static final String PROPERTY_REFUSED_ACCESS = "announce.messages.refusedAccess";
    private static final String PROPERTY_PAGE_TITLE = "announce.page_announce.pageTitle";
    private static final String PROPERTY_PAGE_PATH = "announce.page_announce.pagePathLabel";
    private static final String PROPERTY_CONFIRM_REMOVE_ANNOUNCE = "announce.messages.confirmRemoveAnnounce";
    private static final String PROPERTY_CONFIRM_SUSPEND_ANNOUNCE = "announce.messages.confirmSuspendAnnounce";
    private static final String PROPERTY_PAGE_TITLE_SEARCH_RESULTS = "announce.search_results.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_ANNOUNCE = "announce.modify_announce.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MY_ANNOUNCES = "announce.my_announces.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_ANNOUNCE = "announce.create_announce.pageTitle";
    private static final String PROPERTY_DEFAULT_FRONT_LIST_ANNOUNCE_PER_PAGE = "announce.front.announce.defaultItemsPerPage";
    private static final String PROPERTY_WEBMASTER_EMAIL = "email.webmaster";
    private static final String PROPERTY_WEBMASTER_NAME = "lutece.name";
    private static final String PROPERTY_ANNOUNCE_NOTIFY_SUBJECT = "announce.notification.subject";
    private static final String PROPERTY_PROD_URL = "lutece.prod.url";
    private static final String PROPERTY_MAX_AMOUNT_ANNOUNCE = "announce.announce.qty.max";

    //Templates
    private static final String TEMPLATE_PAGE_CREATE_ANNOUNCE_STEP_CATEGORY = "skin/plugins/announce/create_announce_step_category.html";
    private static final String TEMPLATE_PAGE_CREATE_ANNOUNCE_STEP_FORM = "skin/plugins/announce/create_announce_step_form.html";
    private static final String TEMPLATE_VIEW_ANNOUNCE = "skin/plugins/announce/view_announce.html";
    private static final String TEMPLATE_VIEW_ANNOUNCES = "skin/plugins/announce/view_announces.html";
    private static final String TEMPLATE_MY_ANNOUNCES = "skin/plugins/announce/my_announces.html";
    private static final String TEMPLATE_MODIFY_ANNOUNCE = "skin/plugins/announce/modify_announce.html";
    private static final String TEMPLATE_LIST_ANNOUNCES = "skin/plugins/announce/list_announces.html";
    private static final String TEMPLATE_ANNOUNCE_NOTIFY_MESSAGE = "skin/plugins/announce/announce_notify_message.html";
    private static final String TEMPLATE_ANNOUNCE_FRAMESET = "skin/plugins/announce/announce_frameset.html";

    //Markers
    private static final String MARK_LIST_FIELDS = "list_sectors";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_CATEGORY = "category";
    private static final String MARK_SECTOR = "sector";
    private static final String MARK_ANNOUNCES_LIST = "announces_list";
    private static final String MARK_ANNOUNCES_PUBLISHED_AMOUNT = "announce_qty";
    private static final String MARK_CONTACT_INFORMATION = "contact_information";
    private static final String MARK_LIST_RESPONSES = "list_responses";
    private static final String MARK_USER = "user";
    private static final String MARK_CONTENT = "content";
    private static final String FULL_URL = "fullurl";
    private static final String MARK_FILTER_SEARCHED_KEYWORDS = "filter_searched_keywords";
    private static final String MARK_FILTER_SEARCHED_CATEGORY = "filter_searched_category";
    private static final String MARK_FILTER_DATE_MIN = "filter_date_min";
    private static final String MARK_FILTER_DATE_MAX = "filter_date_max";
    private static final String MARK_ANNOUNCE = "announce";
    private static final String MARK_ANNOUNCE_OWNER = "owner";
    private static final String MARK_ALLOW_ACCESS = "allow_access";
    private static final String MARK_USER_IS_AUTHOR = "user_is_author";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_MODERATED = "moderated";
    private static final String MARK_PROD_URL = "prod_url";
    private static final String MARK_FORM_HTML = "form_html";
    private static final String MARK_LIST_ERRORS = "list_errors";

    // Session keys
    private static final String SESSION_ATTRIBUTE_MY_ANNOUNCES_ITEMS_PER_PAGE = "announce.myAnnouncesItemsPerPage";

    //defaults
    private static final String DEFAULT_PAGE_INDEX = "1";

    // private fields
    private AnnounceService _announceService = SpringContextService.getBean( AnnounceService.BEAN_NAME );
    private Plugin _plugin;
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;

    /**
     * {@inheritDoc}
     */
    @Override
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin ) throws SiteMessageException
    {
        /* Global variables for this App */
        String strPluginName = request.getParameter( PARAMETER_PAGE );
        String strAction = request.getParameter( MVCUtils.PARAMETER_ACTION );
        _plugin = PluginService.getPlugin( strPluginName );

        XPage page = new XPage( );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_PAGE_PATH, request.getLocale( ) ) );

        if ( strAction != null )
        {
            if ( strAction.equals( PARAMETER_ACTION_ADDNEW ) )
            {
                page.setTitle( I18nService.getLocalizedString( PROPERTY_PAGE_TITLE_CREATE_ANNOUNCE, request.getLocale( ) ) );
                page.setContent( getCreateAnnounce( request ) );
            }
            else if ( strAction.equals( ACTION_VIEW_ANNOUNCE ) )
            {
                int nIdAnnounce = Integer.parseInt( request.getParameter( PARAMETER_ANNOUNCE_ID ) );
                Announce announce = AnnounceHome.findByPrimaryKey( nIdAnnounce );
                page.setTitle( announce.getTitle( ) );
                page.setContent( getViewAnnounce( request, announce ) );
            }
            else if ( strAction.equals( ACTION_VIEW_ANNOUNCES ) )
            {
                String strUserName = request.getParameter( PARAMETER_USERNAME );
                page.setContent( getViewUserAnnounces( request, strUserName ) );
            }
            else if ( strAction.equals( ACTION_CATEGORY_ANNOUNCES ) )
            {
                int nIdCategory = Integer.parseInt( request.getParameter( PARAMETER_CATEGORY_ID ) );
                Category category = CategoryHome.findByPrimaryKey( nIdCategory, _plugin );
                page.setTitle( category.getLabel( ) );
                page.setContent( getPublishedAnnouncesForCategory( request, category ) );
            }
            else if ( strAction.equals( ACTION_MY_ANNOUNCES ) )
            {
                page.setTitle( I18nService.getLocalizedString( PROPERTY_PAGE_TITLE_MY_ANNOUNCES, request.getLocale( ) ) );
                page.setContent( getManageUserAnnounces( request ) );
            }
            else if ( strAction.equals( ACTION_MODIFY_ANNOUNCE ) )
            {
                page.setTitle( I18nService.getLocalizedString( PROPERTY_PAGE_TITLE_MODIFY_ANNOUNCE, request.getLocale( ) ) );
                page.setContent( getModifyAnnounce( request ) );
            }
            else if ( strAction.equals( ACTION_DELETE_ANNOUNCE ) )
            {
                page.setTitle( I18nService.getLocalizedString( PROPERTY_PAGE_TITLE, request.getLocale( ) ) );
                page.setContent( getDeleteAnnounce( request ) );
            }
            else if ( strAction.equals( ACTION_SUSPEND_ANNOUNCE_BY_USER ) )
            {
                page.setTitle( I18nService.getLocalizedString( PROPERTY_PAGE_TITLE, request.getLocale( ) ) );
                page.setContent( getSuspendAnnounceByUser( request ) );
            }
            else if ( strAction.equals( ACTION_ENABLE_ANNOUNCE_BY_USER ) )
            {
                page.setTitle( I18nService.getLocalizedString( PROPERTY_PAGE_TITLE, request.getLocale( ) ) );
                page.setContent( enableAnnounceByUser( request ) );
            }
            else if ( strAction.equals( ACTION_SEARCH ) )
            {
                page.setTitle( I18nService.getLocalizedString( PROPERTY_PAGE_TITLE_SEARCH_RESULTS, request.getLocale( ) ) );
                page.setContent( getSearchAnnounces( request ) );
            }
            else if ( strAction.equals( ACTION_DOWNLOAD ) )
            {
                getFileDownload( request );
            }
            else
            {
                page.setTitle( I18nService.getLocalizedString( PROPERTY_PAGE_TITLE, request.getLocale( ) ) );
                page.setContent( getDefaultPage( request ) );
            }
        }
        else
        {
            page.setTitle( I18nService.getLocalizedString( PROPERTY_PAGE_TITLE, request.getLocale( ) ) );
            page.setContent( getDefaultPage( request ) );
        }

        /* model and template declarations */
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_LIST_FIELDS, getSectorList( ) );
        model.put( MARK_LOCALE, request.getLocale( ) );
        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
        model.put( MARK_USER, user );
        model.put( MARK_CONTENT, page.getContent( ) );
        HtmlTemplate template = AppTemplateService
                .getTemplate( TEMPLATE_ANNOUNCE_FRAMESET, request.getLocale( ), model );
        page.setContent( template.getHtml( ) );
        return page;
    }

    /**
     * gets the file for download
     * @param request httpRequest
     */
    public void getFileDownload( HttpServletRequest request )
    {
        HttpServletResponse response = LocalVariables.getResponse( );

        int nIdEntryValue = Integer.parseInt( request.getParameter( PARAMETER_ENTRY_VALUE_ID ) );
        Response entryValue = ResponseHome.findByPrimaryKey( nIdEntryValue );

        if ( entryValue.getFile( ) != null )
        {
            entryValue.setFile( FileHome.findByPrimaryKey( entryValue.getFile( ).getIdFile( ) ) );
            String strFileName = entryValue.getFile( ).getTitle( );

            // ----------------------------------------------------------------------- 
            AnnounceUtils.addHeaderResponse( request, response, strFileName );

            response.setContentType( entryValue.getFile( ).getMimeType( ) );

            PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( entryValue.getFile( ).getPhysicalFile( )
                    .getIdPhysicalFile( ) );
            byte[] bResult = physicalFile.getValue( );

            try
            {
                response.setContentLength( bResult.length );
                response.getOutputStream( ).write( bResult );
                response.getOutputStream( ).close( );
            }
            catch ( IOException e )
            {
                AppLogService.error( e );
            }
        }
    }

    /**
     * Get the default page to display
     * @param request The request
     * @param model The model
     * @return The HTML content to display
     */
    private String getDefaultPage( HttpServletRequest request )
    {
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, DEFAULT_PAGE_INDEX );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_FRONT_LIST_ANNOUNCE_PER_PAGE, 10 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        List<Integer> listIdAnnounces = AnnounceHome.findAllPublishedId( );

        LocalizedPaginator<Integer> paginatorId = new LocalizedPaginator<Integer>( listIdAnnounces, _nItemsPerPage,
                StringUtils.EMPTY, PARAMETER_PAGE_INDEX, _strCurrentPageIndex, request.getLocale( ) );

        List<Announce> listAnnounces = AnnounceHome.findByListId( paginatorId.getPageItems( ) );

        LocalizedDelegatePaginator<Announce> paginator = new LocalizedDelegatePaginator<Announce>( listAnnounces,
                _nItemsPerPage, JSP_PORTAL + "?" + PARAMETER_PAGE + "=" + AnnounceUtils.PARAMETER_PAGE_ANNOUNCE,
                PARAMETER_PAGE_INDEX, _strCurrentPageIndex, listIdAnnounces.size( ), request.getLocale( ) );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( _nItemsPerPage ) );
        model.put( MARK_PAGINATOR, paginator );

        for ( Announce announce : paginator.getPageItems( ) )
        {
            announce.setListIdImageResponse( AnnounceHome.findListIdImageResponse( announce.getId( ) ) );
        }

        model.put( MARK_ANNOUNCES_LIST, paginator.getPageItems( ) );
        model.put( MARK_LIST_FIELDS, getSectorList( ) );
        model.put( MARK_LOCALE, request.getLocale( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_LIST_ANNOUNCES, request.getLocale( ), model );

        return template.getHtml( );
    }

    /**
     * Get the list of published announces of a category
     * @param request The request
     * @param category The category
     * @param model The model
     * @return The HTML content to display
     */
    private String getPublishedAnnouncesForCategory( HttpServletRequest request, Category category )
    {
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, DEFAULT_PAGE_INDEX );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_FRONT_LIST_ANNOUNCE_PER_PAGE, 10 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        List<Integer> listIdAnnounces = AnnounceHome.getPublishedAnnouncesForCategory( category );
        Paginator<Integer> paginatorId = new Paginator<Integer>( listIdAnnounces, _nItemsPerPage, StringUtils.EMPTY,
                PARAMETER_PAGE_INDEX, _strCurrentPageIndex );

        List<Announce> listAnnounces = AnnounceHome.findByListId( paginatorId.getPageItems( ) );

        LocalizedDelegatePaginator<Announce> paginator = new LocalizedDelegatePaginator<Announce>( listAnnounces,
                _nItemsPerPage, JSP_PORTAL + "?" + PARAMETER_PAGE + "=" + AnnounceUtils.PARAMETER_PAGE_ANNOUNCE + "&"
                        + MVCUtils.PARAMETER_ACTION + "=" + ACTION_CATEGORY_ANNOUNCES + "&" + PARAMETER_CATEGORY_ID
                        + "=" + category.getId( ), PARAMETER_PAGE_INDEX, _strCurrentPageIndex, listIdAnnounces.size( ),
                request.getLocale( ) );

        for ( Announce announce : paginator.getPageItems( ) )
        {
            announce.setListIdImageResponse( AnnounceHome.findListIdImageResponse( announce.getId( ) ) );
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( _nItemsPerPage ) );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_LIST_FIELDS, getSectorList( ) );
        model.put( MARK_LOCALE, request.getLocale( ) );

        model.put( MARK_ANNOUNCES_LIST, paginator.getPageItems( ) );
        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
        model.put( MARK_USER, user );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_LIST_ANNOUNCES, request.getLocale( ), model );

        return template.getHtml( );
    }

    /**
     * Get the page to create an announce
     * @param request The request
     * @return The HTML content to display
     * @throws SiteMessageException
     */
    private String getCreateAnnounce( HttpServletRequest request ) throws SiteMessageException
    {
        LuteceUser user = getLuteceUserAuthentication( request );

        String strCategoryId = request.getParameter( PARAMETER_CATEGORY_ID );
        String strFormSend = request.getParameter( PARAMETER_FORM_SEND );
        HtmlTemplate template;
        Map<String, Object> model = new HashMap<String, Object>( );

        /* CATEOGRY */
        if ( ( strCategoryId != null ) && ( Integer.parseInt( strCategoryId ) != 0 ) )
        {
            Category category = CategoryHome.findByPrimaryKey( Integer.parseInt( strCategoryId ), _plugin );
            Sector sector = SectorHome.findByPrimaryKey( category.getIdSector( ) );
            Announce announce = null;
            /* FORM */
            if ( strFormSend != null )
            {
                announce = new Announce( );
                model.put( MARK_CATEGORY, category );
                List<GenericAttributeError> listErrors = doCreateAnnounce( request, sector, category, announce, user );
                if ( listErrors == null || listErrors.size( ) == 0 )
                {
                    UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + AppPathService.getPortalUrl( ) );
                    urlItem.addParameter( MVCUtils.PARAMETER_ACTION, ACTION_VIEW_ANNOUNCE );
                    urlItem.addParameter( PARAMETER_PAGE, AnnounceUtils.PARAMETER_PAGE_ANNOUNCE );
                    urlItem.addParameter( PARAMETER_ANNOUNCE_ID, announce.getId( ) );
                    try
                    {
                        LocalVariables.getResponse( ).sendRedirect( urlItem.getUrl( ) );
                    }
                    catch ( IOException e )
                    {
                        AppLogService.error( e.getMessage( ), e );
                    }
                }
                model.put( MARK_LIST_ERRORS, listErrors );
            }
            else
            {
                AnnounceAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ).getId( ) );
            }

            model.put( MARK_ANNOUNCE, announce );
            model.put( MARK_CONTACT_INFORMATION, user.getUserInfo( LuteceUser.BUSINESS_INFO_ONLINE_EMAIL ) );

            return getAnnounceFormHtml( request, announce, category, request.getLocale( ), model );
        }

        AnnounceAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ).getId( ) );

        Collection<Announce> listAnnounces = AnnounceHome.getAnnouncesForUser( user );

        if ( listAnnounces.size( ) < AppPropertiesService.getPropertyInt( PROPERTY_MAX_AMOUNT_ANNOUNCE, 20 ) )
        {
            template = AppTemplateService.getTemplate( TEMPLATE_PAGE_CREATE_ANNOUNCE_STEP_CATEGORY,
                    request.getLocale( ), model );
        }
        else
        {
            SiteMessageService.setMessage( request, PROPERTY_QUOTA_EXCEEDED, SiteMessage.TYPE_STOP );

            return null;
        }

        return template.getHtml( );
    }

    /**
     * Do create an announce
     * @param request The request
     * @param sector The sector
     * @param category The category
     * @param announce The announce
     * @param user The user
     * @return The list of error, or null if no error was found and if the
     *         announce was created
     * @throws SiteMessageException
     */
    private List<GenericAttributeError> doCreateAnnounce( HttpServletRequest request, Sector sector, Category category,
            Announce announce, LuteceUser user ) throws SiteMessageException
    {
        String strTitleAnnounce = request.getParameter( PARAMETER_TITLE_ANNOUNCE );
        String strDescriptionAnnounce = request.getParameter( PARAMETER_DESCRIPTION_ANNOUNCE );
        String strContactInformation = request.getParameter( PARAMETER_CONTACT_INFORMATION );
        String strTags = request.getParameter( PARAMETER_TAGS );
        String strPriceAnnounce = ( request.getParameter( PARAMETER_PRICE_ANNOUNCE ) == null ) ? StringUtils.EMPTY
                : request.getParameter( PARAMETER_PRICE_ANNOUNCE );

        if ( StringUtils.isEmpty( strTitleAnnounce ) || StringUtils.isEmpty( strDescriptionAnnounce )
                || StringUtils.isEmpty( strContactInformation ) )
        {
            SiteMessageService.setMessage( request, Messages.MANDATORY_FIELDS, SiteMessage.TYPE_STOP );

            return null;
        }

        switch ( category.getAnnouncesValidation( ) )
        {
        case PARAMETER_ANNOUNCES_VALIDATION_YES:
            announce.setPublished( false );

            break;

        case PARAMETER_ANNOUNCES_VALIDATION_NO:
            announce.setPublished( true );

            break;
        case PARAMETER_ANNOUNCES_VALIDATION_GLOBAL_PARAMETERS:
        default:
            announce.setPublished( !sector.getAnnouncesValidation( ) );
        }

        announce.setCategory( category );
        announce.setTitle( strTitleAnnounce );
        announce.setDescription( strDescriptionAnnounce );
        announce.setPrice( strPriceAnnounce );
        announce.setContactInformation( strContactInformation );
        announce.setUserName( user.getName( ) );
        announce.setTags( strTags );

        EntryFilter filter = new EntryFilter( );
        filter.setIdResource( category.getId( ) );
        filter.setResourceType( Category.RESOURCE_TYPE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        filter.setIdIsComment( EntryFilter.FILTER_FALSE );

        List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );
        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );

        AnnounceDTO announceDTO = new AnnounceDTO( announce );

        for ( Entry entry : listEntryFirstLevel )
        {
            listFormErrors.addAll( _announceService.getResponseEntry( request, entry.getIdEntry( ),
                    request.getLocale( ), announceDTO ) );
        }

        _announceService.convertMapResponseToList( announceDTO );
        announce.setListResponse( announceDTO.getListResponse( ) );
        if ( listFormErrors.size( ) > 0 )
        {
            return listFormErrors;
        }

        announce.setHasPictures( false );
        for ( Response response : announceDTO.getListResponse( ) )
        {
            if ( response.getFile( ) != null && FileUtil.hasImageExtension( response.getFile( ).getTitle( ) ) )
            {
                announce.setHasPictures( true );
                break;
            }
        }

        AnnounceHome.create( announce );

        for ( Response response : announceDTO.getListResponse( ) )
        {
            ResponseHome.create( response );
            AnnounceHome.insertAppointmentResponse( announce.getId( ), response.getIdResponse( ),
                    response.getFile( ) != null && FileUtil.hasImageExtension( response.getFile( ).getTitle( ) ) );
        }

        // send mail notification only if announce is not published
        if ( !announce.getPublished( ) )
        {
            sendAnnounceNotification( request, announce );
        }

        AnnounceAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ).getId( ) );
        return null;
    }

    /**
     * Get the page to create an announce
     * @param request The request
     * @param model The model
     * @return The HTML content to display
     * @throws SiteMessageException If a site message needs to be displayed
     */
    private String getModifyAnnounce( HttpServletRequest request ) throws SiteMessageException
    {
        LuteceUser user = getLuteceUserAuthentication( request );
        int nIdAnnounce = Integer.parseInt( request.getParameter( PARAMETER_ANNOUNCE_ID ) );
        Announce announce = getAuthorizedAnnounce( nIdAnnounce, user, request );

        String strFormSend = request.getParameter( PARAMETER_FORM_SEND );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_LIST_FIELDS, getSectorList( ) );
        model.put( MARK_LOCALE, request.getLocale( ) );

        if ( strFormSend != null )
        {
            List<GenericAttributeError> listErrors = doModifyAnnounce( request, announce );
            if ( listErrors == null )
            {
                return getViewAnnounce( request, announce );
            }
            model.put( MARK_LIST_ERRORS, listErrors );
        }
        else
        {
            AnnounceAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ).getId( ) );
        }

        Category category = CategoryHome.findByPrimaryKey( announce.getCategory( ).getId( ), _plugin );
        Sector sector = SectorHome.findByPrimaryKey( category.getIdSector( ) );
        boolean bModerated = true;

        // unpublish announce if category moderation is on
        switch ( category.getAnnouncesValidation( ) )
        {
        case PARAMETER_ANNOUNCES_VALIDATION_YES:
            bModerated = true;
            break;
        case PARAMETER_ANNOUNCES_VALIDATION_NO:
            bModerated = false;
            break;
        case PARAMETER_ANNOUNCES_VALIDATION_GLOBAL_PARAMETERS:
        default:
            bModerated = sector.getAnnouncesValidation( );
        }

        model.put( MARK_MODERATED, bModerated );
        model.put( MARK_ANNOUNCE, announce );

        return getAnnounceFormHtml( request, announce, category, request.getLocale( ), model );
    }

    /**
     * Do modify an announce
     * @param request The request
     * @param announce The announce
     * @return The list of errors, or null if no error occurred
     * @throws SiteMessageException If a site message needs to be displayed
     */
    private List<GenericAttributeError> doModifyAnnounce( HttpServletRequest request, Announce announce )
            throws SiteMessageException
    {
        announce.setTitle( request.getParameter( PARAMETER_TITLE_ANNOUNCE ) );
        announce.setDescription( request.getParameter( PARAMETER_DESCRIPTION_ANNOUNCE ) );
        announce.setContactInformation( request.getParameter( PARAMETER_CONTACT_INFORMATION ) );

        String strPriceAnnounce = ( request.getParameter( PARAMETER_PRICE_ANNOUNCE ) == null ) ? "" : request
                .getParameter( PARAMETER_PRICE_ANNOUNCE );
        announce.setPrice( strPriceAnnounce );

        Category category = CategoryHome.findByPrimaryKey( announce.getCategory( ).getId( ), _plugin );
        Sector sector = SectorHome.findByPrimaryKey( category.getIdSector( ) );

        // unpublish announce if category moderation is on
        switch ( category.getAnnouncesValidation( ) )
        {
        case PARAMETER_ANNOUNCES_VALIDATION_YES:
            announce.setPublished( false );

            break;

        case PARAMETER_ANNOUNCES_VALIDATION_NO:
            announce.setPublished( true );

            break;
        case PARAMETER_ANNOUNCES_VALIDATION_GLOBAL_PARAMETERS:
        default:
            announce.setPublished( !sector.getAnnouncesValidation( ) );
        }

        EntryFilter filter = new EntryFilter( );
        filter.setIdResource( category.getId( ) );
        filter.setResourceType( Category.RESOURCE_TYPE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        filter.setIdIsComment( EntryFilter.FILTER_FALSE );

        List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );
        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );

        AnnounceDTO announceDTO = new AnnounceDTO( announce );

        for ( Entry entry : listEntryFirstLevel )
        {
            listFormErrors.addAll( _announceService.getResponseEntry( request, entry.getIdEntry( ),
                    request.getLocale( ), announceDTO ) );
        }

        // If there is some errors, we redirect the user to the form page
        if ( listFormErrors.size( ) > 0 )
        {
            _announceService.convertMapResponseToList( announceDTO );
            announce.setListResponse( announceDTO.getListResponse( ) );
            return listFormErrors;
        }

        _announceService.convertMapResponseToList( announceDTO );
        announce.setListResponse( announceDTO.getListResponse( ) );

        announce.setHasPictures( false );
        for ( Response response : announceDTO.getListResponse( ) )
        {
            if ( response.getFile( ) != null && FileUtil.hasImageExtension( response.getFile( ).getTitle( ) ) )
            {
                announce.setHasPictures( true );
                break;
            }
        }

        AnnounceHome.update( announce );

        List<Integer> listIdResponse = AnnounceHome.findListIdResponse( announce.getId( ) );

        for ( int nIdResponse : listIdResponse )
        {
            ResponseHome.remove( nIdResponse );
        }

        AnnounceHome.removeAnnounceResponse( announce.getId( ) );

        for ( Response response : announceDTO.getListResponse( ) )
        {
            ResponseHome.create( response );
            AnnounceHome.insertAppointmentResponse( announce.getId( ), response.getIdResponse( ),
                    response.getFile( ) != null && FileUtil.hasImageExtension( response.getFile( ).getTitle( ) ) );
        }

        // send mail notification only if announce is not published
        if ( !announce.getPublished( ) )
        {
            sendAnnounceNotification( request, announce );
        }

        return null;
    }

    /**
     * Get the confirmation page before removing an announce
     * @param request The request
     * @param model The model
     * @return The HTML content if the site message could not be displayed
     * @throws SiteMessageException If a site message needs to be displayed
     */
    private String getDeleteAnnounce( HttpServletRequest request ) throws SiteMessageException
    {
        String strConfirmRemoveAnnounce = request.getParameter( PARAMETER_CONFIRM_REMOVE_ANNOUNCE );
        int nIdAnnounce = Integer.parseInt( request.getParameter( PARAMETER_ANNOUNCE_ID ) );

        if ( strConfirmRemoveAnnounce != null )
        {
            AnnounceHome.remove( nIdAnnounce );
            return getManageUserAnnounces( request );
        }
        Map<String, Object> requestParameters = new HashMap<String, Object>( );
        requestParameters.put( PARAMETER_PAGE, AnnounceUtils.PARAMETER_PAGE_ANNOUNCE );
        requestParameters.put( MVCUtils.PARAMETER_ACTION, ACTION_DELETE_ANNOUNCE );
        requestParameters.put( PARAMETER_ANNOUNCE_ID, nIdAnnounce );
        requestParameters.put( PARAMETER_CONFIRM_REMOVE_ANNOUNCE, "1" );
        SiteMessageService.setMessage( request, PROPERTY_CONFIRM_REMOVE_ANNOUNCE, SiteMessage.TYPE_CONFIRMATION,
                JSP_PORTAL, requestParameters );
        // Never return null because the setMessage method throw an exception
        return null;
    }

    /**
     * Get the confirmation page before suspending an announce
     * @param request The request
     * @param model The model
     * @return The HTML content if the site message could not be displayed
     * @throws SiteMessageException If a site message needs to be displayed
     */
    private String getSuspendAnnounceByUser( HttpServletRequest request ) throws SiteMessageException
    {
        String strConfirmSuspendAnnounce = request.getParameter( PARAMETER_CONFIRM_SUSPEND_ANNOUNCE );
        int nIdAnnounce = Integer.parseInt( request.getParameter( PARAMETER_ANNOUNCE_ID ) );

        //user must be authenticated
        LuteceUser user = getLuteceUserAuthentication( request );

        // user must be authorized to change this anounce
        Announce announce = getAuthorizedAnnounce( nIdAnnounce, user, request );

        if ( strConfirmSuspendAnnounce != null )
        {
            announce.setSuspendedByUser( true );
            AnnounceHome.setSuspendedByUser( announce );

            return getManageUserAnnounces( request );
        }

        Map<String, Object> requestParameters = new HashMap<String, Object>( );
        requestParameters.put( PARAMETER_PAGE, AnnounceUtils.PARAMETER_PAGE_ANNOUNCE );
        requestParameters.put( MVCUtils.PARAMETER_ACTION, ACTION_SUSPEND_ANNOUNCE_BY_USER );
        requestParameters.put( PARAMETER_ANNOUNCE_ID, nIdAnnounce );
        requestParameters.put( PARAMETER_CONFIRM_SUSPEND_ANNOUNCE, "1" );
        SiteMessageService.setMessage( request, PROPERTY_CONFIRM_SUSPEND_ANNOUNCE, SiteMessage.TYPE_CONFIRMATION,
                JSP_PORTAL, requestParameters );

        return null;
    }

    /**
     * Do enable an announce
     * @param request The request
     * @param model The model
     * @return
     * @throws SiteMessageException If a site message needs to be displayed
     */
    private String enableAnnounceByUser( HttpServletRequest request ) throws SiteMessageException
    {
        int nIdAnnounce = Integer.parseInt( request.getParameter( PARAMETER_ANNOUNCE_ID ) );

        //user must be authenticated
        LuteceUser user = getLuteceUserAuthentication( request );

        // user must be authorized to change this anounce
        Announce announce = getAuthorizedAnnounce( nIdAnnounce, user, request );

        announce.setSuspendedByUser( false );
        AnnounceHome.setSuspendedByUser( announce );

        return getManageUserAnnounces( request );
    }

    /**
     * View an announce
     * @param request The request
     * @param announce The announce
     * @param model The model
     * @returnThe HTML content to display
     */
    private String getViewAnnounce( HttpServletRequest request, Announce announce )
    {
        boolean bAllowAccess = false;
        boolean bUserIsAuthor = false;

        LuteceUser user = null;

        Map<String, Object> model = new HashMap<String, Object>( );
        if ( SecurityService.isAuthenticationEnable( ) )
        { // myLutece not installed or disabled
            user = SecurityService.getInstance( ).getRegisteredUser( request );

            if ( user != null ) // user is logged
            {
                model.put( MARK_USER, user );
            }
        }

        if ( ( ( user != null ) && user.getName( ).equals( announce.getUserName( ) ) ) )
        {
            bUserIsAuthor = true;
        }

        if ( ( announce.getPublished( ) && !announce.getSuspended( ) && !announce.getSuspendedByUser( ) )
                || bUserIsAuthor )
        {
            bAllowAccess = true;
        }

        model.put( MARK_ALLOW_ACCESS, bAllowAccess );

        if ( bAllowAccess )
        {
            Collection<Response> listResponses = AnnounceHome.findListResponse( announce.getId( ), false );

            for ( Response response : listResponses )
            {
                IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( response.getEntry( ) );
                response.setToStringValueResponse( entryTypeService.getResponseValueForRecap( response.getEntry( ),
                        request, response, request.getLocale( ) ) );
            }

            model.put( MARK_USER_IS_AUTHOR, bUserIsAuthor );
            model.put( MARK_ANNOUNCE, announce );
            model.put( MARK_LIST_RESPONSES, listResponses );
            model.put( MARK_LIST_FIELDS, getSectorList( ) );
            model.put( MARK_LOCALE, request.getLocale( ) );

            Category category = CategoryHome.findByPrimaryKey( announce.getCategory( ).getId( ), _plugin );
            announce.setCategory( category );
        }
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_VIEW_ANNOUNCE, request.getLocale( ), model );

        return template.getHtml( );
    }

    /**
     * gets template in order to view all the user's announces
     * @param request httpRequest
     * @param userName the name of the user
     * @param model HashMap model
     * @throws SiteMessageException If a site message needs to be displayed
     */
    private String getViewUserAnnounces( HttpServletRequest request, String userName ) throws SiteMessageException
    {
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, DEFAULT_PAGE_INDEX );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_FRONT_LIST_ANNOUNCE_PER_PAGE, 10 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        int nNbPlublishedAnnounces;

        List<Announce> listAnnounces = AnnounceHome.getAnnouncesForUser( userName );

        Paginator<Announce> paginator = new Paginator<Announce>( listAnnounces, _nItemsPerPage, JSP_PORTAL + "?"
                + PARAMETER_PAGE + "=" + AnnounceUtils.PARAMETER_PAGE_ANNOUNCE + "&" + MVCUtils.PARAMETER_ACTION + "="
                + ACTION_MY_ANNOUNCES, PARAMETER_PAGE_INDEX, _strCurrentPageIndex );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( MARK_PAGINATOR, paginator );

        for ( Announce announce : paginator.getPageItems( ) )
        {
            announce.setListIdImageResponse( AnnounceHome.findListIdImageResponse( announce.getId( ) ) );
        }

        model.put( MARK_ANNOUNCES_LIST, paginator.getPageItems( ) );

        nNbPlublishedAnnounces = 0;

        for ( Announce a : listAnnounces )
        {
            if ( a.getPublished( ) )
            {
                nNbPlublishedAnnounces++;
            }
        }

        LuteceUser user = null;

        if ( SecurityService.isAuthenticationEnable( ) )
        { // myLutece not installed or disabled
            user = SecurityService.getInstance( ).getRegisteredUser( request );

            if ( user != null ) // user is logged
            {
                model.put( MARK_USER, user );
            }
        }

        model.put( MARK_ANNOUNCE_OWNER, userName );
        model.put( MARK_ANNOUNCES_PUBLISHED_AMOUNT, nNbPlublishedAnnounces );
        model.put( MARK_LIST_FIELDS, getSectorList( ) );
        model.put( MARK_LOCALE, request.getLocale( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_VIEW_ANNOUNCES, request.getLocale( ), model );

        return template.getHtml( );
    }

    /**
     * Get the page to search for announces
     * @param request The request
     * @param model The model
     * @return The HTML content to displayed
     */
    private String getSearchAnnounces( HttpServletRequest request )
    {
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, DEFAULT_PAGE_INDEX );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_FRONT_LIST_ANNOUNCE_PER_PAGE, 10 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        String strKeywords = request.getParameter( PARAMETER_KEYWORDS );
        String strIdCategory = request.getParameter( PARAMETER_CATEGORY_ID );
        String strDateMin = request.getParameter( PARAMETER_DATE_MIN );
        String strDateMax = request.getParameter( PARAMETER_DATE_MAX );
        int nIdCategory = Integer.parseInt( strIdCategory );

        DateFormat dateFormat = new SimpleDateFormat( PATTERN_DATE, Locale.FRENCH );

        dateFormat.setLenient( false );

        // Check if filters sectors are null
        strKeywords = ( strKeywords == null ) ? StringUtils.EMPTY : strKeywords;

        Date formatedDateMin = null;
        Date formatedDateMax = null;

        if ( StringUtils.isNotEmpty( strDateMin ) )
        {
            try
            {
                formatedDateMin = dateFormat.parse( strDateMin.trim( ) );
            }
            catch ( ParseException e )
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
            catch ( ParseException e )
            {
                AppLogService.error( e );
            }
        }

        List<Integer> listIdAnnounces = AnnounceSearchService.getInstance( ).getSearchResults( strKeywords,
                nIdCategory, formatedDateMin, formatedDateMax, request, _plugin );

        Paginator<Integer> paginatorId = new Paginator<Integer>( listIdAnnounces, _nItemsPerPage, StringUtils.EMPTY,
                PARAMETER_PAGE_INDEX, _strCurrentPageIndex );

        List<Announce> listAnnounces = AnnounceHome.findByListId( paginatorId.getPageItems( ) );

        String strSearchParameters = PARAMETER_KEYWORDS + "=" + strKeywords + "&" + PARAMETER_CATEGORY_ID + "="
                + strIdCategory + "&" + PARAMETER_DATE_MIN + "=" + strDateMin + "&" + PARAMETER_DATE_MAX + "="
                + strDateMax;

        LocalizedDelegatePaginator<Announce> paginator = new LocalizedDelegatePaginator<Announce>( listAnnounces,
                _nItemsPerPage, JSP_PORTAL + "?" + PARAMETER_PAGE + "=" + AnnounceUtils.PARAMETER_PAGE_ANNOUNCE + "&"
                        + MVCUtils.PARAMETER_ACTION + "=" + ACTION_SEARCH + "&" + strSearchParameters,
                PARAMETER_PAGE_INDEX, _strCurrentPageIndex, listIdAnnounces.size( ), request.getLocale( ) );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( _nItemsPerPage ) );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_LIST_FIELDS, getSectorList( ) );
        model.put( MARK_LOCALE, request.getLocale( ) );

        for ( Announce announce : paginator.getPageItems( ) )
        {
            announce.setListIdImageResponse( AnnounceHome.findListIdImageResponse( announce.getId( ) ) );
        }

        model.put( MARK_ANNOUNCES_LIST, paginator.getPageItems( ) );
        model.put( MARK_FILTER_SEARCHED_KEYWORDS, strKeywords );
        model.put( MARK_FILTER_SEARCHED_CATEGORY, nIdCategory );
        model.put( MARK_FILTER_DATE_MIN, strDateMin );
        model.put( MARK_FILTER_DATE_MAX, strDateMax );
        model.put( MARK_LOCALE, request.getLocale( ) );
        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
        model.put( MARK_USER, user );

        //useful if you want to work with Portal.jsp and RunStandaloneApp.jsp
        model.put( FULL_URL, request.getRequestURL( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_LIST_ANNOUNCES, request.getLocale( ), model );

        return template.getHtml( );
    }

    /**
     * Get the current LuteceUser, and throw an exception if the user was not
     * found
     * @param request The request
     * @return The current Lutece User
     * @throws SiteMessageException If a site message needs to be displayed
     */
    private static LuteceUser getLuteceUserAuthentication( HttpServletRequest request ) throws SiteMessageException
    {
        LuteceUser user = null;

        if ( SecurityService.isAuthenticationEnable( ) )
        { // myLutece not installed or disabled
            user = SecurityService.getInstance( ).getRegisteredUser( request );

            if ( user == null ) // user is not logged
            {
                SiteMessageService.setMessage( request, PROPERTY_NOT_AUTHORIZED, SiteMessage.TYPE_STOP );
            }
        }
        else
        {
            SiteMessageService.setMessage( request, PROPERTY_NOT_AUTHORIZED, SiteMessage.TYPE_STOP );
        }

        return user;
    }

    /**
     * Get an announce if the current user is its owner
     * @param nIdAnnounce The id of the announce to get
     * @param user The user
     * @param request The request
     * @return The announce
     * @throws SiteMessageException If a site message needs to be displayed
     */
    private Announce getAuthorizedAnnounce( int nIdAnnounce, LuteceUser user, HttpServletRequest request )
            throws SiteMessageException
    {
        Announce announce = AnnounceHome.findByPrimaryKey( nIdAnnounce );

        if ( announce == null || !announce.getUserName( ).equals( user.getName( ) ) )
        {
            SiteMessageService.setMessage( request, PROPERTY_REFUSED_ACCESS, SiteMessage.TYPE_STOP );
        }

        return announce;
    }

    /**
     * Do send an announce by email to admin users
     * @param request The request
     * @param announce The announce
     */
    private void sendAnnounceNotification( HttpServletRequest request, Announce announce )
    {
        int nIdMailingList = announce.getCategory( ).getIdMailingList( );
        Collection<Recipient> listRecipients = AdminMailingListService.getRecipients( nIdMailingList );

        for ( Recipient recipient : listRecipients )
        {
            HashMap<String, Object> model = new HashMap<String, Object>( );

            String strSenderEmail = AppPropertiesService.getProperty( PROPERTY_WEBMASTER_EMAIL );
            String strSenderName = AppPropertiesService.getProperty( PROPERTY_WEBMASTER_NAME );
            String strSubject = I18nService.getLocalizedString( PROPERTY_ANNOUNCE_NOTIFY_SUBJECT, request.getLocale( ) );

            // Generate the subject of the message
            strSubject += ( " " + announce.getCategory( ).getLabel( ) );

            // Generate the body of the message
            model.put( MARK_PROD_URL, AppPropertiesService.getProperty( PROPERTY_PROD_URL ) );
            model.put( MARK_ANNOUNCE, announce );
            model.put( MARK_LIST_FIELDS, getSectorList( ) );
            model.put( MARK_LOCALE, request.getLocale( ) );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_ANNOUNCE_NOTIFY_MESSAGE,
                    request.getLocale( ), model );
            String strBody = template.getHtml( );

            MailService.sendMailHtml( recipient.getEmail( ), strSenderName, strSenderEmail, strSubject, strBody );
        }
    }

    /**
     * Get the HTML code of the form to create or modify an announce
     * @param request The request
     * @param announce The announce to get the form of (null to get a creation
     *            form)
     * @param category The category of the announce
     * @param locale the locale
     * @param model The model to use to display the page
     * @return The HTML code to display, or an empty string if the form is null
     *         or not active
     */
    public String getAnnounceFormHtml( HttpServletRequest request, Announce announce, Category category, Locale locale,
            Map<String, Object> model )
    {
        if ( category == null )
        {
            return StringUtils.EMPTY;
        }

        Sector sector = SectorHome.findByPrimaryKey( category.getIdSector( ) );

        model.put( MARK_FORM_HTML, _announceService.getHtmlAnnounceForm( announce, category, locale, true, request ) );
        model.put( MARK_CATEGORY, category );
        model.put( MARK_SECTOR, sector );
        model.put( MARK_LIST_FIELDS, getSectorList( ) );
        model.put( MARK_LOCALE, request.getLocale( ) );

        HtmlTemplate template = AppTemplateService.getTemplate(
                announce == null ? TEMPLATE_PAGE_CREATE_ANNOUNCE_STEP_FORM : TEMPLATE_MODIFY_ANNOUNCE, locale, model );

        return template.getHtml( );
    }

    /**
     * Removes the uploaded fileItem
     * @param request the request
     * @return The JSON result
     */
    public String doRemoveAsynchronousUploadedFile( HttpServletRequest request )
    {
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        String strFieldIndex = request.getParameter( PARAMETER_FIELD_INDEX );

        if ( StringUtils.isBlank( strIdEntry ) || StringUtils.isBlank( strFieldIndex ) )
        {
            return JSONUtils.buildJsonErrorRemovingFile( request ).toString( );
        }

        // parse json
        JSON jsonFieldIndexes = JSONSerializer.toJSON( strFieldIndex );

        if ( !jsonFieldIndexes.isArray( ) )
        {
            return JSONUtils.buildJsonErrorRemovingFile( request ).toString( );
        }

        JSONArray jsonArrayFieldIndexers = (JSONArray) jsonFieldIndexes;
        int[] tabFieldIndex = new int[jsonArrayFieldIndexers.size( )];

        for ( int nIndex = 0; nIndex < jsonArrayFieldIndexers.size( ); nIndex++ )
        {
            try
            {
                tabFieldIndex[nIndex] = Integer.parseInt( jsonArrayFieldIndexers.getString( nIndex ) );
            }
            catch ( NumberFormatException nfe )
            {
                return JSONUtils.buildJsonErrorRemovingFile( request ).toString( );
            }
        }

        // inverse order (removing using index - remove greater first to keep order)
        Arrays.sort( tabFieldIndex );
        ArrayUtils.reverse( tabFieldIndex );

        AnnounceAsynchronousUploadHandler handler = AnnounceAsynchronousUploadHandler.getHandler( );

        for ( int nFieldIndex : tabFieldIndex )
        {
            handler.removeFileItem( strIdEntry, request.getSession( ).getId( ), nFieldIndex );
        }

        JSONObject json = new JSONObject( );
        // operation successful
        json.element( JSONUtils.JSON_KEY_SUCCESS, JSONUtils.JSON_KEY_SUCCESS );
        json.accumulateAll( JSONUtils.getUploadedFileJSON( handler.getFileItems( strIdEntry, request.getSession( )
                .getId( ) ) ) );
        json.element( JSONUtils.JSON_KEY_FIELD_NAME, handler.buildFieldName( strIdEntry ) );

        return json.toString( );
    }

    /**
     * Get the list of announces of the user
     * @param request The request
     * @param model The model
     * @return The HTML content to display
     * @throws SiteMessageException If a site message needs to be displayed
     */
    public static String getManageUserAnnounces( HttpServletRequest request ) throws SiteMessageException
    {
        LuteceUser user = getLuteceUserAuthentication( request );

        String strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                DEFAULT_PAGE_INDEX );
        int nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_FRONT_LIST_ANNOUNCE_PER_PAGE,
                10 );

        Integer nOldItemsPerPageFromSession = (Integer) request.getSession( ).getAttribute(
                SESSION_ATTRIBUTE_MY_ANNOUNCES_ITEMS_PER_PAGE );
        int nOldItemsPerPage = nOldItemsPerPageFromSession != null ? nOldItemsPerPageFromSession : nDefaultItemsPerPage;

        int nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, nOldItemsPerPage,
                nDefaultItemsPerPage );

        request.getSession( ).setAttribute( SESSION_ATTRIBUTE_MY_ANNOUNCES_ITEMS_PER_PAGE, nItemsPerPage );

        List<Announce> listAnnounces = AnnounceHome.getAnnouncesForUser( user );

        UrlItem urlItem = new UrlItem( JSP_PORTAL );
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_PAGE ) ) )
        {
            urlItem.addParameter( PARAMETER_PAGE, request.getParameter( PARAMETER_PAGE ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( MVCUtils.PARAMETER_ACTION ) ) )
        {
            urlItem.addParameter( PARAMETER_PAGE, request.getParameter( MVCUtils.PARAMETER_ACTION ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( MVCUtils.PARAMETER_VIEW ) ) )
        {
            urlItem.addParameter( PARAMETER_PAGE, request.getParameter( MVCUtils.PARAMETER_VIEW ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( Parameters.PAGE_ID ) ) )
        {
            urlItem.addParameter( PARAMETER_PAGE, request.getParameter( Parameters.PAGE_ID ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( Parameters.PAGE_ID ) ) )
        {
            urlItem.addParameter( PARAMETER_PAGE, request.getParameter( Parameters.PAGE_ID ) );
        }

        Paginator<Announce> paginator = new Paginator<Announce>( listAnnounces, nItemsPerPage, urlItem.getUrl( ),
                PARAMETER_PAGE_INDEX, strCurrentPageIndex );

        for ( Announce announce : paginator.getPageItems( ) )
        {
            announce.setListIdImageResponse( AnnounceHome.findListIdImageResponse( announce.getId( ) ) );
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_LIST_FIELDS, getSectorList( ) );
        model.put( MARK_LOCALE, request.getLocale( ) );
        model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( nItemsPerPage ) );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_ANNOUNCES_LIST, paginator.getPageItems( ) );
        model.put( MARK_USER, user );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MY_ANNOUNCES, request.getLocale( ), model );

        return template.getHtml( );
    }

    /**
     * Get the list of sectors to be displayed in the navigation menu
     * @return The list of sectors
     */
    private static Collection<Sector> getSectorList( )
    {
        Collection<Sector> listSectors = SectorHome.findAll( );

        for ( Sector sector : listSectors )
        {
            int nNumberAnnounces = 0;
            Collection<Category> categoryList = CategoryHome.findCategoriesForSector( sector,
                    AnnouncePlugin.getPlugin( ) );
            sector.setListCategories( categoryList );

            for ( Category category : categoryList )
            {
                nNumberAnnounces += CategoryHome.countPublishedAnnouncesForCategory( category,
                        AnnouncePlugin.getPlugin( ) );
            }

            sector.setNumberAnnounces( nNumberAnnounces );
        }
        return listSectors;
    }
}
