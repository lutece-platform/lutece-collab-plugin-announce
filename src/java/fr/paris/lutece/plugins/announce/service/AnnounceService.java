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

import fr.paris.lutece.plugins.announce.business.Announce;
import fr.paris.lutece.plugins.announce.business.AnnounceHome;
import fr.paris.lutece.plugins.announce.business.Category;
import fr.paris.lutece.plugins.announce.business.CategoryHome;
import fr.paris.lutece.plugins.announce.business.Sector;
import fr.paris.lutece.plugins.announce.business.SectorHome;
import fr.paris.lutece.plugins.announce.service.upload.AnnounceAsynchronousUploadHandler;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenAttFileItem;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeUpload;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.util.file.FileUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Service for announces
 */
public class AnnounceService implements Serializable
{
    /**
     * Pattern for dates
     */
    public static final String PATTERN_DATE = "dd/MM/yyyy";

    /**
     * Name of the bean of the service
     */
    public static final String BEAN_NAME = "announce.announceService";
    private static final long serialVersionUID = 6197939507943704211L;
    private static final String VIEW_GET_FORM = "viewForm";
    private static final String PARAMETER_ID_CATEGORY = "id_form";
    private static final String PREFIX_ATTRIBUTE = "attribute";
    private static final String BEAN_NAME_ENTRY_TYPE_GEOLOCATION = "announce.entryTypeGeolocation";

    // Validation constants
    private static final String ERROR_MESSAGE_MANDATORY_FIELDS = "announce.message.error.mandatory_fields";
    private static final String ERROR_MESSAGE_INVALID_PRICE_FORMAT = "announce.message.error.invalid_price_format";

    // Moderation flags (matches category.announcesValidation DB values)
    private static final int ANNOUNCES_VALIDATION_GLOBAL_PARAMETERS = 0;
    private static final int ANNOUNCES_VALIDATION_YES = 1;
    private static final int ANNOUNCES_VALIDATION_NO = 2;

    // marks
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_ENTRY = "entry";
    private static final String MARK_FIELD = "field";
    private static final String MARK_STR_LIST_CHILDREN = "str_list_entry_children";
    private static final String MARK_CATEGORY = "category";
    private static final String MARK_SECTOR = "sector";
    private static final String MARK_STR_ENTRY = "str_entry";
    private static final String MARK_USER = "user";
    private static final String MARK_LIST_RESPONSES = "list_responses";
    private static final String MARK_UPLOAD_HANDLER = "uploadHandler";

    // Templates
    private static final String TEMPLATE_DIV_CONDITIONAL_ENTRY = "skin/plugins/announce/html_code_div_conditional_entry.html";
    private static final String TEMPLATE_HTML_CODE_FORM = "skin/plugins/announce/html_code_form.html";
    private static final String TEMPLATE_HTML_CODE_FORM_ADMIN = "admin/plugins/announce/html_code_form.html";

