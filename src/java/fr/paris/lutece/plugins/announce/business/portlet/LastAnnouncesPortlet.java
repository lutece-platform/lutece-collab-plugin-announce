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
package fr.paris.lutece.plugins.announce.business.portlet;

import fr.paris.lutece.plugins.announce.business.AnnounceHome;
import fr.paris.lutece.plugins.announce.business.AnnounceSort;
import fr.paris.lutece.plugins.announce.web.AnnounceApp;
import fr.paris.lutece.portal.business.portlet.PortletHtmlContent;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class represents business objects AppointmentPortlet
 */
public class LastAnnouncesPortlet extends PortletHtmlContent
{
    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    private static final String TEMPLATE_PORTLET_CONTENT = "skin/plugins/announce/portletlastannounces_content.html";
    private static final String MARK_PORTLET = "portlet";
    private static final String MARK_CONTENT = "content";
    private int _nNbAnnouncesToDisplay;

    /**
     * Sets the identifier of the portlet type to value specified
     */
    public LastAnnouncesPortlet(  )
    {
        setPortletTypeId( MyAnnouncesPortletHome.getInstance(  ).getPortletTypeId(  ) );
    }

    /**
     * Get the number of announces to display
     * @return The number of announces to display
     */
    public int getNbAnnouncesToDisplay(  )
    {
        return _nNbAnnouncesToDisplay;
    }

    /**
     * Set the number of announces to display
     * @param nNbAnnouncesToDisplay The number of announces to display
     */
    public void setNbAnnouncesToDisplay( int nNbAnnouncesToDisplay )
    {
        this._nNbAnnouncesToDisplay = nNbAnnouncesToDisplay;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHtmlContent( HttpServletRequest request )
    {
        if ( request != null )
        {
            AnnounceSort announceSort = AnnounceSort.getAnnounceSort( AnnounceSort.SORT_DATE_CREATION, false );
            List<Integer> listAllIdAnnounces = AnnounceHome.findAllPublishedId( announceSort );

            String strContent = AnnounceApp.getAnnounceListById( request,
                    ( getNbAnnouncesToDisplay(  ) > listAllIdAnnounces.size(  ) ) ? listAllIdAnnounces
                                                                                  : listAllIdAnnounces.subList( 0,
                        getNbAnnouncesToDisplay(  ) ), announceSort );

            Map<String, Object> model = new HashMap<String, Object>(  );
            model.put( MARK_PORTLET, this );
            model.put( MARK_CONTENT, strContent );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PORTLET_CONTENT, request.getLocale(  ),
                    model );

            return template.getHtml(  );
        }

        return StringUtils.EMPTY;
    }

    /**
     * Updates the current instance of the MyAnnouncePortlet object
     */
    public void update(  )
    {
        LastAnnouncesPortletHome.getInstance(  ).update( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(  )
    {
        LastAnnouncesPortletHome.getInstance(  ).remove( this );
    }
}
