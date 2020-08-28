/*
 * Copyright (c) 2002-2020, City of Paris
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
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.TransactionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * the Home class for category
 */
public final class CategoryHome
{
    // Static variable pointed at the DAO instance
    private static ICategoryDAO _dao = SpringContextService.getBean( "announce.categoryDAO" );
    private static Plugin _plugin = PluginService.getPlugin( AnnouncePlugin.PLUGIN_NAME );

    /** Creates a new instance of CategoryHome */
    private CategoryHome( )
    {
    }

    /**
     * Creation of an instance of category
     *
     * @param category
     *            The instance of the category which contains the informations to store
     */
    public static void create( Category category )
    {
        _dao.insert( category, _plugin );
    }

    /**
     * Update of the category which is specified in parameter
     *
     * @param category
     *            The instance of the category which contains the informations to store
     * @return The instance of the category which has been updated
     */
    public static Category update( Category category )
    {
        _dao.store( category, _plugin );
        AnnounceCacheService.getService( ).putInCache( AnnounceCacheService.getCategoryCacheKey( category.getId( ) ), category );

        return category;
    }

    /**
     * Remove the Category whose identifier is specified in parameter
     *
     * @param category
     *            The Category object to remove
     */
    public static void remove( Category category )
    {
        List<Entry> listEntry;
        EntryFilter filter = new EntryFilter( );
        filter.setIdResource( category.getId( ) );
        filter.setResourceType( Category.RESOURCE_TYPE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        listEntry = EntryHome.getEntryList( filter );

        try
        {

            for ( Entry entry : listEntry )
            {
                EntryHome.remove( entry.getIdEntry( ) );
            }

        }
        catch( Exception e )
        {
            throw new AppException( e.getMessage( ), e );
        }

        AnnounceSearchFilterHome.deleteByIdCategory( category.getId( ) );
        _dao.delete( category, _plugin );
        AnnounceCacheService.getService( ).removeKey( AnnounceCacheService.getCategoryCacheKey( category.getId( ) ) );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Returns an instance of a category whose identifier is specified in parameter
     *
     * @param nKey
     *            The Primary key of the category
     * @return An instance of category
     */
    public static Category findByPrimaryKey( int nKey )
    {
        Category category = (Category) AnnounceCacheService.getService( ).getFromCache( AnnounceCacheService.getCategoryCacheKey( nKey ) );

        if ( category == null )
        {
            category = _dao.load( nKey, _plugin );

            if ( category != null )
            {
                AnnounceCacheService.getService( ).putInCache( AnnounceCacheService.getCategoryCacheKey( category.getId( ) ), category );
            }
        }

        return category;
    }

    /**
     * Returns a collection of categories objects
     * 
     * @return A collection of categories
     */
    public static List<Category> findAll( )
    {
        return _dao.selectAll( _plugin );
    }

    /**
     * selects the categories list for a given sector
     * 
     * @param sector
     *            the sector
     * @return the categories list
     */
    public static List<Category> findCategoriesForSector( Sector sector )
    {
        return _dao.selectCategoriesForSector( sector, _plugin );
    }

    /**
     * gets the categories reference list
     * 
     * @return the categories reference list
     */
    public static ReferenceList findCategoriesReferenceList( )
    {
        return _dao.selectCategoriesReferenceList( _plugin );
    }

    /**
     * counts the entries for a given category
     * 
     * @param category
     *            the category
     * @return the number of entries
     */
    public static int countEntriesForCategory( Category category )
    {
        return _dao.countEntriesForCategory( category, _plugin );
    }

    /**
     * Count the number of published announce of a given category
     * 
     * @param category
     *            The category to get the number of published announce of
     * @return The number of published announce of the category
     */
    public static int countPublishedAnnouncesForCategory( Category category )
    {
        return _dao.countPublishedAnnouncesForCategory( category, _plugin );
    }

    /**
     * Copy of an instance of Form
     *
     * @param form
     *            The instance of the Form who must copy
     * @param plugin
     *            the Plugin
     *
     */
    public static void copy( Category category )
    {
        List<Entry> listEntry;
        EntryFilter filter = new EntryFilter( );
        filter.setIdResource( category.getId( ) );
        filter.setResourceType( Category.RESOURCE_TYPE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        listEntry = EntryHome.getEntryList( filter );

        Category cat = new Category( );
        cat.setId( _dao.copyCategory( category, _plugin ) );

        try
        {

            for ( Entry entry : listEntry )
            {
                entry = EntryHome.findByPrimaryKey( entry.getIdEntry( ) );
                entry.setIdResource( cat.getId( ) );
                entry.setResourceType( Category.RESOURCE_TYPE );
                EntryHome.copy( entry );
            }

        }
        catch( Exception e )
        {
            throw new AppException( e.getMessage( ), e );
        }

    }
}