    /**
     * Return the HTML code of the form
     * 
     * @param announce
     *            The announce the get the HTML form of, or null to get a default form for the given category. The list of responses of the announce must have
     *            been set if the announce is not null.
     * @param category
     *            the category to display the form of
     * @param locale
     *            the locale
     * @param bDisplayFront
     *            True if the entry will be displayed in Front Office, false if it will be displayed in Back Office.
     * @param request
     *            HttpServletRequest
     * @return the HTML code of the form
     */
    public String getHtmlAnnounceForm( Announce announce, Category category, Locale locale, boolean bDisplayFront, HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<>( );
        StringBuffer strBuffer = new StringBuffer( );
        EntryFilter filter = new EntryFilter( );
        filter.setIdResource( category.getId( ) );
        filter.setResourceType( Category.RESOURCE_TYPE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );

        Map<Integer, List<Response>> mapResponsesByIdEntry = new HashMap<>( );

        if ( announce != null )
        {
            if ( ( announce.getListResponse( ) == null ) && ( announce.getId( ) > 0 ) )
            {
                announce.setListResponse( AnnounceHome.findListResponse( announce.getId( ), true ) );
            }

            if ( announce.getListResponse( ) != null )
            {
                for ( Response response : announce.getListResponse( ) )
                {
                    if ( ( response.getFile( ) != null ) && ( response.getFile( ).getIdFile( ) > 0 ) )
                    {
                        File file = FileHome.findByPrimaryKey( response.getFile( ).getIdFile( ) );

                        if ( ( file == null ) || ( file.getPhysicalFile( ) == null ) )
                        {
                            continue;
                        }

                        PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( file.getPhysicalFile( ).getIdPhysicalFile( ) );

                        if ( physicalFile == null )
                        {
                            continue;
                        }

                        FileItem fileItem = new GenAttFileItem( physicalFile.getValue( ), file.getTitle( ) );
                        AnnounceAsynchronousUploadHandler.getHandler( ).addFileItemToUploadedFilesList( fileItem,
                                IEntryTypeService.PREFIX_ATTRIBUTE + Integer.toString( response.getEntry( ).getIdEntry( ) ), request );
                    }
                }

                for ( Response response : announce.getListResponse( ) )
                {
                    List<Response> listResponse = mapResponsesByIdEntry.get( response.getEntry( ).getIdEntry( ) );

                    if ( listResponse == null )
                    {
                        listResponse = new ArrayList<>( );
                        mapResponsesByIdEntry.put( response.getEntry( ).getIdEntry( ), listResponse );
                    }

                    listResponse.add( response );
                }
            }
        }

        List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );

        for ( Entry entry : listEntryFirstLevel )
        {
            getHtmlEntry( mapResponsesByIdEntry, entry.getIdEntry( ), strBuffer, locale, bDisplayFront, request );
        }

        Sector sector = SectorHome.findByPrimaryKey( category.getIdSector( ) );

        model.put( MARK_CATEGORY, category );
        model.put( MARK_SECTOR, sector );
        model.put( MARK_STR_ENTRY, strBuffer.toString( ) );
        model.put( MARK_LOCALE, locale );

        HtmlTemplate template = AppTemplateService.getTemplate( bDisplayFront ? TEMPLATE_HTML_CODE_FORM : TEMPLATE_HTML_CODE_FORM_ADMIN, locale, model );

