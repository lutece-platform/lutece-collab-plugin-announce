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

import fr.paris.lutece.plugins.announce.service.AnnouncePlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 * Home for announce search filters
 */
public class AnnounceSearchFilterHome
{
    private static IAnnounceSearchFilterDAO _dao = SpringContextService.getBean( "announce.announceSearchFilterDAO" );
    private static Plugin _plugin = PluginService.getPlugin( AnnouncePlugin.PLUGIN_NAME );

    /**
     * Find a filter by primary key
     * @param nIdFilter The id of the filter
     * @return The filter
     */
    public static AnnounceSearchFilter findByPrimaryKey( int nIdFilter )
    {
        return _dao.findByPrimaryKey( nIdFilter, _plugin );
    }

    /**
     * Insert a new filter into the database
     * @param filter the filter
     */
    public static void create( AnnounceSearchFilter filter )
    {
        _dao.create( filter, _plugin );
    }

    /**
     * Update a filter
     * @param filter The filter to update
     */
    public static void update( AnnounceSearchFilter filter )
    {
        _dao.update( filter, _plugin );
    }

    /**
     * Remove a filter from the database
     * @param nIdFilter The id of the filter
     */
    public static void delete( int nIdFilter )
    {
        _dao.delete( nIdFilter, _plugin );
    }

    /**
     * Remove filters from the database from the id of categories
     * @param nIdCategory The id of the filter
     */
    public static void deleteByIdCategory( int nIdCategory )
    {
        _dao.deleteByIdCategory( nIdCategory, _plugin );
    }

    /**
     * Get the list of every filters
     * @return the list of every filters
     */
    public List<AnnounceSearchFilter> findAll(  )
    {
        return _dao.findAll( _plugin );
    }

    /**
     * Get the list of filters from a list of id
     * @param listIdFilters The list of id of filters
     * @return the list of filters
     */
    public List<AnnounceSearchFilter> findByListId( List<Integer> listIdFilters )
    {
        return _dao.findByListId( listIdFilters, _plugin );
    }
}
