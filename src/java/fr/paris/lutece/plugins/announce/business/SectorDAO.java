/*
 * Copyright (c) 2002-2018, Mairie de Paris
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

import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

/**
 * DAO implementation to manage sectors
 */
public final class SectorDAO implements ISectorDAO
{
    private static final String SQL_QUERY_NEWPK = "SELECT max( id_sector ) FROM announce_sector ";
    private static final String SQL_QUERY_SELECT = "SELECT id_sector, label_sector, description_sector, announces_validation, sector_order, tags FROM announce_sector WHERE id_sector = ? ";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_sector, label_sector, description_sector, announces_validation, sector_order,tags FROM announce_sector ORDER BY sector_ORDER";
    private static final String SQL_QUERY_INSERT = "INSERT INTO announce_sector ( id_sector, label_sector, description_sector, announces_validation, sector_order, tags )  VALUES (?,?,?,?,?,?) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM announce_sector WHERE id_sector = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE announce_sector SET label_sector = ?, description_sector = ?, announces_validation = ?, tags = ? WHERE id_sector = ?  ";
    private static final String SQL_QUERY_COUNT_CATEGORIES_FOR_FIELD = "SELECT COUNT(*) FROM announce_category WHERE id_sector = ?";

    /* ORDER */
    private static final String SQL_QUERY_SELECT_FIELD_ID_BY_ORDER = "SELECT id_sector FROM announce_sector WHERE sector_order = ? LIMIT 1";
    private static final String SQL_QUERY_SELECT_FIELD_ORDER_BY_ID = "SELECT sector_order FROM announce_sector WHERE id_sector = ? LIMIT 1";
    private static final String SQL_QUERY_UPDATE_FIELD_ORDER = "UPDATE announce_sector SET sector_order = ? WHERE id_sector = ?";
    private static final String SQL_QUERY_SELECT_MAX_ORDER = "SELECT max(sector_order) FROM announce_sector";

    /* PROPERTY */
    private static final String PROPERTY_FIELD_REFERENCE_LIST_TOP_LABEL = "announce.sector.referenceListTopLabel";

    /**
     * Generates a new primary key
     * 
     * @param plugin
     *            The plugin
     * @return The new primary key
     */
    private int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEWPK, plugin );
        daoUtil.executeQuery( );

        int nKey;

        if ( !daoUtil.next( ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;

        daoUtil.free( );

        return nKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert( Sector sector, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        sector.setId( newPrimaryKey( plugin ) );
        daoUtil.setInt( 1, sector.getId( ) );
        daoUtil.setString( 2, sector.getLabel( ) );
        daoUtil.setString( 3, sector.getDescription( ) );
        daoUtil.setBoolean( 4, sector.getAnnouncesValidation( ) );
        daoUtil.setInt( 5, selectMaxOrder( plugin ) + 1 );
        daoUtil.setString( 6, sector.getTags( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sector load( int nIdFIeld, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nIdFIeld );
        daoUtil.executeQuery( );

        Sector sector = null;

        if ( daoUtil.next( ) )
        {
            sector = new Sector( );
            sector.setId( daoUtil.getInt( 1 ) );
            sector.setLabel( daoUtil.getString( 2 ) );
            sector.setDescription( daoUtil.getString( 3 ) );
            sector.setAnnouncesValidation( daoUtil.getBoolean( 4 ) );
            sector.setOrder( daoUtil.getInt( 5 ) );
            sector.setTags( daoUtil.getString( 6 ) );
            sector.setNumberCategories( countCategoriesForSector( sector, plugin ) );
        }

        daoUtil.free( );

        return sector;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( Sector sector, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, sector.getId( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( Sector sector, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setString( 1, sector.getLabel( ) );
        daoUtil.setString( 2, sector.getDescription( ) );
        daoUtil.setBoolean( 3, sector.getAnnouncesValidation( ) );
        daoUtil.setString( 4, sector.getTags( ) );

        daoUtil.setInt( 5, sector.getId( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Sector> selectAll( Plugin plugin )
    {
        Collection<Sector> listSectors = new ArrayList<Sector>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            Sector sector = new Sector( );
            sector.setId( daoUtil.getInt( 1 ) );
            sector.setLabel( daoUtil.getString( 2 ) );
            sector.setDescription( daoUtil.getString( 3 ) );
            sector.setAnnouncesValidation( daoUtil.getBoolean( 4 ) );
            sector.setOrder( daoUtil.getInt( 5 ) );
            sector.setTags( daoUtil.getString( 6 ) );
            sector.setNumberCategories( countCategoriesForSector( sector, plugin ) );

            listSectors.add( sector );
        }

        daoUtil.free( );

        return listSectors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList selectReferenceList( Plugin plugin )
    {
        ReferenceList listSectors = new ReferenceList( );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            ReferenceItem item = new ReferenceItem( );
            item.setCode( daoUtil.getString( 1 ) );
            item.setName( daoUtil.getString( 2 ) );

            listSectors.add( item );
        }

        daoUtil.free( );

        return listSectors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList selectLocaleReferenceList( Plugin plugin, Locale locale )
    {
        ReferenceList frontListSectors = new ReferenceList( );
        frontListSectors.addItem( "0", I18nService.getLocalizedString( PROPERTY_FIELD_REFERENCE_LIST_TOP_LABEL, locale ) );

        ReferenceList listSectors = selectReferenceList( plugin );
        frontListSectors.addAll( listSectors );

        return frontListSectors;
    }

    /**
     * Counts the number of categories for a specified sector
     *
     * @param plugin
     *            The plugin
     * @param sector
     *            The specified sector
     * @return The Number of categories
     */
    private int countCategoriesForSector( Sector sector, Plugin plugin )
    {
        int nNumberCategories = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_CATEGORIES_FOR_FIELD, plugin );

        daoUtil.setInt( 1, sector.getId( ) );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            nNumberCategories = daoUtil.getInt( 1 );
        }

        daoUtil.free( );

        return nNumberCategories;
    }

    // //////////////////////////////////////////////////////////////////////////
    // Order management

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeOrder( int nNewOrder, int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_FIELD_ORDER, plugin );
        daoUtil.setInt( 1, nNewOrder );
        daoUtil.setInt( 2, nId );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int selectIdByOrder( int nOrder, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_FIELD_ID_BY_ORDER, plugin );
        int nResult = 0;
        daoUtil.setInt( 1, nOrder );
        daoUtil.executeQuery( );

        if ( !daoUtil.next( ) )
        {
            // If number order doesn't exist
            nResult = 1;
        }
        else
        {
            nResult = daoUtil.getInt( 1 );
        }

        daoUtil.free( );

        return nResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int selectOrderById( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_FIELD_ORDER_BY_ID, plugin );
        int nResult = 0;
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery( );

        if ( !daoUtil.next( ) )
        {
            // If number order doesn't exist
            nResult = 1;
        }
        else
        {
            nResult = daoUtil.getInt( 1 );
        }

        daoUtil.free( );

        return nResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int selectMaxOrder( Plugin plugin )
    {
        int nOrder = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_MAX_ORDER, plugin );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            nOrder = daoUtil.getInt( 1 );
        }

        daoUtil.free( );

        return nOrder;
    }
}
