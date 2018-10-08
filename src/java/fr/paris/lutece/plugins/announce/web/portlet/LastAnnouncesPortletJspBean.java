/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
package fr.paris.lutece.plugins.announce.web.portlet;

import fr.paris.lutece.plugins.announce.business.portlet.LastAnnouncesPortlet;
import fr.paris.lutece.plugins.announce.business.portlet.LastAnnouncesPortletHome;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.portlet.PortletJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage MyAnnouncesPortlet features
 */
public class LastAnnouncesPortletJspBean extends PortletJspBean
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 6187858592847623844L;

    // Marks
    private static final String MARK_DEFAULT_NB_ANNOUNCES_TO_DISPLAY = "defaultNbAnnouncesToDisplay";
    private static final String MARK_PORTLET = "portlet";

    // Parameters
    private static final String PARAMETER_NB_ANNOUNCES_TO_DISPLAY = "nb_announces_to_display";

    // Properties
    private static final String PROPERTY_DEFAULT_NB_ANNOUNCES_TO_DISPLAY = "announce.portletLastAnnounces.defaultNbAnnouncesToDisplay";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCreate( HttpServletRequest request )
    {
        String strPageId = request.getParameter( PARAMETER_PAGE_ID );
        String strPortletTypeId = request.getParameter( PARAMETER_PORTLET_TYPE_ID );
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_DEFAULT_NB_ANNOUNCES_TO_DISPLAY, AppPropertiesService.getProperty( PROPERTY_DEFAULT_NB_ANNOUNCES_TO_DISPLAY ) );

        HtmlTemplate template = getCreateTemplate( strPageId, strPortletTypeId, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModify( HttpServletRequest request )
    {
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        int nPortletId = Integer.parseInt( strPortletId );
        LastAnnouncesPortlet portlet = (LastAnnouncesPortlet) PortletHome.findByPrimaryKey( nPortletId );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_PORTLET, portlet );

        HtmlTemplate template = getModifyTemplate( portlet, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doCreate( HttpServletRequest request )
    {
        LastAnnouncesPortlet portlet = new LastAnnouncesPortlet( );

        // recovers portlet specific attributes
        String strPageId = request.getParameter( PARAMETER_PAGE_ID );
        int nPageId = Integer.parseInt( strPageId );

        // get portlet common attributes
        String strErrorUrl = setPortletCommonData( request, portlet );

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        String strNbItemsToDisplay = request.getParameter( PARAMETER_NB_ANNOUNCES_TO_DISPLAY );

        if ( StringUtils.isEmpty( strNbItemsToDisplay ) || !StringUtils.isNumeric( strNbItemsToDisplay ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nNbAnnouncesToDisplay = Integer.parseInt( strNbItemsToDisplay );

        portlet.setPageId( nPageId );
        portlet.setNbAnnouncesToDisplay( nNbAnnouncesToDisplay );

        // Creates the portlet
        LastAnnouncesPortletHome.getInstance( ).create( portlet );

        // Displays the page with the new Portlet
        return "../" + getPageUrl( nPageId );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doModify( HttpServletRequest request )
    {
        // fetches portlet attributes
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        int nPortletId = Integer.parseInt( strPortletId );
        LastAnnouncesPortlet portlet = (LastAnnouncesPortlet) PortletHome.findByPrimaryKey( nPortletId );

        // retrieve portlet common attributes
        String strErrorUrl = setPortletCommonData( request, portlet );

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        String strNbItemsToDisplay = request.getParameter( PARAMETER_NB_ANNOUNCES_TO_DISPLAY );

        if ( StringUtils.isEmpty( strNbItemsToDisplay ) || !StringUtils.isNumeric( strNbItemsToDisplay ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nNbAnnouncesToDisplay = Integer.parseInt( strNbItemsToDisplay );

        portlet.setNbAnnouncesToDisplay( nNbAnnouncesToDisplay );

        // updates the portlet
        portlet.update( );

        // displays the page with the updated portlet
        return "../" + getPageUrl( portlet.getPageId( ) );
    }
}
