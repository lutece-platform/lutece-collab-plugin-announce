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
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;


/**
 * the Home class for categry
 */
public class CategoryHome
{
    // Static variable pointed at the DAO instance
    private static ICategoryDAO _dao = SpringContextService.getBean( "announce.categoryDAO" );

    /** Creates a new instance of CategoryHome */
    public CategoryHome( )
    {
    }

    /**
     * Creation of an instance of category
     * 
     * @param category The instance of the category which contains the
     *            informations to store
     * @param plugin The Plugin object
     * @return The instance of category which has been created with its primary
     *         key.
     */
    public static Category create( Category category, Plugin plugin )
    {
        _dao.insert( category, plugin );

        return category;
    }

    /**
     * Update of the category which is specified in parameter
     * 
     * @param category The instance of the category which contains the
     *            informations to store
     * @param plugin The Plugin object
     * @return The instance of the category which has been updated
     */
    public static Category update( Category category, Plugin plugin )
    {
        _dao.store( category, plugin );

        return category;
    }

    /**
     * Remove the Category whose identifier is specified in parameter
     * 
     * @param category The Category object to remove
     * @param plugin The Plugin object
     */
    public static void remove( Category category, Plugin plugin )
    {
        AnnounceSearchFilterHome.deleteByIdCategory( category.getId( ) );
        _dao.delete( category, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns an instance of a category whose identifier is specified in
     * parameter
     * 
     * @param nKey The Primary key of the category
     * @param plugin The Plugin object
     * @return An instance of category
     */
    public static Category findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _dao.load( nKey, plugin );
    }

    /**
     * Returns a collection of categorys objects
     * @param plugin The Plugin object
     * @return A collection of categorys
     */
    public static List<Category> findAll( Plugin plugin )
    {
        return _dao.selectAll( plugin );
    }

    /**
     * selects the categories list for a given sector
     * @param sector the sector
     * @param plugin the plugin
     * @return the categories list
     */
    public static List<Category> findCategoriesForSector( Sector sector, Plugin plugin )
    {
        return _dao.selectCategoriesForSector( sector, plugin );
    }

    /**
     * gets the categories reference list
     * @param plugin the plugin
     * @return the categories reference list
     */
    public static ReferenceList findCategoriesReferenceList( Plugin plugin )
    {
        return _dao.selectCategoriesReferenceList( plugin );
    }

    /**
     * counts the entries for a given category
     * @param category the category
     * @param plugin the plugin
     * @return the number of entries
     */
    public static int countEntriesForCategory( Category category, Plugin plugin )
    {
        return _dao.countEntriesForCategory( category, plugin );
    }

    /**
     * Count the number of published announce of a given category
     * @param category The category to get the number of published announce of
     * @param plugin The plugin
     * @return The number of published announce of the category
     */
    public static int countPublishedAnnouncesForCategory( Category category, Plugin plugin )
    {
        return _dao.countPublishedAnnouncesForCategory( category, plugin );
    }
}
