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
package fr.paris.lutece.plugins.announce.business.portlet;

import fr.paris.lutece.portal.business.portlet.Portlet;

/**
 * this class provides Data Access methods for MyAnnouncePortlet objects
 */
public final class MyAnnouncesPortletDAO implements IMyAnnouncesPortletDAO
{
    /**
     * Insert a new record in the table.
     *
     * @param portlet
     *            The Instance of the Portlet
     */
    @Override
    public void insert( Portlet portlet )
    {
        // Do nothing
    }

    /**
     * Delete record from table
     * 
     * @param nPortletId
     *            The identifier of the Portlet
     */
    @Override
    public void delete( int nPortletId )
    {
        // Do nothing
    }

    /**
     * Update the record in the table
     * 
     * @param portlet
     *            The reference of the portlet
     */
    @Override
    public void store( Portlet portlet )
    {
        // Do nothing
    }

    /**
     * Load the data of the portlet from the table
     * 
     * @return portlet The instance of the object portlet
     * @param nIdPortlet
     *            The identifier of the portlet
     */
    @Override
    public Portlet load( int nIdPortlet )
    {
        MyAnnouncesPortlet portlet = new MyAnnouncesPortlet( );
        portlet.setId( nIdPortlet );

        return portlet;
    }
}
