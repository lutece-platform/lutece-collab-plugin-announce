/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
import fr.paris.lutece.plugins.announce.business.AnnounceSearchFilter;
import fr.paris.lutece.plugins.announce.business.AnnounceSearchFilterHome;
import fr.paris.lutece.plugins.announce.business.AnnounceSort;
import fr.paris.lutece.plugins.announce.business.Category;
import fr.paris.lutece.plugins.announce.business.CategoryHome;
import fr.paris.lutece.plugins.announce.business.Sector;
import fr.paris.lutece.plugins.announce.business.SectorHome;
import fr.paris.lutece.plugins.announce.service.AnnounceService;
import fr.paris.lutece.plugins.announce.service.AnnounceSubscriptionProvider;
import fr.paris.lutece.plugins.announce.service.announcesearch.AnnounceSearchService;
import fr.paris.lutece.plugins.announce.service.upload.AnnounceAsynchronousUploadHandler;
import fr.paris.lutece.plugins.announce.utils.AnnounceUtils;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.subscribe.business.Subscription;
import fr.paris.lutece.plugins.subscribe.web.SubscribeApp;
import fr.paris.lutece.portal.business.mailinglist.Recipient;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.LuteceUserService;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.portal.web.util.LocalizedDelegatePaginator;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.file.FileUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class manages Announce page.
 */
@Controller( xpageName = AnnounceUtils.PARAMETER_PAGE_ANNOUNCE, pageTitleI18nKey = AnnounceApp.PROPERTY_PAGE_TITLE, pagePathI18nKey = AnnounceApp.PROPERTY_PAGE_PATH )
public class AnnounceApp extends MVCApplication
{
    // Public properties
    /**
     * The title of the default page
     */
    public static final String PROPERTY_PAGE_TITLE = "announce.page_announce.pageTitle";

    /**
     * The default path of pages of this application
     */
    public static final String PROPERTY_PAGE_PATH = "announce.page_announce.pagePathLabel";
    private static final long serialVersionUID = 3586318619582357870L;
    private static final String PARAMETER_USERNAME = "username";

    //Jsp redirections
    private static final String JSP_PORTAL = "jsp/site/Portal.jsp";

    // Parameters
    private static final String PARAMETER_FORM_SEND = "form_send";
    private static final String PARAMETER_PAGE = "page";
    private static final String PARAMETER_SECTOR_ID = "sector_id";
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
    private static final String PARAMETER_PRICE_MIN = "price_min";
    private static final String PARAMETER_PRICE_MAX = "price_max";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_TAGS = "tags";
    private static final String PARAMETER_HAS_FILTER = "hasFilter";
    private static final String PARAMETER_ID_FILTER = "id_filter";

    // Actions
    private static final String ACTION_VIEW_ANNOUNCE = "view_announce";
    private static final String ACTION_VIEW_ANNOUNCES = "view_announces";
    private static final String ACTION_MY_ANNOUNCES = "my_announces";
    private static final String ACTION_MODIFY_ANNOUNCE = "modify_announce";
    private static final String ACTION_DELETE_ANNOUNCE = "delete_announce";
    private static final String ACTION_SUSPEND_ANNOUNCE_BY_USER = "suspend_by_user";
    private static final String ACTION_ENABLE_ANNOUNCE_BY_USER = "enable_by_user";
    private static final String ACTION_VIEW_SUBSCRIPTIONS = "view_subscriptions";
    private static final String ACTION_VIEW_SUB = "view_sub";
    private static final String ACTION_SEARCH = "search";
    private static final String ACTION_ADDNEW = "addnew";

    // Views
    private static final String VIEW_DEFAULT_PAGE = "viewDefaultPage";
    
    // Validation flags
    private static final int PARAMETER_ANNOUNCES_VALIDATION_GLOBAL_PARAMETERS = 0;
    private static final int PARAMETER_ANNOUNCES_VALIDATION_YES = 1;
    private static final int PARAMETER_ANNOUNCES_VALIDATION_NO = 2;

    //properties
    private static final String PROPERTY_NOT_AUTHORIZED = "announce.messages.notAuthorized";
    private static final String PROPERTY_QUOTA_EXCEEDED = "announce.messages.quotaExceeded";
    private static final String PROPERTY_REFUSED_ACCESS = "announce.messages.refusedAccess";
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
    private static final String TEMPLATE_VIEW_SUB = "skin/plugins/announce/subscription/user_sub_des.html";
    private static final String TEMPLATE_MY_ANNOUNCES = "skin/plugins/announce/my_announces.html";
    private static final String TEMPLATE_MODIFY_ANNOUNCE = "skin/plugins/announce/modify_announce.html";
    private static final String TEMPLATE_LIST_ANNOUNCES = "skin/plugins/announce/list_announces.html";
    private static final String TEMPLATE_LIST_ANNOUNCES_BY_ID = "skin/plugins/announce/list_announces_by_id.html";
    private static final String TEMPLATE_ANNOUNCE_NOTIFY_MESSAGE = "skin/plugins/announce/announce_notify_message.html";

    // Session keys
    private static final String SESSION_KEY_ANNOUNCE_FILTER = "announce.session.announceSearchFilter";

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
    private static final String FULL_URL = "fullurl";
    private static final String MARK_FILTER_DATE_MIN = "filter_date_min";
    private static final String MARK_FILTER_DATE_MAX = "filter_date_max";
    private static final String MARK_FILTER = "filter";
    private static final String MARK_ANNOUNCE = "announce";
    private static final String MARK_ANNOUNCE_OWNER = "owner";
    private static final String MARK_ANNOUNCE_OWNER_NAME = "owner_name";
    private static final String MARK_HAS_SUBSCRIBED_TO_USER = "hasSubscribedToUser";
    private static final String MARK_ALLOW_ACCESS = "allow_access";
    private static final String MARK_USER_IS_AUTHOR = "user_is_author";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_MODERATED = "moderated";
    private static final String MARK_PROD_URL = "prod_url";
    private static final String MARK_FORM_HTML = "form_html";
    private static final String MARK_LIST_ERRORS = "list_errors";
    private static final String MARK_IS_EXTEND_INSTALLED = "isExtendInstalled";
    private static final String MARK_CAPTCHA = "captcha";
    private static final String MARK_LIST_SECTORS = "list_sectors";
    private static final String MARK_LIST_CATEGORIES = "list_sector_categories";
    private static final String MARK_ENTRY_LIST_GEOLOCATION = "list_entryTypeGeolocation";
    // Messages
    private static final String ERROR_MESSAGE_WRONG_CAPTCHA = "portal.admin.message.wrongCaptcha";

    // Constants
    private static final String CONSTANT_BLANK_SPACE = " ";
    private static final String CONSTANT_COMA = ",";
    private static final String CONSTANT_POINT = ".";

    // Session keys
    private static final String SESSION_ATTRIBUTE_MY_ANNOUNCES_ITEMS_PER_PAGE = "announce.myAnnouncesItemsPerPage";

