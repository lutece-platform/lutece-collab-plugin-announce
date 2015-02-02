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
package fr.paris.lutece.plugins.announce.business;

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.sql.Timestamp;

import java.util.List;


/**
 * IAnnounceDAO Interface
 */
public interface IAnnounceDAO
{
    /**
     * Insert a new record in the table.
     * @param announce instance of the Announce object to insert
     * @param plugin the Plugin
     */
    void insert( Announce announce, Plugin plugin );

    /**
     * Update the record in the table
     * @param announce the reference of the Announce
     * @param plugin the Plugin
     */
    void store( Announce announce, Plugin plugin );

    /**
     * Delete a record from the table
     * @param nIdAnnounce int identifier of the Announce to delete
     * @param plugin the Plugin
     */
    void delete( int nIdAnnounce, Plugin plugin );

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the data from the table
     * @return The instance of the announce
     * @param nKey the primary key
     * @param plugin the Plugin
     */
    Announce load( int nKey, Plugin plugin );

    /**
     * Load the data of all the announce objects and returns them as a List
     * @param announceSort the sort to use
     * @param plugin the Plugin
     * @return The List which contains the data of all the announce objects
     */
    List<Integer> selectAll( AnnounceSort announceSort, Plugin plugin );

    /**
     * selects id of published announces
     * @param announceSort the sort to use
     * @param plugin the plugin
     * @return id of published announces
     */
    List<Integer> selectAllPublishedId( AnnounceSort announceSort, Plugin plugin );

    /**
     * selects all the published announces
     * @param announceSort the sort to use
     * @param plugin the plugin
     * @return announces list
     */
    List<Announce> selectAllPublished( AnnounceSort announceSort, Plugin plugin );

    /**
     * Get the list of announces from a list of ids
     * @param announceSort the sort to use
     * @param listIdAnnounces The list of ids of announces to get
     * @param plugin The plugin
     * @return The list of announces
     */
    List<Announce> findByListId( List<Integer> listIdAnnounces, AnnounceSort announceSort, Plugin plugin );

    /**
     * selects all published announces for a given category
     * @param announceSort the sort to use
     * @param category announces list
     * @param plugin the plugin
     * @return announces list
     */
    List<Integer> selectAllPublishedForCategory( Category category, AnnounceSort announceSort, Plugin plugin );

    /**
     * selects all announces for a given user
     * @param announceSort the sort to use
     * @param strUsername the username
     * @param plugin the plugin
     * @return all announces for a given user
     */
    List<Announce> selectAllForUser( String strUsername, AnnounceSort announceSort, Plugin plugin );

    /**
     * publish or unpublish an announce
     * @param announce the announce
     * @param plugin the plugin
     */
    void setPublished( Announce announce, Plugin plugin );

    /**
     * suspend or enable an announce
     * @param announce the announce
     * @param plugin the plugin
     */
    void setSuspended( Announce announce, Plugin plugin );

    /**
     * suspend or enable an announce
     * @param announce the announce
     * @param plugin the plugin
     */
    void setSuspendedByUser( Announce announce, Plugin plugin );

    /**
     * Get the list of ids of announces that was created before the given date
     * @param timestamp The timestamp
     * @param plugin The plugin
     * @return The list of ids
     */
    List<Integer> findIdAnnouncesByDateCreation( Timestamp timestamp, Plugin plugin );

    // ----------------------------------------
    // Announce response management
    // ----------------------------------------

    /**
     * Associates a response to an Announce
     * @param nIdAnnounce The id of the Announce
     * @param nIdResponse The id of the response
     * @param bIsImage True if the response is an image, false otherwise
     * @param plugin The plugin
     */
    void insertAnnounceResponse( int nIdAnnounce, int nIdResponse, boolean bIsImage, Plugin plugin );

    /**
     * Get the list of id of responses associated with an announce
     * @param nIdAnnounce the id of the announce
     * @param plugin the plugin
     * @return the list of responses, or an empty list if no response was found
     */
    List<Integer> findListIdResponse( int nIdAnnounce, Plugin plugin );

    /**
     * Get the list of id of image responses associated with an announce
     * @param nIdAnnounce the id of the announce
     * @param plugin the plugin
     * @return the list of responses, or an empty list if no response was found
     */
    List<Integer> findListIdImageResponse( int nIdAnnounce, Plugin plugin );

    /**
     * Remove the association between an announce and responses
     * @param nIdAnnounce The id of the announce
     * @param plugin The plugin
     */
    void deleteAnnounceResponse( int nIdAnnounce, Plugin plugin );

    /**
     * Get the list of ids of announces that were created after a given time
     * @param lMinPublicationTime The minimum publication time of announces to
     *            get
     * @param plugin The plugin
     * @return The list of ids of announces
     */
    List<Integer> findIdAnnouncesByDatePublication( long lMinPublicationTime, Plugin plugin );
}
