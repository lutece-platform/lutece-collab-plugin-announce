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
package fr.paris.lutece.plugins.announce.business;

import fr.paris.lutece.plugins.announce.service.AnnouncePlugin;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.ArrayList;
import java.util.List;

/**
 * Home for the announce_announce_response junction table. Manages the association between announces and their generic attribute responses.
 */
public final class AnnounceResponseHome
{
    private static IAnnounceDAO _dao = SpringContextService.getBean( "announce.announceDAO" );
    private static Plugin _plugin = PluginService.getPlugin( AnnouncePlugin.PLUGIN_NAME );

    private AnnounceResponseHome( )
    {
    }

    /**
     * Associates a response to an Announce
     *
     * @param nIdAnnounce
     *            The id of the announce
     * @param nIdResponse
     *            The id of the response
     * @param bIsImage
     *            True if the response is an image, false otherwise
     */
    public static void insertAnnounceResponse( int nIdAnnounce, int nIdResponse, boolean bIsImage )
    {
        _dao.insertAnnounceResponse( nIdAnnounce, nIdResponse, bIsImage, _plugin );
    }

    /**
     * Get the list of id of responses associated with an announce
     *
     * @param nIdAnnounce
     *            the id of the announce
     * @return the list of responses, or an empty list if no response was found
     */
    public static List<Integer> findListIdResponse( int nIdAnnounce )
    {
        return _dao.findListIdResponse( nIdAnnounce, _plugin );
    }

    /**
     * Get the list of id of image responses associated with an announce
     *
     * @param nIdAnnounce
     *            the id of the announce
     * @return the list of responses, or an empty list if no response was found
     */
    public static List<Integer> findListIdImageResponse( int nIdAnnounce )
    {
        return _dao.findListIdImageResponse( nIdAnnounce, _plugin );
    }

    /**
     * Get the list of responses associated with an announce
     *
     * @param nIdAnnounce
     *            the id of the announce
     * @param bLoadFiles
     *            True to load files, false to ignore them. Note that physical files are never loaded by this method.
     * @return the list of responses, or an empty list if no response was found
     */
    public static List<Response> findListResponse( int nIdAnnounce, boolean bLoadFiles )
    {
        List<Integer> listIdResponse = findListIdResponse( nIdAnnounce );
        List<Response> listResponse = new ArrayList<>( listIdResponse.size( ) );

        for ( Integer nIdResponse : listIdResponse )
        {
            Response response = ResponseHome.findByPrimaryKey( nIdResponse );

            if ( response != null )
            {
                if ( bLoadFiles && ( response.getFile( ) != null ) )
                {
                    response.setFile( FileHome.findByPrimaryKey( response.getFile( ).getIdFile( ) ) );
                }

                listResponse.add( response );
            }
        }

        return listResponse;
    }

    /**
     * Remove the association between an announce and responses
     *
     * @param nIdAnnounce
     *            The id of the announce
     */
    public static void removeAnnounceResponse( int nIdAnnounce )
    {
        _dao.deleteAnnounceResponse( nIdAnnounce, _plugin );
    }

    /**
     * Returns the announce id from its image response id
     *
     * @param nIdResponse
     *            the id of the response
     * @return The announce id, or null if there no announce that has this response as an image
     */
    public static Integer findIdByImageResponse( int nIdResponse )
    {
        return _dao.findIdByImageResponse( nIdResponse, _plugin );
    }
}