    //defaults
    private static final String DEFAULT_PAGE_INDEX = "1";
    private static final CaptchaSecurityService _captchaSecurityService = new CaptchaSecurityService(  );

    // private fields
    private AnnounceService _announceService = SpringContextService.getBean( AnnounceService.BEAN_NAME );
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;

    /**
     * Date format for filters
     */
    private final DateFormat _dateFormat = AnnounceService.getDateFormat(  );

    /**
     * Get the default page to display
     * @param request The request
     * @return The HTML content to display
     */
    @View( value = VIEW_DEFAULT_PAGE, defaultView = true )
    public XPage getDefaultPage( HttpServletRequest request )
    {
        request.getSession(  ).removeAttribute( SESSION_KEY_ANNOUNCE_FILTER );
        
        return getSearchAnnounces( request );
    }

    /**
     * Get the page to search for announces
     * @param request The request
     * @return The HTML content to displayed
     */
    @Action( ACTION_SEARCH )
    public XPage getSearchAnnounces( HttpServletRequest request )
    {
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, DEFAULT_PAGE_INDEX );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_FRONT_LIST_ANNOUNCE_PER_PAGE, 10 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        AnnounceSearchFilter filter = getAnnounceFilterFromRequest( request );
        
        int nCurrentPageIndex = ( StringUtils.isNotEmpty( _strCurrentPageIndex ) &&
            StringUtils.isNumeric( _strCurrentPageIndex ) ) ? Integer.parseInt( _strCurrentPageIndex ) : 1;
        List<Announce> listAnnouncesResults = new ArrayList<Announce>();
        
       
        //-------------------------SORT---------------------------------
       
        String strSort = (request.getParameter("sortBy") == null ? "" :request.getParameter("sortBy"));
        AnnounceSort anSort = AnnounceSort.DEFAULT_SORT;
        String strUrl = getUrlSearchAnnounceSort( request, 0);

        if(strSort.compareTo("date_modification") == 0){
        	anSort = AnnounceSort.getAnnounceSort(AnnounceSort.SORT_DATE_MODIFICATION, false);
        	strUrl = getUrlSearchAnnounceSort( request,1 );
        }
        	        
        if(strSort.compareTo("title_announce") == 0){
        	anSort = AnnounceSort.getAnnounceSort(AnnounceSort.SORT_TITLE, true);
        	strUrl = getUrlSearchAnnounceSort( request,2 );
        }
        if(strSort.compareTo("price_announce") == 0){
        	anSort = AnnounceSort.getAnnounceSort(AnnounceSort.SORT_PRICE, true);
        	strUrl = getUrlSearchAnnounceSort( request,3 );
        }
        if(strSort.compareTo("description_announce") == 0){
        	anSort = AnnounceSort.getAnnounceSort(AnnounceSort.SORT_DESCRIPTION, true);
        	strUrl = getUrlSearchAnnounceSort( request,4 );
        }
        	
        int nNbItems = AnnounceSearchService.getInstance(  )
                .getSearchResultsBis( filter, nCurrentPageIndex, _nItemsPerPage,listAnnouncesResults, anSort );
        //List<Announce> listAnnounces = AnnounceHome.findByListId( listIdAnnounces);
        
