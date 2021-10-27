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
package fr.paris.lutece.plugins.announce.service;

import fr.paris.lutece.plugins.announce.business.Announce;
import fr.paris.lutece.plugins.announce.business.AnnounceDTO;
import fr.paris.lutece.plugins.announce.business.AnnounceHome;
import fr.paris.lutece.plugins.announce.business.Category;
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
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.fileupload.FileItem;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
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

        AnnounceDTO announceDTO = null;

        if ( announce != null )
        {
            if ( ( announce.getListResponse( ) == null ) && ( announce.getId( ) > 0 ) )
            {
                announce.setListResponse( AnnounceHome.findListResponse( announce.getId( ), true ) );
            }

            announceDTO = new AnnounceDTO( announce );

            if ( announce.getListResponse( ) != null )
            {
                for ( Response response : announce.getListResponse( ) )
                {
                    if ( ( response.getFile( ) != null ) && ( response.getFile( ).getIdFile( ) > 0 ) )
                    {
                        File file = FileHome.findByPrimaryKey( response.getFile( ).getIdFile( ) );
                        PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( file.getPhysicalFile( ).getIdPhysicalFile( ) );
                        FileItem fileItem = new GenAttFileItem( physicalFile.getValue( ), file.getTitle( ) );
                        AnnounceAsynchronousUploadHandler.getHandler( ).addFileItemToUploadedFilesList( fileItem,
                                IEntryTypeService.PREFIX_ATTRIBUTE + Integer.toString( response.getEntry( ).getIdEntry( ) ), request );
                    }
                }

                Map<Integer, List<Response>> mapResponsesByIdEntry = announceDTO.getMapResponsesByIdEntry( );

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
            getHtmlEntry( announceDTO, entry.getIdEntry( ), strBuffer, locale, bDisplayFront, request );
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
     * @param announce
     *            The announce to load current values, or null to use default values
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
    public void getHtmlEntry( AnnounceDTO announce, int nIdEntry, StringBuffer stringBuffer, Locale locale, boolean bDisplayFront, HttpServletRequest request )
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
                getHtmlEntry( announce, entryChild.getIdEntry( ), strGroupStringBuffer, locale, bDisplayFront, request );
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
                        getHtmlEntry( announce, entryConditional.getIdEntry( ), strGroupStringBuffer, locale, bDisplayFront, request );
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

        if ( ( announce != null ) && ( announce.getMapResponsesByIdEntry( ) != null ) )
        {
            List<Response> listResponses = announce.getMapResponsesByIdEntry( ).get( entry.getIdEntry( ) );
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
     * Return null if there is no error in the response, or return the list of errors Response created are stored the map of {@link AnnounceDTO}. The key of the
     * map is this id of the entry, and the value the list of responses
     * 
     * @param request
     *            the request
     * @param nIdEntry
     *            the key of the entry
     * @param locale
     *            the locale
     * @param announce
     *            The announce
     * @return null if there is no error in the response or the list of errors found
     */
    public List<GenericAttributeError> getResponseEntry( HttpServletRequest request, int nIdEntry, Locale locale, AnnounceDTO announce )
    {
        List<Response> listResponse = new ArrayList<>( );
        announce.getMapResponsesByIdEntry( ).put( nIdEntry, listResponse );

        return getResponseEntry( request, nIdEntry, listResponse, false, locale, announce );
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
     * @param announce
     *            The announce
     * @return null if there is no error in the response or the list of errors found
     */
    private List<GenericAttributeError> getResponseEntry( HttpServletRequest request, int nIdEntry, List<Response> listResponse, boolean bResponseNull,
            Locale locale, AnnounceDTO announce )
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
                announce.getMapResponsesByIdEntry( ).put( entryChild.getIdEntry( ), listResponseChild );

                listFormErrors.addAll( getResponseEntry( request, entryChild.getIdEntry( ), listResponseChild, false, locale, announce ) );
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
                            announce.getMapResponsesByIdEntry( ).put( conditionalEntry.getIdEntry( ), listResponseChild );

                            listFormErrors.addAll(
                                    getResponseEntry( request, conditionalEntry.getIdEntry( ), listResponseChild, !bIsFieldInResponseList, locale, announce ) );
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
     * Convert an AppointmentDTO to an Appointment by transferring response from the map of class AppointmentDTO to the list of class Appointment.
     * 
     * @param announce
     *            The announce to get the map to convert
     */
    public void convertMapResponseToList( AnnounceDTO announce )
    {
        List<Response> listResponse = new ArrayList<>( );

        for ( List<Response> listResponseByEntry : announce.getMapResponsesByIdEntry( ).values( ) )
        {
            listResponse.addAll( listResponseByEntry );
        }

        announce.setMapResponsesByIdEntry( null );
        announce.setListResponse( listResponse );
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
}