        return template.getHtml( );
    }

    /**
     * Insert in the string buffer the content of the HTML code of the entry
     *
     * @param mapResponsesByIdEntry
     *            The map of responses indexed by entry id, or null to use default values
     * @param nIdEntry
     *            the key of the entry which HTML code must be insert in the stringBuffer
     * @param stringBuffer
     *            the buffer which contains the HTML code
     * @param locale
     *            the locale
     * @param bDisplayFront
     *            True if the entry will be displayed in Front Office, false if it will be displayed in Back Office.
     * @param request
     *            HttpServletRequest
     */
    public void getHtmlEntry( Map<Integer, List<Response>> mapResponsesByIdEntry, int nIdEntry, StringBuffer stringBuffer, Locale locale, boolean bDisplayFront, HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<>( );
        StringBuilder strConditionalQuestionStringBuffer = null;
        HtmlTemplate template;
        Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

        if ( Boolean.TRUE.equals( entry.getEntryType( ).getGroup( ) ) )
        {
            StringBuffer strGroupStringBuffer = new StringBuffer( );

            for ( Entry entryChild : entry.getChildren( ) )
            {
                getHtmlEntry( mapResponsesByIdEntry, entryChild.getIdEntry( ), strGroupStringBuffer, locale, bDisplayFront, request );
            }

            model.put( MARK_STR_LIST_CHILDREN, strGroupStringBuffer.toString( ) );
        }
        else
        {
            if ( entry.getNumberConditionalQuestion( ) != 0 )
            {
                for ( Field field : entry.getFields( ) )
                {
                    field.setConditionalQuestions( FieldHome.findByPrimaryKey( field.getIdField( ) ).getConditionalQuestions( ) );
                }
            }
        }

        if ( entry.getNumberConditionalQuestion( ) != 0 )
        {
            strConditionalQuestionStringBuffer = new StringBuilder( );

            for ( Field field : entry.getFields( ) )
            {
                if ( CollectionUtils.isNotEmpty( field.getConditionalQuestions( ) ) )
                {
                    StringBuffer strGroupStringBuffer = new StringBuffer( );

                    for ( Entry entryConditional : field.getConditionalQuestions( ) )
                    {
                        getHtmlEntry( mapResponsesByIdEntry, entryConditional.getIdEntry( ), strGroupStringBuffer, locale, bDisplayFront, request );
                    }

                    model.put( MARK_STR_LIST_CHILDREN, strGroupStringBuffer.toString( ) );
                    model.put( MARK_FIELD, field );
                    template = AppTemplateService.getTemplate( TEMPLATE_DIV_CONDITIONAL_ENTRY, locale, model );
                    strConditionalQuestionStringBuffer.append( template.getHtml( ) );
                }
            }

            model.put( MARK_STR_LIST_CHILDREN, strConditionalQuestionStringBuffer.toString( ) );
        }

        model.put( MARK_ENTRY, entry );
        model.put( MARK_LOCALE, locale );

        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

        if ( ( user == null ) && SecurityService.isAuthenticationEnable( ) && SecurityService.getInstance( ).isExternalAuthentication( ) )
        {
            try
            {
                user = SecurityService.getInstance( ).getRemoteUser( request );
            }
            catch( UserNotSignedException e )
            {
                // Nothing to do : lutece user is not mandatory
            }
        }

        model.put( MARK_USER, user );

        if ( mapResponsesByIdEntry != null )
        {
            List<Response> listResponses = mapResponsesByIdEntry.get( entry.getIdEntry( ) );
            if ( listResponses != null )
            {
                for ( Response response : listResponses )
                {
                    for ( Field filed : entry.getFields( ) )
                    {
                        if ( response.getField( ) != null && filed.getIdField( ) == response.getField( ).getIdField( ) )
                        {
                            response.setField( filed );
                        }
                    }
                }
            }

            model.put( MARK_LIST_RESPONSES, listResponses );
        }

        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( entry );

        // If the entry type is a file, we add the
        if ( entryTypeService instanceof AbstractEntryTypeUpload )
        {
            model.put( MARK_UPLOAD_HANDLER, ( (AbstractEntryTypeUpload) entryTypeService ).getAsynchronousUploadHandler( ) );
        }

        template = AppTemplateService.getTemplate( EntryTypeServiceManager.getEntryTypeService( entry ).getTemplateHtmlForm( entry, bDisplayFront ), locale,
                model );
        stringBuffer.append( template.getHtml( ) );
    }

    /**
     * Get the responses associated with an entry.<br />
     * Return null if there is no error in the response, or return the list of errors. Response created are stored in the map.
     * The key of the map is the id of the entry, and the value the list of responses
     *
     * @param request
     *            the request
     * @param nIdEntry
     *            the key of the entry
     * @param locale
     *            the locale
     * @param mapResponsesByIdEntry
     *            The map to store responses indexed by entry id
     * @return null if there is no error in the response or the list of errors found
     */
    public List<GenericAttributeError> getResponseEntry( HttpServletRequest request, int nIdEntry, Locale locale, Map<Integer, List<Response>> mapResponsesByIdEntry )
    {
        List<Response> listResponse = new ArrayList<>( );
        mapResponsesByIdEntry.put( nIdEntry, listResponse );

        return getResponseEntry( request, nIdEntry, listResponse, false, locale, mapResponsesByIdEntry );
    }

    /**
     * Get the responses associated with an entry.<br />
     * Return null if there is no error in the response, or return the list of errors
     *
     * @param request
     *            the request
     * @param nIdEntry
     *            the key of the entry
     * @param listResponse
     *            The list of response to add responses found in
     * @param bResponseNull
     *            true if the response created must be null
     * @param locale
     *            the locale
     * @param mapResponsesByIdEntry
     *            The map to store responses indexed by entry id
     * @return null if there is no error in the response or the list of errors found
     */
    private List<GenericAttributeError> getResponseEntry( HttpServletRequest request, int nIdEntry, List<Response> listResponse, boolean bResponseNull,
            Locale locale, Map<Integer, List<Response>> mapResponsesByIdEntry )
    {
        List<GenericAttributeError> listFormErrors = new ArrayList<>( );
        Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

        List<Field> listField = new ArrayList<>( );

        for ( Field field : entry.getFields( ) )
        {
            field = FieldHome.findByPrimaryKey( field.getIdField( ) );
            listField.add( field );
        }

        entry.setFields( listField );

        if ( Boolean.TRUE.equals( entry.getEntryType( ).getGroup( ) ) )
        {
            for ( Entry entryChild : entry.getChildren( ) )
            {
                List<Response> listResponseChild = new ArrayList<>( );
                mapResponsesByIdEntry.put( entryChild.getIdEntry( ), listResponseChild );

                listFormErrors.addAll( getResponseEntry( request, entryChild.getIdEntry( ), listResponseChild, false, locale, mapResponsesByIdEntry ) );
            }
        }
        else
            if ( !Boolean.TRUE.equals( entry.getEntryType( ).getComment( ) ) )
            {
                GenericAttributeError formError = null;

                if ( !bResponseNull )
                {
                    formError = EntryTypeServiceManager.getEntryTypeService( entry ).getResponseData( entry, request, listResponse, locale );

                    if ( formError != null )
                    {
                        formError.setUrl( getEntryUrl( entry ) );
                    }
                }
                else
                {
                    Response response = new Response( );
                    response.setEntry( entry );
                    listResponse.add( response );
                }

                if ( formError != null )
                {
                    entry.setError( formError );
                    listFormErrors.add( formError );
                }

                if ( entry.getNumberConditionalQuestion( ) != 0 )
                {
                    for ( Field field : entry.getFields( ) )
                    {
                        boolean bIsFieldInResponseList = isFieldInTheResponseList( field.getIdField( ), listResponse );

                        for ( Entry conditionalEntry : field.getConditionalQuestions( ) )
                        {
                            List<Response> listResponseChild = new ArrayList<>( );
                            mapResponsesByIdEntry.put( conditionalEntry.getIdEntry( ), listResponseChild );

                            listFormErrors.addAll(
                                    getResponseEntry( request, conditionalEntry.getIdEntry( ), listResponseChild, !bIsFieldInResponseList, locale, mapResponsesByIdEntry ) );
                        }
                    }
                }
            }

        return listFormErrors;
    }

    /**
     * Check if a field is in a response list
     * 
     * @param nIdField
     *            the id of the field to search
     * @param listResponse
     *            the list of responses
     * @return true if the field is in the response list, false otherwise
     */
    public Boolean isFieldInTheResponseList( int nIdField, List<Response> listResponse )
    {
        for ( Response response : listResponse )
        {
            if ( ( response.getField( ) != null ) && ( response.getField( ).getIdField( ) == nIdField ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the URL to modify an entry of the form in front office
     * 
     * @param entry
     *            the entry
     * @return The URL to modify the entry in front office
     */
    public String getEntryUrl( Entry entry )
    {
        UrlItem url = new UrlItem( AppPathService.getPortalUrl( ) );
        url.addParameter( XPageAppService.PARAM_XPAGE_APP, AnnouncePlugin.PLUGIN_NAME );
        url.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_GET_FORM );

        if ( ( entry != null ) && ( entry.getIdResource( ) > 0 ) )
        {
            url.addParameter( PARAMETER_ID_CATEGORY, entry.getIdResource( ) );
            url.setAnchor( PREFIX_ATTRIBUTE + entry.getIdEntry( ) );
        }

        return url.getUrl( );
    }

    /**
     * Convert a map of responses indexed by entry id to a flat list of responses.
     *
     * @param mapResponsesByIdEntry
     *            The map to convert
     * @return The flat list of all responses
     */
    public List<Response> convertMapResponseToList( Map<Integer, List<Response>> mapResponsesByIdEntry )
    {
        List<Response> listResponse = new ArrayList<>( );

        for ( List<Response> listResponseByEntry : mapResponsesByIdEntry.values( ) )
        {
            listResponse.addAll( listResponseByEntry );
        }

        return listResponse;
    }

    /**
     * Extract geolocation entries from a list of responses, enriching each response's field
     * with the full field data from the entry.
     *
     * @param listResponses
     *            The list of responses to process (modified in place for field enrichment)
     * @return The list of unique geolocation entries found
     */
    public static List<Entry> extractGeolocationEntries( List<Response> listResponses )
    {
        List<Entry> listGeolocalisation = new ArrayList<>( );

        for ( Response response : listResponses )
        {
            if ( response.getEntry( ) == null || response.getEntry( ).getEntryType( ) == null
                    || !BEAN_NAME_ENTRY_TYPE_GEOLOCATION.equals( response.getEntry( ).getEntryType( ).getBeanName( ) ) )
            {
                continue;
            }

            Entry entry = EntryHome.findByPrimaryKey( response.getEntry( ).getIdEntry( ) );

            for ( Field field : entry.getFields( ) )
            {
                if ( response.getField( ) != null && field.getIdField( ) == response.getField( ).getIdField( ) )
                {
                    response.setField( field );
                }
            }

            boolean bAlreadyAdded = false;

            for ( Entry existingEntry : listGeolocalisation )
            {
                if ( existingEntry.getIdEntry( ) == entry.getIdEntry( ) )
                {
                    bAlreadyAdded = true;
                    break;
                }
            }

            if ( !bAlreadyAdded )
            {
                listGeolocalisation.add( entry );
            }
        }

        return listGeolocalisation;
    }

    /**
     * Parse a price string, handling comma-to-dot conversion.
     *
     * @param strPrice
     *            The price string from the form, may be null or empty
     * @return The parse result with the numeric value and a flag indicating invalid format
     */
    public static PriceParseResult parsePrice( String strPrice )
    {
        if ( StringUtils.isEmpty( strPrice ) )
        {
            return new PriceParseResult( 0.0, false );
        }

        try
        {
            return new PriceParseResult( Double.parseDouble( strPrice.replace( ',', '.' ) ), false );
        }
        catch( NumberFormatException e )
        {
            return new PriceParseResult( 0.0, true );
        }
    }

    /**
     * Validate the mandatory announce form fields and price format.
     *
     * @param strTitle
     *            The announce title
     * @param strDescription
     *            The announce description
     * @param strContact
     *            The contact information
     * @param category
     *            The category (for price validation rules)
     * @param priceResult
     *            The result of price parsing
     * @param locale
     *            The locale for error messages
     * @return The list of validation errors, empty if valid
     */
    public static List<GenericAttributeError> validateAnnounceFormFields( String strTitle, String strDescription, String strContact,
            Category category, PriceParseResult priceResult, Locale locale )
    {
        List<GenericAttributeError> listErrors = new ArrayList<>( );

        if ( StringUtils.isEmpty( strTitle ) || StringUtils.isEmpty( strDescription ) || StringUtils.isEmpty( strContact )
                || ( category.getDisplayPrice( ) && category.getPriceMandatory( ) && ( priceResult.getPrice( ) == 0.0 ) && !priceResult.isInvalidFormat( ) ) )
        {
            GenericAttributeError error = new GenericAttributeError( );
            error.setErrorMessage( I18nService.getLocalizedString( ERROR_MESSAGE_MANDATORY_FIELDS, locale ) );
            listErrors.add( error );
        }

        if ( priceResult.isInvalidFormat( ) )
        {
            GenericAttributeError error = new GenericAttributeError( );
            error.setErrorMessage( I18nService.getLocalizedString( ERROR_MESSAGE_INVALID_PRICE_FORMAT, locale ) );
            listErrors.add( error );
        }

        return listErrors;
    }

    /**
     * Determine whether moderation is required based on category and sector validation settings.
     *
     * @param category
     *            The category
     * @param sector
     *            The sector
     * @return true if the announce requires moderation (should NOT be published), false otherwise
     */
    public static boolean isModerationRequired( Category category, Sector sector )
    {
        switch( category.getAnnouncesValidation( ) )
        {
            case ANNOUNCES_VALIDATION_YES:
                return true;

            case ANNOUNCES_VALIDATION_NO:
                return false;

            case ANNOUNCES_VALIDATION_GLOBAL_PARAMETERS:
            default:
                return sector.getAnnouncesValidation( );
        }
    }

    /**
     * Process form entries for a category: create filter, collect entries, validate responses
     * and convert to a flat response list.
     *
     * @param request
     *            The HTTP request
     * @param nIdCategory
     *            The category id
     * @return The processing result with validation errors and responses
     */
    public FormProcessingResult processFormEntries( HttpServletRequest request, int nIdCategory )
    {
        EntryFilter filter = new EntryFilter( );
        filter.setIdResource( nIdCategory );
        filter.setResourceType( Category.RESOURCE_TYPE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        filter.setIdIsComment( EntryFilter.FILTER_FALSE );

        List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );
        List<GenericAttributeError> listErrors = new ArrayList<>( );
        Map<Integer, List<Response>> mapResponsesByIdEntry = new HashMap<>( );

        for ( Entry entry : listEntryFirstLevel )
        {
            listErrors.addAll( getResponseEntry( request, entry.getIdEntry( ), request.getLocale( ), mapResponsesByIdEntry ) );
        }

        List<Response> listResponses = convertMapResponseToList( mapResponsesByIdEntry );

        return new FormProcessingResult( listErrors, listResponses );
    }

    /**
     * Check whether any response in the list contains an image file.
     *
     * @param listResponses
     *            The list of responses to check
     * @return true if at least one response contains an image
     */
    public static boolean detectHasPictures( List<Response> listResponses )
    {
        for ( Response response : listResponses )
        {
            if ( ( response.getFile( ) != null ) && FileUtil.hasImageExtension( response.getFile( ).getTitle( ) ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the list of sectors with their categories and published announce counts.
     *
     * @return The enriched list of sectors
     */
    public static Collection<Sector> getSectorList( )
    {
        Collection<Sector> listSectors = SectorHome.findAll( );

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
     * Get the list of categories, optionally filtered by sector.
     *
     * @param nIdSector
     *            The sector id, or 0 for all categories
     * @return The list of categories
     */
    public static Collection<Category> getCategoryList( int nIdSector )
    {
        if ( nIdSector == 0 )
        {
            return CategoryHome.findAll( );
        }

        Sector sector = SectorHome.findByPrimaryKey( nIdSector );

        if ( sector == null )
        {
            return Collections.emptyList( );
        }

        return CategoryHome.findCategoriesForSector( sector );
    }

    /**
     * Sort responses according to the hierarchical order of entries in the category form.
     * This ensures conditional question responses appear right after their parent entry responses.
     *
     * @param listResponses
     *            The flat list of responses
     * @param nIdCategory
     *            The id of the category to get the entry hierarchy
     * @return The sorted list of responses
     */
    public static List<Response> sortResponsesByEntryHierarchy( List<Response> listResponses, int nIdCategory )
    {
        // Build the ordered list of entry IDs following the form hierarchy
        List<Integer> listOrderedEntryIds = new ArrayList<>( );
        EntryFilter filter = new EntryFilter( );
        filter.setIdResource( nIdCategory );
        filter.setResourceType( Category.RESOURCE_TYPE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );

        List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );

        for ( Entry entry : listEntryFirstLevel )
        {
            collectEntryIdsRecursive( entry.getIdEntry( ), listOrderedEntryIds );
        }

        // Build a map of position by entry ID
        Map<Integer, Integer> mapEntryOrder = new HashMap<>( );

        for ( int i = 0; i < listOrderedEntryIds.size( ); i++ )
        {
            mapEntryOrder.put( listOrderedEntryIds.get( i ), i );
        }

        // Sort responses based on the entry order
        List<Response> sortedResponses = new ArrayList<>( listResponses );
        sortedResponses.sort( ( r1, r2 ) ->
        {
            int order1 = ( r1.getEntry( ) != null ) ? mapEntryOrder.getOrDefault( r1.getEntry( ).getIdEntry( ), Integer.MAX_VALUE ) : Integer.MAX_VALUE;
            int order2 = ( r2.getEntry( ) != null ) ? mapEntryOrder.getOrDefault( r2.getEntry( ).getIdEntry( ), Integer.MAX_VALUE ) : Integer.MAX_VALUE;

            return Integer.compare( order1, order2 );
        } );

        return sortedResponses;
    }

    /**
     * Recursively collect entry IDs in hierarchical order (entry, then its conditional children).
     *
     * @param nIdEntry
     *            The entry ID to process
     * @param listOrderedEntryIds
     *            The list to add IDs to
     */
    private static void collectEntryIdsRecursive( int nIdEntry, List<Integer> listOrderedEntryIds )
    {
        Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

        if ( entry == null )
        {
            return;
        }

        if ( Boolean.TRUE.equals( entry.getEntryType( ).getGroup( ) ) )
        {
            listOrderedEntryIds.add( nIdEntry );

            for ( Entry child : entry.getChildren( ) )
            {
                collectEntryIdsRecursive( child.getIdEntry( ), listOrderedEntryIds );
            }
        }
        else
        {
            listOrderedEntryIds.add( nIdEntry );

            for ( Field field : entry.getFields( ) )
            {
                Field fullField = FieldHome.findByPrimaryKey( field.getIdField( ) );

                if ( fullField.getConditionalQuestions( ) != null )
                {
                    for ( Entry conditionalEntry : fullField.getConditionalQuestions( ) )
                    {
                        collectEntryIdsRecursive( conditionalEntry.getIdEntry( ), listOrderedEntryIds );
                    }
                }
            }
        }
    }

    /**
     * Get the date format to use
     *
     * @return The date format to use
     */
    public static DateFormat getDateFormat( )
    {
        DateFormat dateFormat = new SimpleDateFormat( PATTERN_DATE, Locale.FRENCH );
        dateFormat.setLenient( false );

        return dateFormat;
    }

    /**
     * Check if the subscribe module (module-announce-subscribe) is available
     *
     * @return true if at least one IAnnounceSubscriptionProvider bean is deployed
     */
    public static boolean isSubscribeModuleAvailable( )
    {
        return !SpringContextService.getBeansOfType( IAnnounceSubscriptionProvider.class ).isEmpty( );
    }

    // -----------------------------------------------------------------------
    // Inner classes for form processing results
    // -----------------------------------------------------------------------

    /**
     * Result of price string parsing.
     */
    public static class PriceParseResult
    {
        private final double _dPrice;
        private final boolean _bInvalidFormat;

        public PriceParseResult( double dPrice, boolean bInvalidFormat )
        {
            _dPrice = dPrice;
            _bInvalidFormat = bInvalidFormat;
        }

        public double getPrice( )
        {
            return _dPrice;
        }

        public boolean isInvalidFormat( )
        {
            return _bInvalidFormat;
        }
    }

    /**
     * Result of form entry processing (generic attributes validation + response collection).
     */
    public static class FormProcessingResult
    {
        private final List<GenericAttributeError> _listErrors;
        private final List<Response> _listResponses;

        public FormProcessingResult( List<GenericAttributeError> listErrors, List<Response> listResponses )
        {
            _listErrors = listErrors;
            _listResponses = listResponses;
        }

        public List<GenericAttributeError> getErrors( )
        {
            return _listErrors;
        }

        public List<Response> getResponses( )
        {
            return _listResponses;
        }

        public boolean hasErrors( )
        {
            return !_listErrors.isEmpty( );
        }
    }
}
