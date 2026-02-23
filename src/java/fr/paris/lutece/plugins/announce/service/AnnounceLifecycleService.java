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
import fr.paris.lutece.plugins.announce.business.IndexerAction;
import fr.paris.lutece.plugins.announce.service.announcesearch.AnnounceSearchService;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.resource.ExtendableResourceRemovalListenerService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;

import java.sql.Timestamp;
import java.util.List;

/**
 * Service handling announce lifecycle operations (create, update, remove, publish, suspend).
 * Centralizes cross-cutting concerns: indexation, cache invalidation, publication timestamp, and cleanup.
 */
public class AnnounceLifecycleService
{
    public static final String BEAN_NAME = "announce.announceLifecycleService";

    private Plugin _plugin = PluginService.getPlugin( AnnouncePlugin.PLUGIN_NAME );

    /**
     * Create an announce with indexation and cache invalidation
     *
     * @param announce
     *            The announce to create
     * @return The created announce
     */
    public Announce create( Announce announce )
    {
        announce.setDateModification( new Timestamp( System.currentTimeMillis( ) ) );
        updatePublicationTimestamp( announce );
        AnnounceHome.create( announce );

        if ( isVisible( announce ) )
        {
            AnnounceSearchService.getInstance( ).addIndexerAction( announce.getId( ), IndexerAction.TASK_CREATE, _plugin );
        }

        invalidateCache( announce.getId( ) );

        return announce;
    }

    /**
     * Update an announce with indexation and cache invalidation
     *
     * @param announce
     *            The announce to update
     * @return The updated announce
     */
    public Announce update( Announce announce )
    {
        announce.setDateModification( new Timestamp( System.currentTimeMillis( ) ) );
        updatePublicationTimestamp( announce );
        AnnounceHome.update( announce );

        if ( isVisible( announce ) )
        {
            AnnounceSearchService.getInstance( ).addIndexerAction( announce.getId( ), IndexerAction.TASK_MODIFY, _plugin );
        }
        else
        {
            AnnounceSearchService.getInstance( ).addIndexerAction( announce.getId( ), IndexerAction.TASK_DELETE, _plugin );
        }

        AnnounceCacheService.getService( ).putInCache( AnnounceCacheService.getAnnounceCacheKey( announce.getId( ) ), announce );
        invalidateCache( announce.getId( ) );

        return announce;
    }

    /**
     * Remove an announce with indexation, response cleanup, extend cleanup, workflow cleanup, and cache invalidation
     *
     * @param nIdAnnounce
     *            The id of the announce to remove
     */
    public void remove( int nIdAnnounce )
    {
        AnnounceSearchService.getInstance( ).addIndexerAction( nIdAnnounce, IndexerAction.TASK_DELETE, _plugin );

        List<Integer> listIdResponse = AnnounceHome.findListIdResponse( nIdAnnounce );

        for ( int nIdResponse : listIdResponse )
        {
            ResponseHome.remove( nIdResponse );
        }

        AnnounceHome.removeAnnounceResponse( nIdAnnounce );

        ExtendableResourceRemovalListenerService.doRemoveResourceExtentions( Announce.RESOURCE_TYPE, Integer.toString( nIdAnnounce ) );

        if ( WorkflowService.getInstance( ).isAvailable( ) )
        {
            WorkflowService.getInstance( ).doRemoveWorkFlowResource( nIdAnnounce, Announce.RESOURCE_TYPE );
        }

        AnnounceHome.remove( nIdAnnounce );

        invalidateCache( nIdAnnounce );
    }

    /**
     * Publish or unpublish an announce with indexation and cache invalidation
     *
     * @param announce
     *            The announce (published flag must already be set)
     */
    public void publish( Announce announce )
    {
        updatePublicationTimestamp( announce );
        AnnounceHome.setPublished( announce );

        if ( isVisible( announce ) )
        {
            AnnounceSearchService.getInstance( ).addIndexerAction( announce.getId( ), IndexerAction.TASK_CREATE, _plugin );
        }
        else
        {
            AnnounceSearchService.getInstance( ).addIndexerAction( announce.getId( ), IndexerAction.TASK_DELETE, _plugin );
        }

        invalidateCache( announce.getId( ) );
    }

    /**
     * Suspend or unsuspend an announce by admin with indexation and cache invalidation
     *
     * @param announce
     *            The announce (suspended flag must already be set)
     */
    public void suspendByAdmin( Announce announce )
    {
        updatePublicationTimestamp( announce );
        AnnounceHome.setSuspended( announce );

        if ( isVisible( announce ) )
        {
            AnnounceSearchService.getInstance( ).addIndexerAction( announce.getId( ), IndexerAction.TASK_CREATE, _plugin );
        }
        else
        {
            AnnounceSearchService.getInstance( ).addIndexerAction( announce.getId( ), IndexerAction.TASK_DELETE, _plugin );
        }

        invalidateCache( announce.getId( ) );
    }

    /**
     * Suspend or unsuspend an announce by user with indexation and cache invalidation
     *
     * @param announce
     *            The announce (suspendedByUser flag must already be set)
     */
    public void suspendByUser( Announce announce )
    {
        updatePublicationTimestamp( announce );
        AnnounceHome.setSuspendedByUser( announce );

        if ( isVisible( announce ) )
        {
            AnnounceSearchService.getInstance( ).addIndexerAction( announce.getId( ), IndexerAction.TASK_CREATE, _plugin );
        }
        else
        {
            AnnounceSearchService.getInstance( ).addIndexerAction( announce.getId( ), IndexerAction.TASK_DELETE, _plugin );
        }

        invalidateCache( announce.getId( ) );
    }

    /**
     * Mark an announce as notified with cache invalidation
     *
     * @param announce
     *            The announce
     */
    public void setHasNotified( Announce announce )
    {
        AnnounceHome.setHasNotified( announce );

        AnnounceCacheService.getService( ).removeKey( AnnounceCacheService.getAnnounceCacheKey( announce.getId( ) ) );
    }

    /**
     * Check if an announce is visible (published, not suspended by admin, not suspended by user)
     *
     * @param announce
     *            The announce
     * @return true if visible
     */
    private boolean isVisible( Announce announce )
    {
        return announce.getPublished( ) && !announce.getSuspended( ) && !announce.getSuspendedByUser( );
    }

    /**
     * Update the publication timestamp if the announce is visible
     *
     * @param announce
     *            The announce
     */
    private void updatePublicationTimestamp( Announce announce )
    {
        if ( isVisible( announce ) )
        {
            announce.setTimePublication( System.currentTimeMillis( ) );
        }
    }

    /**
     * Invalidate cache entries for an announce and the published list
     *
     * @param nIdAnnounce
     *            The announce id
     */
    private void invalidateCache( int nIdAnnounce )
    {
        AnnounceCacheService cacheService = AnnounceCacheService.getService( );
        cacheService.removeKey( AnnounceCacheService.getAnnounceCacheKey( nIdAnnounce ) );

        String strPrefix = AnnounceCacheService.getListIdPublishedAnnouncesCacheKeyPrefix( );

        for ( String strKey : cacheService.getKeys( ) )
        {
            if ( strKey.startsWith( strPrefix ) )
            {
                cacheService.removeKey( strKey );
            }
        }
    }
}
