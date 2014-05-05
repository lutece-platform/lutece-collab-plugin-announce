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
import fr.paris.lutece.util.ReferenceList;

import java.util.Collection;
import java.util.Locale;


/**
 *
 * @author kpomdagu
 */
public interface ISectorDAO
{
    /**
     * Delete a record from the table
     *
     * @param sector The sector object
     * @param plugin The plugin
     */
    void delete( Sector sector, Plugin plugin );

    /**
     * Insert a new record in the table.
     * @param sector The Sector object
     * @param plugin The plugin
     */
    void insert( Sector sector, Plugin plugin );

    /**
     * Load the data of Sector from the table
     * @param nSectorId The identifier of Sector
     * @param plugin The plugin
     * @return the instance of the Sector
     */
    Sector load( int nSectorId, Plugin plugin );

    /**
     * Load the list of sectors
     *
     *
     * @param plugin The plugin
     * @return The Collection of the Sectors
     */
    Collection<Sector> selectAll( Plugin plugin );

    /**
     * Update the record in the table
     * @param sector The reference of sector
     * @param plugin The plugin
     */
    void store( Sector sector, Plugin plugin );

    /**
     * Returns a referenceList of sectors
     * @param plugin The Plugin object
     * @return A referenceList of sectors
     */
    ReferenceList selectReferenceList( Plugin plugin );

    /**
     * selects the reference list with the i18n string at the top (for front)
     *
     * @param plugin The plugin
     * @param locale The locale
     * @return A referenceList of sectors
     */
    ReferenceList selectLocaleReferenceList( Plugin plugin, Locale locale );

    //////////////////////////////// Order management

    /**
     * Update the order of a sector
     * @param nNewOrder The order of a sector
     * @param nId The id of the sector to update
     * @param plugin The plugin
     */
    void storeOrder( int nNewOrder, int nId, Plugin plugin );

    /**
     * Returns identifier in a distinct order
     * @return The order
     * @param nOrder The order number
     * @param plugin The plugin
     */
    int selectIdByOrder( int nOrder, Plugin plugin );

    /**
     * Returns the order
     * @param nId the id
     * @param plugin the plugin
     * @return the order
     */
    int selectOrderById( int nId, Plugin plugin );

    /**
     * Get the maximum order of sectors
     * @param plugin The plugin
     * @return The maximum order of sectors
     */
    int selectMaxOrder( Plugin plugin );
}