        //--------------------------END SORT----------------------------------
        
        
        LocalizedDelegatePaginator<Announce> paginator = new LocalizedDelegatePaginator<Announce>( listAnnouncesResults,
                _nItemsPerPage, strUrl, PARAMETER_PAGE_INDEX, _strCurrentPageIndex, nNbItems,
                request.getLocale(  ) );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( _nItemsPerPage ) );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_LIST_FIELDS, getSectorList(  ) );
        model.put( MARK_LOCALE, request.getLocale(  ) );
        
        for ( Announce announce : paginator.getPageItems(  ) )
        {
            announce.setListIdImageResponse( AnnounceHome.findListIdImageResponse( announce.getId(  ) ) );
        }

        model.put( MARK_ANNOUNCES_LIST, paginator.getPageItems(  ) );
        model.put( MARK_FILTER_DATE_MIN,
            ( filter.getDateMin(  ) != null ) ? _dateFormat.format( filter.getDateMin(  ) ) : null );
        model.put( MARK_FILTER_DATE_MAX,
            ( filter.getDateMax(  ) != null ) ? _dateFormat.format( filter.getDateMax(  ) ) : null );
        model.put( MARK_FILTER, filter );

        LuteceUser user = SecurityService.getInstance(  ).getRegisteredUser( request );
        model.put( MARK_USER, user );

        //useful if you want to work with Portal.jsp and RunStandaloneApp.jsp
        model.put( FULL_URL, request.getRequestURL(  ) );

        model.put( MARK_LIST_SECTORS, AnnounceApp.getSectorList(  ) );
        int nIdSector = (request.getParameter("sector_id") == null ? 0 :Integer.parseInt(request.getParameter("sector_id")));
        model.put( MARK_LIST_CATEGORIES, AnnounceApp.getCategoryList( nIdSector ));
        model.put( "sortArg", anSort.getSortColumn()  );
        model.put( "page_index", _strCurrentPageIndex  );
        model.put( "nbItem",nNbItems );
        if ( SecurityService.isAuthenticationEnable(  ) )
        {
            model.put( MARK_USER, SecurityService.getInstance(  ).getRegisteredUser( request ) );
        }

        XPage page = getXPage( TEMPLATE_LIST_ANNOUNCES, request.getLocale(  ), model );
        page.setTitle( I18nService.getLocalizedString( PROPERTY_PAGE_TITLE_SEARCH_RESULTS, request.getLocale(  ) ) );

        return page;
    }
    
    /**
     * Get the HTML to describe a list of announces
     * @param request The request
     * @param listIdAnnounces The list of ids of announces
     * @param announceSort the sort to use
     * @return The HTML content to display
     */
    public static String getAnnounceListById( HttpServletRequest request, List<Integer> listIdAnnounces,
        AnnounceSort announceSort )
    {
        List<Announce> listAnnounces = AnnounceHome.findByListId( listIdAnnounces, announceSort );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_LIST_FIELDS, getSectorList(  ) );
        model.put( MARK_LOCALE, request.getLocale(  ) );

        for ( Announce announce : listAnnounces )
        {
            announce.setListIdImageResponse( AnnounceHome.findListIdImageResponse( announce.getId(  ) ) );
        }

        model.put( MARK_ANNOUNCES_LIST, listAnnounces );

        LuteceUser user = SecurityService.getInstance(  ).getRegisteredUser( request );
        model.put( MARK_USER, user );

        //useful if you want to work with Portal.jsp and RunStandaloneApp.jsp
        model.put( FULL_URL, request.getRequestURL(  ) );

        model.put( MARK_LIST_SECTORS, AnnounceApp.getSectorList(  ) );

        if ( SecurityService.isAuthenticationEnable(  ) )
        {
            model.put( MARK_USER, SecurityService.getInstance(  ).getRegisteredUser( request ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_LIST_ANNOUNCES_BY_ID, request.getLocale(  ),
                model );

        return template.getHtml(  );
    }

    /**
     * Get the page to create an announce
     * @param request The request
     * @return The HTML content to display
     * @throws SiteMessageException If a site message needs to be displayed
     */
    @Action( ACTION_ADDNEW )
    public XPage getCreateAnnounce( HttpServletRequest request )
        throws SiteMessageException
    {
        LuteceUser user = getLuteceUserAuthentication( request );

        String strCategoryId = request.getParameter( PARAMETER_CATEGORY_ID );
        String strFormSend = request.getParameter( PARAMETER_FORM_SEND );
        Map<String, Object> model = new HashMap<String, Object>(  );

        /* CATEOGRY */
        if ( ( strCategoryId != null ) && ( Integer.parseInt( strCategoryId ) != 0 ) )
        {
            Category category = CategoryHome.findByPrimaryKey( Integer.parseInt( strCategoryId ) );
            Sector sector = SectorHome.findByPrimaryKey( category.getIdSector(  ) );
            Announce announce = null;

            /* FORM */
            if ( strFormSend != null )
            {
                announce = new Announce(  );
                model.put( MARK_CATEGORY, category );

                List<GenericAttributeError> listErrors = doCreateAnnounce( request, sector, category, announce, user );

                if ( ( listErrors == null ) || ( listErrors.size(  ) == 0 ) )
                {
                    try
                    {
                        LocalVariables.getResponse(  ).sendRedirect( getUrlViewAnnounce( request, announce.getId(  ) ) );
                    }
                    catch ( IOException e )
                    {
                        AppLogService.error( e.getMessage(  ), e );
                    }
                }

                model.put( MARK_LIST_ERRORS, listErrors );
            }
            else
            {
                AnnounceAsynchronousUploadHandler.getHandler(  ).removeSessionFiles( request.getSession(  ).getId(  ) );
            }

            model.put( MARK_ANNOUNCE, announce );
            model.put( MARK_CONTACT_INFORMATION, user.getUserInfo( LuteceUser.BUSINESS_INFO_ONLINE_EMAIL ) );

            XPage page = getAnnounceFormHtml( request, announce, category, request.getLocale(  ), model );
            page.setTitle( I18nService.getLocalizedString( PROPERTY_PAGE_TITLE_CREATE_ANNOUNCE, request.getLocale(  ) ) );

            return page;
        }

        AnnounceAsynchronousUploadHandler.getHandler(  ).removeSessionFiles( request.getSession(  ).getId(  ) );

        Collection<Announce> listAnnounces = AnnounceHome.getAnnouncesForUser( user, AnnounceSort.DEFAULT_SORT );

        if ( listAnnounces.size(  ) < AppPropertiesService.getPropertyInt( PROPERTY_MAX_AMOUNT_ANNOUNCE, 20 ) )
        {
            model.put( MARK_LIST_FIELDS, getSectorList(  ) );

            XPage page = getXPage( TEMPLATE_PAGE_CREATE_ANNOUNCE_STEP_CATEGORY, request.getLocale(  ), model );
            page.setTitle( I18nService.getLocalizedString( PROPERTY_PAGE_TITLE_CREATE_ANNOUNCE, request.getLocale(  ) ) );

            return page;
        }

        SiteMessageService.setMessage( request, PROPERTY_QUOTA_EXCEEDED, SiteMessage.TYPE_STOP );

        return null;
    }

    /**
     * Get the page to create an announce
     * @param request The request
     * @return The HTML content to display
     * @throws SiteMessageException If a site message needs to be displayed
     */
    @Action( ACTION_MODIFY_ANNOUNCE )
    public XPage getModifyAnnounce( HttpServletRequest request )
        throws SiteMessageException
    {
        LuteceUser user = getLuteceUserAuthentication( request );
        int nIdAnnounce = Integer.parseInt( request.getParameter( PARAMETER_ANNOUNCE_ID ) );
        Announce announce = getAuthorizedAnnounce( nIdAnnounce, user, request );

        String strFormSend = request.getParameter( PARAMETER_FORM_SEND );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_LIST_FIELDS, getSectorList(  ) );
        model.put( MARK_LOCALE, request.getLocale(  ) );

        if ( strFormSend != null )
        {
            List<GenericAttributeError> listErrors = doModifyAnnounce( request, announce );

            if ( listErrors == null )
            {
                return redirect( request, getUrlViewAnnounce( request, nIdAnnounce ) );
            }

            model.put( MARK_LIST_ERRORS, listErrors );
        }
        else
        {
            AnnounceAsynchronousUploadHandler.getHandler(  ).removeSessionFiles( request.getSession(  ).getId(  ) );
        }

        Category category = CategoryHome.findByPrimaryKey( announce.getCategory(  ).getId(  ) );
        Sector sector = SectorHome.findByPrimaryKey( category.getIdSector(  ) );
        boolean bModerated = true;

        // unpublish announce if category moderation is on
        switch ( category.getAnnouncesValidation(  ) )
        {
            case PARAMETER_ANNOUNCES_VALIDATION_YES:
                bModerated = true;

                break;

            case PARAMETER_ANNOUNCES_VALIDATION_NO:
                bModerated = false;

                break;

            case PARAMETER_ANNOUNCES_VALIDATION_GLOBAL_PARAMETERS:default:
                bModerated = sector.getAnnouncesValidation(  );
        }

        model.put( MARK_MODERATED, bModerated );
        model.put( MARK_ANNOUNCE, announce );

        XPage page = getAnnounceFormHtml( request, announce, category, request.getLocale(  ), model );
        page.setTitle( I18nService.getLocalizedString( PROPERTY_PAGE_TITLE_MODIFY_ANNOUNCE, request.getLocale(  ) ) );

        return page;
    }

    /**
     * Get the confirmation page before removing an announce
     * @param request The request
     * @return The HTML content if the site message could not be displayed
     * @throws SiteMessageException If a site message needs to be displayed
     */
    @Action( ACTION_DELETE_ANNOUNCE )
    public XPage getDeleteAnnounce( HttpServletRequest request )
        throws SiteMessageException
    {
        String strConfirmRemoveAnnounce = request.getParameter( PARAMETER_CONFIRM_REMOVE_ANNOUNCE );
        int nIdAnnounce = Integer.parseInt( request.getParameter( PARAMETER_ANNOUNCE_ID ) );

        if ( strConfirmRemoveAnnounce != null )
        {
            AnnounceHome.remove( nIdAnnounce );

            return redirect( request, AppPathService.getBaseUrl( request ) + getActionFullUrl( ACTION_MY_ANNOUNCES ) );
        }

        Map<String, Object> requestParameters = new HashMap<String, Object>(  );
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
     * @return the XPage to display
     * @throws SiteMessageException If a site message needs to be displayed
     */
    @Action( ACTION_SUSPEND_ANNOUNCE_BY_USER )
    public XPage getSuspendAnnounceByUser( HttpServletRequest request )
        throws SiteMessageException
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

            return redirect( request, AppPathService.getBaseUrl( request ) + getActionFullUrl( ACTION_MY_ANNOUNCES ) );
        }

        Map<String, Object> requestParameters = new HashMap<String, Object>(  );
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
     * @return The HTML to display
     * @throws SiteMessageException If a site message needs to be displayed
     */
    @Action( ACTION_ENABLE_ANNOUNCE_BY_USER )
    public XPage enableAnnounceByUser( HttpServletRequest request )
        throws SiteMessageException
    {
        int nIdAnnounce = Integer.parseInt( request.getParameter( PARAMETER_ANNOUNCE_ID ) );

        //user must be authenticated
        LuteceUser user = getLuteceUserAuthentication( request );

        // user must be authorized to change this anounce
        Announce announce = getAuthorizedAnnounce( nIdAnnounce, user, request );

        announce.setSuspendedByUser( false );
        AnnounceHome.setSuspendedByUser( announce );

        return redirect( request, AppPathService.getBaseUrl( request ) + getActionFullUrl( ACTION_MY_ANNOUNCES ) );
    }

    /**
     * View an announce
     * @param request The request
     * @return The HTML content to display
     */
    @Action( ACTION_VIEW_ANNOUNCE )
    public XPage getViewAnnounce( HttpServletRequest request )
    {
        int nIdAnnounce = Integer.parseInt( request.getParameter( PARAMETER_ANNOUNCE_ID ) );
        Announce announce = AnnounceHome.findByPrimaryKey( nIdAnnounce );

        boolean bAllowAccess = false;
        boolean bUserIsAuthor = false;

        LuteceUser user = null;

        Map<String, Object> model = new HashMap<String, Object>(  );

        if ( SecurityService.isAuthenticationEnable(  ) )
        { // myLutece not installed or disabled
            user = SecurityService.getInstance(  ).getRegisteredUser( request );

            if ( user != null ) // user is logged
            {
                model.put( MARK_USER, user );
            }
        }

        if ( ( ( user != null ) && user.getName(  ).equals( announce.getUserName(  ) ) ) )
        {
            bUserIsAuthor = true;
        }

        if ( ( announce.getPublished(  ) && !announce.getSuspended(  ) && !announce.getSuspendedByUser(  ) ) ||
                bUserIsAuthor )
        {
            bAllowAccess = true;
        }

        model.put( MARK_ALLOW_ACCESS, bAllowAccess );

        if ( bAllowAccess )
        {
            Collection<Response> listResponses = AnnounceHome.findListResponse( announce.getId(  ), false );
            Collection<Entry> listGeolocalisation= new ArrayList<Entry>();
            
            for ( Response response : listResponses )
            {
               // IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( response.getEntry(  ) );
               // response.setToStringValueResponse( entryTypeService.getResponseValueForRecap( response.getEntry(  ),
               //         request, response, request.getLocale(  ) ) );
                
                if(response.getEntry()!= null && response.getEntry().getEntryType() != null 
                		&& "announce.entryTypeGeolocation".equals(response.getEntry().getEntryType().getBeanName())){
                	Entry entry = EntryHome.findByPrimaryKey( response.getEntry().getIdEntry()); 
         	        for ( Field filed: entry.getFields( ) ){
         	            	
         	            if( response.getField( ) != null && filed.getIdField ( ) == response.getField( ).getIdField( ) )
         	            		
         	            	response.setField( filed );
         	        }
         	        
         	        boolean bool = true;
     	      
         	        for ( Entry ent:listGeolocalisation ){
         	        	if( ent.getIdEntry( ) == (entry.getIdEntry( )) ){
             	        	bool= false;
             	        }
         	        }
         	        if( bool ){
         	        		listGeolocalisation.add(entry);
         	        }
                }
                
            }
            
            model.put( MARK_ENTRY_LIST_GEOLOCATION , listGeolocalisation);
            model.put( MARK_USER_IS_AUTHOR, bUserIsAuthor );
            model.put( MARK_ANNOUNCE, announce );
            model.put( MARK_LIST_RESPONSES, listResponses );
            model.put( "width", "500px" );
            model.put( "height", "500px" );
            model.put( MARK_LIST_FIELDS, getSectorList(  ) );
            model.put( MARK_LOCALE, request.getLocale(  ) );
            model.put( MARK_IS_EXTEND_INSTALLED, PortalService.isExtendActivated(  ) );

            Category category = CategoryHome.findByPrimaryKey( announce.getCategory(  ).getId(  ) );
            announce.setCategory( category );
        }

        XPage xpage = getXPage( TEMPLATE_VIEW_ANNOUNCE, request.getLocale(  ), model );
        xpage.setTitle( announce.getTitle(  ) );

        return xpage;
    }

    /**
     * Gets template in order to view all the user's announces
     * @param request httpRequest
     * @return The HTML content to display
     * @throws SiteMessageException If a site message needs to be displayed
     */
    @Action( ACTION_VIEW_ANNOUNCES )
    public XPage getViewUserAnnounces( HttpServletRequest request )
        throws SiteMessageException
    {
        String strUserName = request.getParameter( PARAMETER_USERNAME );
        String strUserInfo = "";
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, DEFAULT_PAGE_INDEX );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_FRONT_LIST_ANNOUNCE_PER_PAGE, 10 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        int nNbPlublishedAnnounces;

        List<Announce> listAnnounces = AnnounceHome.getAnnouncesForUser( strUserName, AnnounceSort.DEFAULT_SORT );
        
        if(listAnnounces != null && !listAnnounces.isEmpty()){
        	strUserInfo = listAnnounces.get(0).getUserLastName() + " " + listAnnounces.get(0).getUserSecondName();
        }

        Paginator<Announce> paginator = new Paginator<Announce>( listAnnounces, _nItemsPerPage,
                JSP_PORTAL + "?" + PARAMETER_PAGE + "=" + AnnounceUtils.PARAMETER_PAGE_ANNOUNCE + "&" +
                MVCUtils.PARAMETER_ACTION + "=" + ACTION_MY_ANNOUNCES, PARAMETER_PAGE_INDEX, _strCurrentPageIndex );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( MARK_PAGINATOR, paginator );

        for ( Announce announce : paginator.getPageItems(  ) )
        {
            announce.setListIdImageResponse( AnnounceHome.findListIdImageResponse( announce.getId(  ) ) );
        }

        model.put( MARK_ANNOUNCES_LIST, paginator.getPageItems(  ) );

        nNbPlublishedAnnounces = 0;

        for ( Announce a : listAnnounces )
        {
            if ( a.getPublished(  ) )
            {
                nNbPlublishedAnnounces++;
            }
        }

        LuteceUser user = null;

        if ( SecurityService.isAuthenticationEnable(  ) )
        { // myLutece not installed or disabled
            user = SecurityService.getInstance(  ).getRegisteredUser( request );

            if ( user != null ) // user is logged
            {
                model.put( MARK_USER, user );
            }
        }
        
       // LuteceUser owner = LuteceUserService.getLuteceUserFromName( strUserName );
        
        model.put( MARK_HAS_SUBSCRIBED_TO_USER,
            ( user != null ) ? AnnounceSubscriptionProvider.getService(  ).hasSubscribedToUser( user, strUserName ) : null );

        String strUserRealName = ( strUserInfo == null ) ? strUserName : strUserInfo;
            //CONSTANT_BLANK_SPACE + owner.getUserInfo( LuteceUser.NAME_FAMILY ) );
        model.put( MARK_ANNOUNCE_OWNER, StringUtils.isNotBlank( strUserRealName ) ? strUserRealName : strUserName );
        model.put( MARK_ANNOUNCE_OWNER_NAME, strUserName );
        model.put( MARK_ANNOUNCES_PUBLISHED_AMOUNT, nNbPlublishedAnnounces );
        model.put( MARK_LIST_FIELDS, getSectorList(  ) );
        model.put( MARK_LOCALE, request.getLocale(  ) );

        return getXPage( TEMPLATE_VIEW_ANNOUNCES, request.getLocale(  ), model );
    }

    /**
     * Get the page to view the list of subscriptions of the current user
     * @param request The request
     * @return The XPage to display
     * @throws UserNotSignedException If the user has not signed in
     * @throws SiteMessageException If the authentication mode is not activated
     */
    @Action( ACTION_VIEW_SUBSCRIPTIONS )
    public XPage getViewSubscriptions( HttpServletRequest request )
        throws UserNotSignedException, SiteMessageException
    {
        if ( SecurityService.isAuthenticationEnable(  ) )
        {
            LuteceUser user = SecurityService.getInstance(  ).getRegisteredUser( request );

            if ( user != null )
            {
                XPage page = getXPage(  );
                page.setTitle( I18nService.getLocalizedString( PROPERTY_PAGE_TITLE, request.getLocale(  ) ) );
                page.setContent( SubscribeApp.getSubscriptionList( request ) );

                return page;
            }
        }
        else
        {
            SiteMessageService.setMessage( request, PROPERTY_NOT_AUTHORIZED, SiteMessage.TYPE_STOP );
        }
        throw new UserNotSignedException(  );
    }
    public String getInfoSubscrition(HttpServletRequest request){
    	String strUserName = request.getParameter( PARAMETER_USERNAME );
        
    	List<Subscription> listSubs = AnnounceSubscriptionProvider.getService(  ).getSubscriptionsToUsers();
    	List<Announce> listAnn = AnnounceHome.getAnnouncesForUser( strUserName, AnnounceSort.DEFAULT_SORT );
        
        if(listSubs != null && !listSubs.isEmpty() && listAnn != null && !listAnn.isEmpty()){
        	for(Subscription sub : listSubs)
    		for(Announce ann : listAnn){
    			if(sub.getUserId().compareTo(ann.getContactInformation())==0)
    				return ann.getUserLastName()+" "+ann.getUserSecondName();
    		}
        }
    	return "";
    }
    
    /**
     * Get the XPage to display the announces of the current user
     * @param request The request
     * @return The
     * @throws SiteMessageException If a site message needs to be displayed
     */
    @Action( ACTION_MY_ANNOUNCES )
    public XPage getUserAnnounces( HttpServletRequest request )
        throws SiteMessageException
    {
        XPage page = getXPage(  );
        page.setContent( getManageUserAnnounces( request ) );
        page.setTitle( I18nService.getLocalizedString( PROPERTY_PAGE_TITLE_MY_ANNOUNCES, request.getLocale(  ) ) );

        return page;
    }

    /**
     * Get the current LuteceUser, and throw an exception if the user was not
     * found
     * @param request The request
     * @return The current Lutece User
     * @throws SiteMessageException If a site message needs to be displayed
     */
    private static LuteceUser getLuteceUserAuthentication( HttpServletRequest request )
        throws SiteMessageException
    {
        LuteceUser user = null;

        if ( SecurityService.isAuthenticationEnable(  ) )
        { // myLutece not installed or disabled
            user = SecurityService.getInstance(  ).getRegisteredUser( request );

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

        if ( ( announce == null ) || !announce.getUserName(  ).equals( user.getName(  ) ) )
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
        int nIdMailingList = announce.getCategory(  ).getIdMailingList(  );

        if ( nIdMailingList > 0 )
        {
            Collection<Recipient> listRecipients = AdminMailingListService.getRecipients( nIdMailingList );

            for ( Recipient recipient : listRecipients )
            {
                HashMap<String, Object> model = new HashMap<String, Object>(  );

                String strSenderEmail = AppPropertiesService.getProperty( PROPERTY_WEBMASTER_EMAIL );
                String strSenderName = AppPropertiesService.getProperty( PROPERTY_WEBMASTER_NAME );
                String strSubject = I18nService.getLocalizedString( PROPERTY_ANNOUNCE_NOTIFY_SUBJECT,
                        request.getLocale(  ) );

                // Generate the subject of the message
                strSubject += ( " " + announce.getCategory(  ).getLabel(  ) );

                // Generate the body of the message
                model.put( MARK_PROD_URL, AppPropertiesService.getProperty( PROPERTY_PROD_URL ) );
                model.put( MARK_ANNOUNCE, announce );
                model.put( MARK_LIST_FIELDS, getSectorList(  ) );
                model.put( MARK_LOCALE, request.getLocale(  ) );

                HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_ANNOUNCE_NOTIFY_MESSAGE,
                        request.getLocale(  ), model );
                String strBody = template.getHtml(  );

                MailService.sendMailHtml( recipient.getEmail(  ), strSenderName, strSenderEmail, strSubject, strBody );
            }
        }
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
     * @throws SiteMessageException If a site message needs to be displayed
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

        if ( StringUtils.isEmpty( strTitleAnnounce ) || StringUtils.isEmpty( strDescriptionAnnounce ) ||
                StringUtils.isEmpty( strContactInformation ) ||
                ( category.getDisplayPrice(  ) && category.getPriceMandatory(  ) &&
                StringUtils.isBlank( strPriceAnnounce ) ) )
        {
            SiteMessageService.setMessage( request, Messages.MANDATORY_FIELDS, SiteMessage.TYPE_STOP );

            return null;
        }

        switch ( category.getAnnouncesValidation(  ) )
        {
            case PARAMETER_ANNOUNCES_VALIDATION_YES:
                announce.setPublished( false );

                break;

            case PARAMETER_ANNOUNCES_VALIDATION_NO:
                announce.setPublished( true );

                break;

            case PARAMETER_ANNOUNCES_VALIDATION_GLOBAL_PARAMETERS:default:
                announce.setPublished( !sector.getAnnouncesValidation(  ) );
        }

        announce.setCategory( category );
        announce.setTitle( strTitleAnnounce );
        announce.setDescription( strDescriptionAnnounce );
        announce.setPrice( strPriceAnnounce );
        announce.setContactInformation( strContactInformation );
        announce.setUserName( user.getName(  ) );
        announce.setUserLastName( user.getUserInfo( LuteceUser.NAME_GIVEN ));
        announce.setUserSecondName( user.getUserInfo( LuteceUser.NAME_FAMILY ));
        announce.setUserName( user.getName(  ) );
        announce.setTags( strTags );

        EntryFilter filter = new EntryFilter(  );
        filter.setIdResource( category.getId(  ) );
        filter.setResourceType( Category.RESOURCE_TYPE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        filter.setIdIsComment( EntryFilter.FILTER_FALSE );

        List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );
        List<GenericAttributeError> listErrors = new ArrayList<GenericAttributeError>(  );

        AnnounceDTO announceDTO = new AnnounceDTO( announce );

        for ( Entry entry : listEntryFirstLevel )
        {
            listErrors.addAll( _announceService.getResponseEntry( request, entry.getIdEntry(  ), request.getLocale(  ),
                    announceDTO ) );
        }

        if ( category.getDisplayCaptcha(  ) && _captchaSecurityService.isAvailable(  ) )
        {
            if ( !_captchaSecurityService.validate( request ) )
            {
                GenericAttributeError genAttError = new GenericAttributeError(  );
                genAttError.setErrorMessage( I18nService.getLocalizedString( ERROR_MESSAGE_WRONG_CAPTCHA,
                        request.getLocale(  ) ) );
                listErrors.add( genAttError );
            }
        }

        _announceService.convertMapResponseToList( announceDTO );
        announce.setListResponse( announceDTO.getListResponse(  ) );

        if ( listErrors.size(  ) > 0 )
        {
            return listErrors;
        }

        announce.setHasPictures( false );

        for ( Response response : announceDTO.getListResponse(  ) )
        {
            if ( ( response.getFile(  ) != null ) && FileUtil.hasImageExtension( response.getFile(  ).getTitle(  ) ) )
            {
                announce.setHasPictures( true );

                break;
            }
        }

        AnnounceHome.create( announce );

        for ( Response response : announceDTO.getListResponse(  ) )
        {
            ResponseHome.create( response );
            AnnounceHome.insertAnnounceResponse( announce.getId(  ), response.getIdResponse(  ),
                ( response.getFile(  ) != null ) && FileUtil.hasImageExtension( response.getFile(  ).getTitle(  ) ) );
        }

        if ( category.getIdWorkflow(  ) > 0 )
        {
            WorkflowService.getInstance(  )
                           .getState( announce.getId(  ), Announce.RESOURCE_TYPE, category.getIdWorkflow(  ),
                category.getId(  ) );
            WorkflowService.getInstance(  )
                           .executeActionAutomatic( announce.getId(  ), Announce.RESOURCE_TYPE,
                category.getIdWorkflow(  ), category.getId(  ) );
        }

        // send mail notification only if announce is not published
        if ( !announce.getPublished(  ) )
        {
            sendAnnounceNotification( request, announce );
        }

        AnnounceAsynchronousUploadHandler.getHandler(  ).removeSessionFiles( request.getSession(  ).getId(  ) );

        return null;
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
        Category category = CategoryHome.findByPrimaryKey( announce.getCategory(  ).getId(  ) );

        String strTitleAnnounce = request.getParameter( PARAMETER_TITLE_ANNOUNCE );
        String strDescriptionAnnounce = request.getParameter( PARAMETER_DESCRIPTION_ANNOUNCE );
        String strContactInformation = request.getParameter( PARAMETER_CONTACT_INFORMATION );
        String strTags = request.getParameter( PARAMETER_TAGS );
        String strPriceAnnounce = ( request.getParameter( PARAMETER_PRICE_ANNOUNCE ) == null ) ? StringUtils.EMPTY
                                                                                               : request.getParameter( PARAMETER_PRICE_ANNOUNCE );

        if ( StringUtils.isEmpty( strTitleAnnounce ) || StringUtils.isEmpty( strDescriptionAnnounce ) ||
                StringUtils.isEmpty( strContactInformation ) ||
                ( category.getDisplayPrice(  ) && category.getPriceMandatory(  ) &&
                StringUtils.isBlank( strPriceAnnounce ) ) )
        {
            SiteMessageService.setMessage( request, Messages.MANDATORY_FIELDS, SiteMessage.TYPE_STOP );

            return null;
        }

        announce.setTitle( strTitleAnnounce );
        announce.setDescription( strDescriptionAnnounce );
        announce.setContactInformation( strContactInformation );
        announce.setTags( strTags );
        announce.setPrice( strPriceAnnounce );
        announce.setHasNotify(0);

        Sector sector = SectorHome.findByPrimaryKey( category.getIdSector(  ) );

        // unpublish announce if category moderation is on
        switch ( category.getAnnouncesValidation(  ) )
        {
            case PARAMETER_ANNOUNCES_VALIDATION_YES:
                announce.setPublished( false );

                break;

            case PARAMETER_ANNOUNCES_VALIDATION_NO:
                announce.setPublished( true );

                break;

            case PARAMETER_ANNOUNCES_VALIDATION_GLOBAL_PARAMETERS:default:
                announce.setPublished( !sector.getAnnouncesValidation(  ) );
        }

        EntryFilter filter = new EntryFilter(  );
        filter.setIdResource( category.getId(  ) );
        filter.setResourceType( Category.RESOURCE_TYPE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        filter.setIdIsComment( EntryFilter.FILTER_FALSE );

        List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );
        List<GenericAttributeError> listErrors = new ArrayList<GenericAttributeError>(  );

        AnnounceDTO announceDTO = new AnnounceDTO( announce );

        for ( Entry entry : listEntryFirstLevel )
        {
            listErrors.addAll( _announceService.getResponseEntry( request, entry.getIdEntry(  ), request.getLocale(  ),
                    announceDTO ) );
        }

        if ( category.getDisplayCaptcha(  ) && _captchaSecurityService.isAvailable(  ) )
        {
            if ( !_captchaSecurityService.validate( request ) )
            {
                GenericAttributeError genAttError = new GenericAttributeError(  );
                genAttError.setErrorMessage( I18nService.getLocalizedString( ERROR_MESSAGE_WRONG_CAPTCHA,
                        request.getLocale(  ) ) );
                listErrors.add( genAttError );
            }
        }

        // If there is some errors, we redirect the user to the form page
        if ( listErrors.size(  ) > 0 )
        {
            _announceService.convertMapResponseToList( announceDTO );
            announce.setListResponse( announceDTO.getListResponse(  ) );

            return listErrors;
        }

        _announceService.convertMapResponseToList( announceDTO );
        announce.setListResponse( announceDTO.getListResponse(  ) );

        announce.setHasPictures( false );

        for ( Response response : announceDTO.getListResponse(  ) )
        {
            if ( ( response.getFile(  ) != null ) && FileUtil.hasImageExtension( response.getFile(  ).getTitle(  ) ) )
            {
                announce.setHasPictures( true );

                break;
            }
        }

        AnnounceHome.update( announce );

        List<Integer> listIdResponse = AnnounceHome.findListIdResponse( announce.getId(  ) );

        for ( int nIdResponse : listIdResponse )
        {
            ResponseHome.remove( nIdResponse );
        }

        AnnounceHome.removeAnnounceResponse( announce.getId(  ) );

        for ( Response response : announceDTO.getListResponse(  ) )
        {
            ResponseHome.create( response );
            AnnounceHome.insertAnnounceResponse( announce.getId(  ), response.getIdResponse(  ),
                ( response.getFile(  ) != null ) && FileUtil.hasImageExtension( response.getFile(  ).getTitle(  ) ) );
        }

        // send mail notification only if announce is not published
        if ( !announce.getPublished(  ) )
        {
            sendAnnounceNotification( request, announce );
        }

        return null;
    }

    /**
     * Get the HTML code of the form to create or modify an announce
     * @param request The request
     * @param announce The announce to get the form of (null to get a creation
     *            form)
     * @param category The category of the announce
     * @param locale the locale
     * @param model The model to use to display the page
     * @return The XPage to display, or an empty string if the form is null
     *         or not active
     */
    private XPage getAnnounceFormHtml( HttpServletRequest request, Announce announce, Category category, Locale locale,
        Map<String, Object> model )
    {
        if ( category == null )
        {
            return new XPage(  );
        }

        Sector sector = SectorHome.findByPrimaryKey( category.getIdSector(  ) );

        model.put( MARK_FORM_HTML, _announceService.getHtmlAnnounceForm( announce, category, locale, true, request ) );
        model.put( MARK_CATEGORY, category );
        model.put( MARK_SECTOR, sector );
        model.put( MARK_LIST_FIELDS, getSectorList(  ) );
        model.put( MARK_LOCALE, request.getLocale(  ) );

        if ( category.getDisplayCaptcha(  ) && _captchaSecurityService.isAvailable(  ) )
        {
            model.put( MARK_CAPTCHA, _captchaSecurityService.getHtmlCode(  ) );
        }

        return getXPage( ( ( announce == null ) || ( announce.getId(  ) == 0 ) )
            ? TEMPLATE_PAGE_CREATE_ANNOUNCE_STEP_FORM : TEMPLATE_MODIFY_ANNOUNCE, locale, model );
    }

    /**
     * Get the URL to view an announce
     * @param request The request
     * @param nIdAnnounce The id of the announce to view
     * @return The URL to view the announce
     */
    private String getUrlViewAnnounce( HttpServletRequest request, int nIdAnnounce )
    {
        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + getActionFullUrl( ACTION_VIEW_ANNOUNCE ) );
        url.addParameter( PARAMETER_ANNOUNCE_ID, nIdAnnounce );

        return url.getUrl(  );
    }

    /**
     * Get the list of announces of the user
     * @param request The request
     * @return The HTML content to display
     * @throws SiteMessageException If a site message needs to be displayed
     */
    public static String getManageUserAnnounces( HttpServletRequest request )
        throws SiteMessageException
    {
        LuteceUser user = getLuteceUserAuthentication( request );

        String strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, DEFAULT_PAGE_INDEX );
        int nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_FRONT_LIST_ANNOUNCE_PER_PAGE,
                10 );

        Integer nOldItemsPerPageFromSession = (Integer) request.getSession(  )
                                                               .getAttribute( SESSION_ATTRIBUTE_MY_ANNOUNCES_ITEMS_PER_PAGE );
        int nOldItemsPerPage = ( nOldItemsPerPageFromSession != null ) ? nOldItemsPerPageFromSession
                                                                       : nDefaultItemsPerPage;

        int nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, nOldItemsPerPage,
                nDefaultItemsPerPage );

        request.getSession(  ).setAttribute( SESSION_ATTRIBUTE_MY_ANNOUNCES_ITEMS_PER_PAGE, nItemsPerPage );

        List<Announce> listAnnounces = AnnounceHome.getAnnouncesForUser( user, AnnounceSort.DEFAULT_SORT );

        UrlItem urlItem = new UrlItem( AppPathService.getPortalUrl(  ) );

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_PAGE ) ) )
        {
            urlItem.addParameter( PARAMETER_PAGE, request.getParameter( PARAMETER_PAGE ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( MVCUtils.PARAMETER_ACTION ) ) )
        {
            urlItem.addParameter( MVCUtils.PARAMETER_ACTION, request.getParameter( MVCUtils.PARAMETER_ACTION ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( MVCUtils.PARAMETER_VIEW ) ) )
        {
            urlItem.addParameter( MVCUtils.PARAMETER_VIEW, request.getParameter( MVCUtils.PARAMETER_VIEW ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( Parameters.PAGE_ID ) ) )
        {
            urlItem.addParameter( Parameters.PAGE_ID, request.getParameter( Parameters.PAGE_ID ) );
        }

        Paginator<Announce> paginator = new Paginator<Announce>( listAnnounces, nItemsPerPage, urlItem.getUrl(  ),
                PARAMETER_PAGE_INDEX, strCurrentPageIndex );

        for ( Announce announce : paginator.getPageItems(  ) )
        {
            announce.setListIdImageResponse( AnnounceHome.findListIdImageResponse( announce.getId(  ) ) );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_LIST_FIELDS, getSectorList(  ) );
        model.put( MARK_LOCALE, request.getLocale(  ) );
        model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( nItemsPerPage ) );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_ANNOUNCES_LIST, paginator.getPageItems(  ) );
        model.put( MARK_USER, user );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MY_ANNOUNCES, request.getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * Get the list of sectors to be displayed in the navigation menu
     * @return The list of sectors
     */
    public static Collection<Sector> getSectorList(  )
    {
        Collection<Sector> listSectors = SectorHome.findAll(  );

        for ( Sector sector : listSectors )
        {
            int nNumberAnnounces = 0;
            Collection<Category> categoryList = CategoryHome.findCategoriesForSector( sector );
            sector.setListCategories( categoryList );

            for ( Category category : categoryList )
            {
                nNumberAnnounces += CategoryHome.countPublishedAnnouncesForCategory( category );
            }

            sector.setNumberAnnounces( nNumberAnnounces );
        }

        return listSectors;
    }
    
    /**
     * Get the list of sectors to be displayed in the navigation menu
     * @return The list of sectors
     */
    public static Collection<Category> getCategoryList( int idSector )
    {
    	if(idSector==0){
    		return CategoryHome.findAll();
    	}
    	Sector sector = SectorHome.findByPrimaryKey(idSector);
    	return CategoryHome.findCategoriesForSector(sector);
    }

    /**
     * Get the announce search filter with data contained in an HTTP request
     * @param request The request
     * @return The search filter. If the request contains no filter data, then
     *         the returned search filter is empty but never null.
     */
    public static AnnounceSearchFilter getAnnounceFilterFromRequest( HttpServletRequest request )
    {
        if ( request == null )
        {
            return new AnnounceSearchFilter(  );
        }

        String strIdFilter = request.getParameter( PARAMETER_ID_FILTER );

        if ( StringUtils.isNotEmpty( strIdFilter ) && StringUtils.isNumeric( strIdFilter ) )
        {
            int nIdFilter = Integer.parseInt( strIdFilter );
            AnnounceSearchFilter filter = AnnounceSearchFilterHome.findByPrimaryKey( nIdFilter );
            request.getSession(  ).setAttribute( SESSION_KEY_ANNOUNCE_FILTER, filter );

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

            DateFormat dateFormat = AnnounceService.getDateFormat(  );

            if ( StringUtils.isNotEmpty( strDateMin ) )
            {
                try
                {
                    formatedDateMin = dateFormat.parse( strDateMin.trim(  ) );
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
                    formatedDateMax = dateFormat.parse( strDateMax.trim(  ) );
                }
                catch ( ParseException e )
                {
                    AppLogService.error( e );
                }
            }

            AnnounceSearchFilter filter = new AnnounceSearchFilter(  );
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
                strPriceMin = strPriceMin.replaceAll( CONSTANT_BLANK_SPACE, StringUtils.EMPTY ).trim(  );

                if ( StringUtils.contains( strPriceMin, CONSTANT_COMA ) )
                {
                    strPriceMin = strPriceMin.substring( 0, strPriceMin.indexOf( CONSTANT_COMA ) );
                }

                if ( StringUtils.contains( strPriceMin, CONSTANT_POINT ) )
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
                strPriceMax = strPriceMax.replaceAll( CONSTANT_BLANK_SPACE, StringUtils.EMPTY ).trim(  );

                if ( StringUtils.contains( strPriceMax, CONSTANT_COMA ) )
                {
                    strPriceMax = strPriceMax.substring( 0, strPriceMax.indexOf( CONSTANT_COMA ) );
                }

                if ( StringUtils.contains( strPriceMax, CONSTANT_POINT ) )
                {
                    strPriceMax = strPriceMax.substring( 0, strPriceMax.indexOf( CONSTANT_POINT ) );
                }

                if ( StringUtils.isNumeric( strPriceMax ) )
                {
                    filter.setPriceMax( Integer.parseInt( strPriceMax ) );
                }
            }

            request.getSession(  ).setAttribute( SESSION_KEY_ANNOUNCE_FILTER, filter );

            return filter;
        }

        AnnounceSearchFilter filter = (AnnounceSearchFilter) request.getSession(  )
                                                                    .getAttribute( SESSION_KEY_ANNOUNCE_FILTER );

        if ( filter == null )
        {
            filter = new AnnounceSearchFilter(  );
        }

        return filter;
    }

    /**
     * Get the URL to search for announces
     * @param request The request
     * @return The URL to search announces
     */
    public static String getUrlSearchAnnounceSort( HttpServletRequest request, int nSort )
    {
        return getUrlSearchAnnounceSort( request, 0 , nSort);
    }
    
    /**
     * Get the URL to search for announces
     * @param request The request
     * @param nIdFilter The id of the filter to load, or 0 to use the filter
     *            stored in session if any
     * @return The URL to search announces
     */
    public static String getUrlSearchAnnounceSort( HttpServletRequest request, int nIdFilter, int nSort)
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + AppPathService.getPortalUrl(  ) );
        urlItem.addParameter( PARAMETER_PAGE, AnnounceUtils.PARAMETER_PAGE_ANNOUNCE );
        urlItem.addParameter( MVCUtils.PARAMETER_ACTION, ACTION_SEARCH );

        if ( nIdFilter > 0 )
        {
            urlItem.addParameter( PARAMETER_ID_FILTER, nIdFilter );
        }
        if(nSort == 0){
        	urlItem.addParameter( "sortBy", "date_creation" );
        }
        if(nSort == 1){
        	urlItem.addParameter( "sortBy", "date_modification" );
        }
        if(nSort == 2){
        	urlItem.addParameter( "sortBy", "title_announce" );
        }
        if(nSort == 3){
        	urlItem.addParameter( "sortBy", "price_announce" );
        }
        if(nSort == 4){
        	urlItem.addParameter( "sortBy", "description_announce" );
        }
       
        return urlItem.getUrl(  );
    }
    
    /**
     * Get the URL to search for announces
     * @param request The request
     * @return The URL to search announces
     */
    public static String getUrlSearchAnnounce( HttpServletRequest request )
    {
        return getUrlSearchAnnounce( request, 0);
    }
    

    /**
     * Get the URL to search for announces
     * @param request The request
     * @param nIdFilter The id of the filter to load, or 0 to use the filter
     *            stored in session if any
     * @return The URL to search announces
     */
    public static String getUrlSearchAnnounce( HttpServletRequest request, int nIdFilter)
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + AppPathService.getPortalUrl(  ) );
        urlItem.addParameter( PARAMETER_PAGE, AnnounceUtils.PARAMETER_PAGE_ANNOUNCE );
        urlItem.addParameter( MVCUtils.PARAMETER_ACTION, ACTION_SEARCH );

        if ( nIdFilter > 0 )
        {
            urlItem.addParameter( PARAMETER_ID_FILTER, nIdFilter );
        }
       
        return urlItem.getUrl(  );
    }

    /**
     * Get the URl to view an announce
     * @param nIdAnnounce The id of the announce to view
     * @return The relative URL to view the announce
     */
    public static String getRelativeUrlViewAnnounce( int nIdAnnounce )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getPortalUrl(  ) );
        urlItem.addParameter( PARAMETER_PAGE, AnnounceUtils.PARAMETER_PAGE_ANNOUNCE );
        urlItem.addParameter( MVCUtils.PARAMETER_ACTION, ACTION_VIEW_ANNOUNCE );
        urlItem.addParameter( PARAMETER_ANNOUNCE_ID, nIdAnnounce );

        return urlItem.getUrl(  );
    }

    /**
     * Get the URL to search for a given category
     * @param request The request
     * @param nIdCategory The of the category to search for
     * @return The URL
     */
    public static String getUrlViewCategory( HttpServletRequest request, int nIdCategory )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + AppPathService.getPortalUrl(  ) );
        urlItem.addParameter( PARAMETER_PAGE, AnnounceUtils.PARAMETER_PAGE_ANNOUNCE );
        urlItem.addParameter( MVCUtils.PARAMETER_ACTION, ACTION_SEARCH );
        urlItem.addParameter( PARAMETER_CATEGORY_ID, nIdCategory );
        urlItem.addParameter( PARAMETER_HAS_FILTER, Boolean.TRUE.toString(  ) );

        return urlItem.getUrl(  );
    }

    /**
     * Get the URL to view announces of a user
     * @param request The request
     * @param strUserName The name of the user to view announces of
     * @return The URL
     */
    public static String getUrlViewUserAnnounces( HttpServletRequest request, String strUserName )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + AppPathService.getPortalUrl(  ) );
        urlItem.addParameter( PARAMETER_PAGE, AnnounceUtils.PARAMETER_PAGE_ANNOUNCE );
        urlItem.addParameter( MVCUtils.PARAMETER_ACTION, ACTION_VIEW_ANNOUNCES );
        urlItem.addParameter( PARAMETER_USERNAME, strUserName );

        return urlItem.getUrl(  );
    }
}
