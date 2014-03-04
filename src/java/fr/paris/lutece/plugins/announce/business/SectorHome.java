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
import fr.paris.lutece.util.ReferenceList;

import java.util.Collection;
import java.util.Locale;


/**
 * Home for sectors
 */
public class SectorHome
{
    // Static variable pointed at the DAO instance
    private static ISectorDAO _dao = SpringContextService.getBean( "announce.sectorDAO" );
    private static Plugin _plugin = PluginService.getPlugin( AnnouncePlugin.PLUGIN_NAME );

    /** Creates a new instance of SectorHome */
    public SectorHome( )
    {
    }

    /**
     * Creation of an instance of sector
     * 
     * @param sector The instance of the sector which contains the informations
     *            to store
     * @param plugin The Plugin object
     * @return The instance of sector which has been created with its primary
     *         key.
     */
    public static Sector create( Sector sector, Plugin plugin )
    {
        _dao.insert( sector, plugin );

        return sector;
    }

    /**
     * Update of the sector which is specified in parameter
     * 
     * @param sector The instance of the sector which contains the informations
     *            to store
     * @param plugin The Plugin object
     * @return The instance of the sector which has been updated
     */
    public static Sector update( Sector sector, Plugin plugin )
    {
        _dao.store( sector, plugin );

        return sector;
    }

    /**
     * Remove the Sector whose identifier is specified in parameter
     * 
     * @param sector The Sector object to remove
     * @param plugin The Plugin object
     */
    public static void remove( Sector sector, Plugin plugin )
    {
        _dao.delete( sector, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns an instance of a sector whose identifier is specified in
     * parameter
     * 
     * @param nKey The Primary key of the sector
     * @return An instance of sector
     */
    public static Sector findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Returns a collection of sectors objects
     * @return A collection of sectors
     */
    public static Collection<Sector> findAll( )
    {
        return _dao.selectAll( _plugin );
    }

    /**
     * Returns a referenceList of sectors
     * @return A referenceList of sectors
     */
    public static ReferenceList findReferenceList( )
    {
        return _dao.selectReferenceList( _plugin );
    }

    /**
     * Returns identifier in a distinct order
     * @return The order
     * @param nOrder The order number
     */
    public static int getIdByOrder( int nOrder )
    {
        return _dao.selectIdByOrder( nOrder, _plugin );
    }

    /**
     * Returns the order
     * @param nOrder the order
     * @return the order
     */
    public static int getOrderById( int nOrder )
    {
        return _dao.selectOrderById( nOrder, _plugin );
    }

    /**
     * Update the order of a sector
     * @param nOrder The order of a sector
     * @param nIdSector The id of the sector to update
     */
    public static void updateOrder( int nOrder, int nIdSector )
    {
        _dao.storeOrder( nOrder, nIdSector, _plugin );
    }

    /**
     * Get the maximum order of sectors
     * @return The maximum order of sectors
     */
    public static int getMaxOrderSector( )
    {
        return _dao.selectMaxOrder( _plugin );
    }

    /**
     * Selects the reference list with the i18n string at the top (for front)
     * @param locale The locale
     * @return A referenceList of sectors
     */
    public static ReferenceList findLocaleReferenceList( Locale locale )
    {
        return _dao.selectLocaleReferenceList( _plugin, locale );
    }
}
