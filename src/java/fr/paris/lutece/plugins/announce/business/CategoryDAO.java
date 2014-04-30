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
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * the DAO class for category
 */
public final class CategoryDAO implements ICategoryDAO
{
    private static final String SQL_QUERY_NEWPK = "SELECT max( id_category ) FROM announce_category ";
    private static final String SQL_QUERY_SELECT = "SELECT id_category, id_sector, label_category, display_price, price_mandatory, announces_validation, id_mailing_list, id_workflow, display_captcha FROM announce_category WHERE id_category = ? ";
    private static final String SQL_QUERY_SELECTALL = "SELECT a.id_category, a.id_sector, a.label_category, b.label_sector FROM announce_category a, announce_sector b WHERE a.id_sector = b.id_sector ORDER BY a.id_sector, a.label_category";
    private static final String SQL_QUERY_INSERT = "INSERT INTO announce_category ( id_category, id_sector, label_category, display_price, price_mandatory, announces_validation, id_mailing_list, id_workflow, display_captcha )  VALUES (?,?,?,?,?,?,?,?,?) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM announce_category WHERE id_category = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE announce_category SET id_sector = ?, label_category = ?, display_price = ?, price_mandatory = ?, announces_validation = ?, id_mailing_list = ?, id_workflow = ?, display_captcha = ? WHERE id_category = ? ";
    private static final String SQL_QUERY_COUNT_ANNOUNCES_FOR_CATEORY = "SELECT COUNT(*) FROM announce_announce WHERE id_category = ?";
    private static final String SQL_QUERY_COUNT_PUBLISHED_ANNOUNCES_FOR_CATEORY = "SELECT COUNT(*) FROM announce_announce WHERE id_category = ? AND published = 1 AND suspended = 0 AND suspended_by_user = 0 ";
    private static final String SQL_QUERY_COUNT_ENTRIES_FOR_CATEGORY = "SELECT COUNT(*) FROM announce_entry WHERE id_category = ?";
    private static final String SQL_QUERY_SELECT_CATEGORIES_FOR_FIELD = "SELECT id_category, id_sector, label_category FROM announce_category WHERE id_sector = ? ORDER BY label_category";
    private static final String SQL_QUERY_SELECT_CATEGORIES_REFERENCELIST = "SELECT id_category, label_category FROM announce_category";

    /**
     * Generates a new primary key
     * @param plugin The plugin
     * @return The new primary key
     */
    private int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEWPK, plugin );
        daoUtil.executeQuery(  );

        int nKey;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;

        daoUtil.free(  );

        return nKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert( Category category, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        category.setId( newPrimaryKey( plugin ) );
        int nIndex = 1;
        daoUtil.setInt( nIndex++, category.getId( ) );
        daoUtil.setInt( nIndex++, category.getIdSector( ) );
        daoUtil.setString( nIndex++, category.getLabel( ) );
        daoUtil.setBoolean( nIndex++, category.getDisplayPrice( ) );
        daoUtil.setBoolean( nIndex++, category.getPriceMandatory( ) );
        daoUtil.setInt( nIndex++, category.getAnnouncesValidation( ) );
        daoUtil.setInt( nIndex++, category.getIdMailingList( ) );
        daoUtil.setBoolean( nIndex++, category.getDisplayCaptcha( ) );
        daoUtil.setInt( nIndex, category.getIdWorkflow( ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Category load( int nCategoryId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nCategoryId );
        daoUtil.executeQuery(  );

        Category category = null;

        if ( daoUtil.next(  ) )
        {
            category = new Category(  );
            int nIndex = 1;
            category.setId( daoUtil.getInt( nIndex++ ) );
            category.setIdSector( daoUtil.getInt( nIndex++ ) );
            category.setLabel( daoUtil.getString( nIndex++ ) );
            category.setDisplayPrice( daoUtil.getBoolean( nIndex++ ) );
            category.setPriceMandatory( daoUtil.getBoolean( nIndex++ ) );
            category.setAnnouncesValidation( daoUtil.getInt( nIndex++ ) );
            category.setIdMailingList( daoUtil.getInt( nIndex++ ) );
            category.setIdWorkflow( daoUtil.getInt( nIndex++ ) );
            category.setDisplayCaptcha( daoUtil.getBoolean( nIndex++ ) );
            category.setNumberAnnounces( countAnnouncesForCategory( category, plugin ) );
        }

        daoUtil.free(  );

        return category;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( Category category, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, category.getId(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( Category category, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;
        daoUtil.setInt( nIndex++, category.getIdSector( ) );
        daoUtil.setString( nIndex++, category.getLabel( ) );
        daoUtil.setBoolean( nIndex++, category.getDisplayPrice( ) );
        daoUtil.setBoolean( nIndex++, category.getPriceMandatory( ) );
        daoUtil.setInt( nIndex++, category.getAnnouncesValidation( ) );
        daoUtil.setInt( nIndex++, category.getIdMailingList( ) );
        daoUtil.setInt( nIndex++, category.getIdWorkflow( ) );
        daoUtil.setBoolean( nIndex++, category.getDisplayCaptcha( ) );
        daoUtil.setInt( nIndex, category.getId( ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Category> selectAll( Plugin plugin )
    {
        List<Category> listCategories = new ArrayList<Category>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Category category = new Category(  );
            category.setId( daoUtil.getInt( 1 ) );
            category.setIdSector( daoUtil.getInt( 2 ) );
            category.setLabel( daoUtil.getString( 3 ) );
            category.setLabelSector( daoUtil.getString( 4 ) );
            category.setNumberAnnounces( countAnnouncesForCategory( category, plugin ) );

            listCategories.add( category );
        }

        daoUtil.free(  );

        return listCategories;
    }

    /**
     * Counts the number of annouces for a specified category
     *
     * @param plugin The plugin
     * @param category The specified category
     * @return The Number of announces
     */
    private int countAnnouncesForCategory( Category category, Plugin plugin )
    {
        int nNumberAnnounces = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_ANNOUNCES_FOR_CATEORY, plugin );
        daoUtil.setInt( 1, category.getId(  ) );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nNumberAnnounces = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nNumberAnnounces;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countPublishedAnnouncesForCategory( Category category, Plugin plugin )
    {
        int nNumberAnnounces = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_PUBLISHED_ANNOUNCES_FOR_CATEORY, plugin );
        daoUtil.setInt( 1, category.getId(  ) );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nNumberAnnounces = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nNumberAnnounces;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countEntriesForCategory( Category category, Plugin plugin )
    {
        int nNumberEntries = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_ENTRIES_FOR_CATEGORY, plugin );
        daoUtil.setInt( 1, category.getId(  ) );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nNumberEntries = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nNumberEntries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Category> selectCategoriesForSector( Sector sector, Plugin plugin )
    {
        List<Category> listCategories = new ArrayList<Category>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_CATEGORIES_FOR_FIELD, plugin );
        daoUtil.setInt( 1, sector.getId(  ) );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Category category = new Category(  );
            category.setId( daoUtil.getInt( 1 ) );
            category.setIdSector( daoUtil.getInt( 2 ) );
            category.setLabel( daoUtil.getString( 3 ) );
            category.setNumberAnnounces( countPublishedAnnouncesForCategory( category, plugin ) );

            listCategories.add( category );
        }

        daoUtil.free(  );

        return listCategories;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList selectCategoriesReferenceList( Plugin plugin )
    {
        ReferenceList listCategories = new ReferenceList(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_CATEGORIES_REFERENCELIST, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            listCategories.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free(  );

        return listCategories;
    }
}
