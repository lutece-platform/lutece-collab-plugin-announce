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

import fr.paris.lutece.plugins.announce.service.AnnounceCacheService;
import fr.paris.lutece.plugins.announce.service.AnnouncePlugin;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.sql.Timestamp;

import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for Announce objects
 */
public final class AnnounceHome
{
    // Static variable pointed at the DAO instance
    private static IAnnounceDAO _dao = SpringContextService.getBean( "announce.announceDAO" );
    private static Plugin _plugin = PluginService.getPlugin( AnnouncePlugin.PLUGIN_NAME );

    /**
     * Private constructor - this class need not be instantiated
     */
    private AnnounceHome( )
    {
    }

    /**
     * Create an instance of the announce class
     * 
     * @return The instance of announce which has been created with its primary key.
     * @param announce
     *            The instance of the Announce which contains the informations to store
     */
    public static Announce create( Announce announce )
    {
        _dao.insert( announce, _plugin );

        return announce;
    }

    /**
     * Update of the announce which is specified in parameter
     * 
     * @return The instance of the announce which has been updated
     * @param announce
     *            The instance of the Announce which contains the data to store
     */
    public static Announce update( Announce announce )
    {
        _dao.store( announce, _plugin );

        return announce;
    }

    /**
     * Remove the announce whose identifier is specified in parameter and every response associated with it
     * 
     * @param nAnnounceId
     *            The announce Id
     */
    public static void remove( int nAnnounceId )
    {
        _dao.delete( nAnnounceId, _plugin );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a announce whose identifier is specified in parameter
     * 
     * @param nKey
     *            The announce primary key
     * @return an instance of Announce
     */
    public static Announce findByPrimaryKey( int nKey )
    {
        Announce announce = (Announce) AnnounceCacheService.getService( ).getFromCache( AnnounceCacheService.getAnnounceCacheKey( nKey ) );

        if ( announce == null )
        {
            announce = _dao.load( nKey, _plugin );

            if ( announce != null )
            {
                AnnounceCacheService.getService( ).putInCache( AnnounceCacheService.getAnnounceCacheKey( announce.getId( ) ), announce );
            }
        }

        return announce;
    }

    /**
     * @deprecated Use {@link AnnounceResponseHome#findIdByImageResponse(int)} instead
     */
    @Deprecated
    public static Integer findIdByImageResponse( int nIdResponse )
    {
        return AnnounceResponseHome.findIdByImageResponse( nIdResponse );
    }

    /**
     * Load the data of all the announce objects and returns them in form of a list
     * 
     * @param announceSort
     *            The sort
     * @return the list which contains the data of all the announce objects
     */
    public static List<Integer> findAll( AnnounceSort announceSort )
    {
        return _dao.selectAll( announceSort, _plugin );
    }

    /**
     * Load the id of every published announce and returns them in form of a list
     * 
     * @param announceSort
     *            The sort
     * @return the list of id of every published announce
     */
    public static List<Integer> findAllPublishedId( AnnounceSort announceSort )
    {
        String strCacheKey = AnnounceCacheService.getListIdPublishedAnnouncesCacheKey( announceSort.getSortColumn( ), announceSort.getSortAsc( ) );

        @SuppressWarnings( "unchecked" )
        List<Integer> listIds = (List<Integer>) AnnounceCacheService.getService( ).getFromCache( strCacheKey );

        if ( listIds == null )
        {
            listIds = _dao.selectAllPublishedId( announceSort, _plugin );
            AnnounceCacheService.getService( ).putInCache( strCacheKey, listIds );
        }

        return listIds;
    }

    /**
     * Load the data of all the announce objects and returns them in form of a list
     * 
     * @param announceSort
     *            The sort
     * @return the list which contains the data of all the announce objects
     */
    public static List<Announce> findAllPublished( AnnounceSort announceSort )
    {
        return _dao.selectAllPublished( announceSort, _plugin );
    }

    /**
     * Get the list of announces from a list of ids
     * 
     * @param listIdAnnounces
     *            The list of ids of announces to get
     * @param announceSort
     *            The sort
     * @return The list of announces
     */
    public static List<Announce> findByListId( List<Integer> listIdAnnounces, AnnounceSort announceSort )
    {
        return _dao.findByListId( listIdAnnounces, announceSort, _plugin );
    }

    /**
     * selects all the announces for a user
     * 
     * @param user
     *            the user
     * @param announceSort
     *            The sort
     * @return the list of announces
     */
    public static List<Announce> getAnnouncesForUser( LuteceUser user, AnnounceSort announceSort )
    {
        return _dao.selectAllForUser( user.getName( ), announceSort, _plugin );
    }

    /**
     * selects all the announces for a user
     * 
     * @param user
     *            the user name
     * @param announceSort
     *            The sort
     * @return the list of announces
     */
    public static List<Announce> getAnnouncesForUser( String user, AnnounceSort announceSort )
    {
        return _dao.selectAllForUser( user, announceSort, _plugin );
    }

    /**
     * selects all the announces for a category
     * 
     * @param category
     *            the category
     * @param announceSort
     *            The sort
     * @return the list of announces
     */
    public static List<Integer> getPublishedAnnouncesForCategory( Category category, AnnounceSort announceSort )
    {
        return _dao.selectAllPublishedForCategory( category, announceSort, _plugin );
    }

    /**
     * Get the list of ids of all announces for a given category
     *
     * @param nIdCategory
     *            The id of the category
     * @return The list of announce ids
     */
    public static List<Integer> findAllIdByCategory( int nIdCategory )
    {
        return _dao.selectAllIdByCategory( nIdCategory, _plugin );
    }

    /**
     * publish or unpublish an announce
     * 
     * @param announce
     *            the announce
     */
    public static void setPublished( Announce announce )
    {
        _dao.setPublished( announce, _plugin );
    }

    public static void setHasNotified( Announce announce )
    {
        _dao.setHasNotified( announce, _plugin );
    }

    /**
     * suspend or UnSuspend an announce
     * 
     * @param announce
     *            the announce
     */
    public static void setSuspended( Announce announce )
    {
        _dao.setSuspended( announce, _plugin );
    }

    /**
     * suspend or UnSuspend an announce
     * 
     * @param announce
     *            the announce
     */
    public static void setSuspendedByUser( Announce announce )
    {
        _dao.setSuspendedByUser( announce, _plugin );
    }

    /**
     * Get the list of ids of announces that was created before the given date
     * 
     * @param timestamp
     *            The timestamp
     * @return The list of ids
     */
    public static List<Integer> findIdAnnouncesByDateCreation( Timestamp timestamp )
    {
        return _dao.findIdAnnouncesByDateCreation( timestamp, _plugin );
    }

    /**
     * Get the list of ids of announces whose most recent activity (creation or modification) is before the given date
     *
     * @param timestamp
     *            The timestamp
     * @return The list of ids
     */
    public static List<Integer> findIdAnnouncesByLastActivity( Timestamp timestamp )
    {
        return _dao.findIdAnnouncesByLastActivity( timestamp, _plugin );
    }

    /**
     * Get the list of ids of announces that were created after a given time
     * 
     * @param lMinPublicationTime
     *            The minimum publication time of announces to get
     * @return The list of ids of announces
     */
    public static List<Integer> findIdAnnouncesByDatePublication( long lMinPublicationTime )
    {
        return _dao.findIdAnnouncesByDatePublication( lMinPublicationTime, _plugin );
    }

    // -----------------------------------------------
    // Announce response management — deprecated, use AnnounceResponseHome
    // -----------------------------------------------

    /**
     * @deprecated Use {@link AnnounceResponseHome#insertAnnounceResponse(int, int, boolean)} instead
     */
    @Deprecated
    public static void insertAnnounceResponse( int nIdAnnounce, int nIdResponse, boolean bIsImage )
    {
        AnnounceResponseHome.insertAnnounceResponse( nIdAnnounce, nIdResponse, bIsImage );
    }

    /**
     * @deprecated Use {@link AnnounceResponseHome#findListIdResponse(int)} instead
     */
    @Deprecated
    public static List<Integer> findListIdResponse( int nIdAnnounce )
    {
        return AnnounceResponseHome.findListIdResponse( nIdAnnounce );
    }

    /**
     * @deprecated Use {@link AnnounceResponseHome#findListIdImageResponse(int)} instead
     */
    @Deprecated
    public static List<Integer> findListIdImageResponse( int nIdAnnounce )
    {
        return AnnounceResponseHome.findListIdImageResponse( nIdAnnounce );
    }

    /**
     * @deprecated Use {@link AnnounceResponseHome#findListResponse(int, boolean)} instead
     */
    @Deprecated
    public static List<Response> findListResponse( int nIdAnnounce, boolean bLoadFiles )
    {
        return AnnounceResponseHome.findListResponse( nIdAnnounce, bLoadFiles );
    }

    /**
     * @deprecated Use {@link AnnounceResponseHome#removeAnnounceResponse(int)} instead
     */
    @Deprecated
    public static void removeAnnounceResponse( int nIdAnnounce )
    {
        AnnounceResponseHome.removeAnnounceResponse( nIdAnnounce );
    }

}
