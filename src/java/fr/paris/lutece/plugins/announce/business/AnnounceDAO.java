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
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.util.sql.DAOUtil;
import fr.paris.lutece.util.sql.Transaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for Announce objects
 */
public final class AnnounceDAO implements IAnnounceDAO
{
    private static final String DEFAULT_ORDER_BY = " ORDER BY a.date_creation DESC ";

    // Select id
    private static final String SQL_QUERY_SELECT_ID = "SELECT a.id_announce FROM announce_announce a, announce_category b WHERE a.id_category = b.id_category";
    private static final String SQL_QUERY_SELECTALL_ID_PUBLISHED = SQL_QUERY_SELECT_ID
            + " AND a.published = 1 AND a.suspended = 0 AND a.suspended_by_user = 0 " + DEFAULT_ORDER_BY;
    private static final String SQL_QUERY_SELECTALL = SQL_QUERY_SELECT_ID + DEFAULT_ORDER_BY;
    private static final String SQL_QUERY_SELECTALL_PUBLISHED_FOR_CATEGORY = "SELECT a.id_announce FROM announce_announce a WHERE a.id_category = ? AND a.published = 1 AND a.suspended = 0 AND a.suspended_by_user = 0 "
            + DEFAULT_ORDER_BY;

    // New primary key
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_announce ) FROM announce_announce";

    // Select
    private static final String SQL_QUERY_SELECT_FIELD_LIST_WITH_CATEGORY = "SELECT a.id_announce, a.title_announce, a.description_announce, a.price_announce, a.date_creation, a.user_name, a.contact_information, a.published, a.suspended, a.suspended_by_user, a.tags, a.has_pictures, a.id_category,b.label_category, b.display_price FROM announce_announce a, announce_category b";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_FIELD_LIST_WITH_CATEGORY
            + " WHERE a.id_announce = ? AND a.id_category = b.id_category " + DEFAULT_ORDER_BY;
    private static final String SQL_QUERY_SELECTALL_PUBLISHED = SQL_QUERY_SELECT_FIELD_LIST_WITH_CATEGORY
            + " WHERE a.id_category = b.id_category AND a.published = 1 AND a.suspended = 0 AND a.suspended_by_user = 0 "
            + DEFAULT_ORDER_BY;
    private static final String SQL_QEURY_SELECT_BY_LIST_ID = SQL_QUERY_SELECT_FIELD_LIST_WITH_CATEGORY
            + " WHERE a.id_announce IN (";
    private static final String SQL_QUERY_SELECTALL_ANNOUNCES_FOR_USER = SQL_QUERY_SELECT_FIELD_LIST_WITH_CATEGORY
            + " WHERE a.id_category = b.id_category AND a.user_name = ? " + DEFAULT_ORDER_BY;

    // insert, delete
    private static final String SQL_QUERY_INSERT = "INSERT INTO announce_announce ( id_announce, user_name, contact_information, id_category, title_announce, description_announce, price_announce, date_creation, published, tags, has_pictures ) VALUES (?,?,?,?,?,?,?,?,?,?,?) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM announce_announce WHERE id_announce = ? ";

    // Update
    private static final String SQL_QUERY_UPDATE = "UPDATE announce_announce SET title_announce = ?, description_announce = ?, price_announce = ?, contact_information = ?, published = ?, tags = ?, has_pictures = ? WHERE id_announce = ?";
    private static final String SQL_QUERY_SET_PUBLISHED = "UPDATE announce_announce SET published = ? WHERE id_announce = ?";
    private static final String SQL_QUERY_SET_SUSPENDED = "UPDATE announce_announce SET suspended = ? WHERE id_announce = ?";
    private static final String SQL_QUERY_SET_SUSPENDED_BY_USER = "UPDATE announce_announce SET suspended_by_user = ? WHERE id_announce = ?";

    // SQL commands to manage announce responses
    private static final String SQL_QUERY_INSERT_ANNOUNCE_RESPONSE = "INSERT INTO announce_announce_response (id_announce, id_response, is_image) VALUES (?,?,?)";
    private static final String SQL_QUERY_SELECT_ANNOUNCE_RESPONSE_LIST = "SELECT id_response FROM announce_announce_response WHERE id_announce = ?";
    private static final String SQL_QUERY_SELECT_ANNOUNCE_IMAGE_RESPONSE_LIST = SQL_QUERY_SELECT_ANNOUNCE_RESPONSE_LIST
            + " AND is_image = ?";
    private static final String SQL_QUERY_DELETE_ANNOUNCE_RESPONSE = "DELETE FROM announce_announce_response WHERE id_announce = ?";

    // Constants
    private static final String CONSTANT_COMA = ",";
    private static final String CONSTANT_CLOSE_PARENTHESIS = ")";

    /**
     * Generates a new primary key
     * @param plugin The Plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
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
    public void insert( Announce announce, Plugin plugin )
    {
        announce.setId( newPrimaryKey( plugin ) );

        int nParam = 1;
        Transaction transaction = new Transaction( plugin );

        try
        {
            /* Creation of Announce */
            transaction.prepareStatement( SQL_QUERY_INSERT );
            transaction.getStatement( ).setInt( nParam++, announce.getId( ) );
            transaction.getStatement( ).setString( nParam++, announce.getUserName( ) );
            transaction.getStatement( ).setString( nParam++, announce.getContactInformation( ) );
            transaction.getStatement( ).setInt( nParam++, announce.getCategory( ).getId( ) );
            transaction.getStatement( ).setString( nParam++, announce.getTitle( ) );
            transaction.getStatement( ).setString( nParam++, announce.getDescription( ) );
            transaction.getStatement( ).setString( nParam++, announce.getPrice( ) );
            transaction.getStatement( ).setTimestamp( nParam++, announce.getDateCreation( ) );
            transaction.getStatement( ).setBoolean( nParam++, announce.getPublished( ) );
            transaction.getStatement( ).setString( nParam++, announce.getTags( ) );
            transaction.getStatement( ).setBoolean( nParam, announce.getHasPictures( ) );
            transaction.executeStatement( );

            /* COMMIT of all the transaction */
            transaction.commit( );
        }
        catch ( SQLException ex )
        {
            transaction.rollback( ex );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Announce load( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery( );

        Announce announce = null;

        if ( daoUtil.next( ) )
        {
            announce = getAnnounceWithCategory( daoUtil );
        }

        daoUtil.free( );

        return announce;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nAnnounceId, Plugin plugin )
    {
        Transaction transaction = new Transaction( plugin );

        try
        {
            /* Delete Announce */
            transaction.prepareStatement( SQL_QUERY_DELETE );
            transaction.getStatement( ).setInt( 1, nAnnounceId );
            transaction.executeStatement( );

            /* COMMIT of all the transaction */
            transaction.commit( );
        }
        catch ( SQLException ex )
        {
            transaction.rollback( ex );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( Announce announce, Plugin plugin )
    {
        int nIndex = 1;
        Transaction transaction = new Transaction( plugin );

        try
        {
            /* Update of Announce */
            transaction.prepareStatement( SQL_QUERY_UPDATE );
            transaction.getStatement( ).setString( nIndex++, announce.getTitle( ) );
            transaction.getStatement( ).setString( nIndex++, announce.getDescription( ) );
            transaction.getStatement( ).setString( nIndex++, announce.getPrice( ) );
            transaction.getStatement( ).setString( nIndex++, announce.getContactInformation( ) );
            transaction.getStatement( ).setBoolean( nIndex++, announce.getPublished( ) );
            transaction.getStatement( ).setString( nIndex++, announce.getTags( ) );
            transaction.getStatement( ).setBoolean( nIndex++, announce.getHasPictures( ) );

            transaction.getStatement( ).setInt( nIndex, announce.getId( ) );
            transaction.executeStatement( );

            /* COMMIT of all the transaction */
            transaction.commit( );
        }
        catch ( SQLException ex )
        {
            transaction.rollback( ex );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> selectAll( Plugin plugin )
    {
        List<Integer> announceList = new ArrayList<Integer>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            announceList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );

        return announceList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> selectAllPublishedId( Plugin plugin )
    {
        List<Integer> listIdAnnounce = new ArrayList<Integer>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID_PUBLISHED, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            listIdAnnounce.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );

        return listIdAnnounce;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Announce> selectAllPublished( Plugin plugin )
    {
        List<Announce> announceList = new ArrayList<Announce>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_PUBLISHED, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            announceList.add( getAnnounceWithCategory( daoUtil ) );
        }

        daoUtil.free( );

        return announceList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Announce> findByListId( List<Integer> listIdAnnounces, Plugin plugin )
    {
        List<Announce> announceList = new ArrayList<Announce>( );
        if ( listIdAnnounces == null || listIdAnnounces.size( ) == 0 )
        {
            return announceList;
        }

        StringBuilder sbSql = new StringBuilder( SQL_QEURY_SELECT_BY_LIST_ID );
        boolean bIsFirst = true;
        for ( Integer nId : listIdAnnounces )
        {
            if ( !bIsFirst )
            {
                sbSql.append( CONSTANT_COMA );
            }
            else
            {
                bIsFirst = false;
            }
            sbSql.append( nId );
        }
        sbSql.append( CONSTANT_CLOSE_PARENTHESIS );
        sbSql.append( DEFAULT_ORDER_BY );

        DAOUtil daoUtil = new DAOUtil( sbSql.toString( ), plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            announceList.add( getAnnounceWithCategory( daoUtil ) );
        }

        daoUtil.free( );

        return announceList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> selectAllPublishedForCategory( Category category, Plugin plugin )
    {
        List<Integer> announceList = new ArrayList<Integer>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_PUBLISHED_FOR_CATEGORY, plugin );
        daoUtil.setInt( 1, category.getId( ) );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            announceList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );

        return announceList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Announce> selectAllForUser( LuteceUser user, Plugin plugin )
    {
        return selectAllForUser( user.getName( ), plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Announce> selectAllForUser( String strUsername, Plugin plugin )
    {
        List<Announce> announceList = new ArrayList<Announce>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ANNOUNCES_FOR_USER, plugin );
        daoUtil.setString( 1, strUsername );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            announceList.add( getAnnounceWithCategory( daoUtil ) );
        }

        daoUtil.free( );

        return announceList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPublished( Announce announce, Plugin plugin )
    {
        int nParam = 1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SET_PUBLISHED, plugin );
        daoUtil.setBoolean( nParam++, announce.getPublished( ) );
        daoUtil.setInt( nParam, announce.getId( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSuspended( Announce announce, Plugin plugin )
    {
        int nParam = 1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SET_SUSPENDED, plugin );
        daoUtil.setBoolean( nParam++, announce.getSuspended( ) );
        daoUtil.setInt( nParam, announce.getId( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSuspendedByUser( Announce announce, Plugin plugin )
    {
        int nParam = 1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SET_SUSPENDED_BY_USER, plugin );
        daoUtil.setBoolean( nParam++, announce.getSuspendedByUser( ) );
        daoUtil.setInt( nParam, announce.getId( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    // ----------------------------------------
    // Appointment response management
    // ----------------------------------------

    /**
     * {@inheritDoc }
     */
    @Override
    public void insertAnnounceResponse( int nIdAnnounce, int nIdResponse, boolean bIsImage, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_ANNOUNCE_RESPONSE, plugin );
        daoUtil.setInt( 1, nIdAnnounce );
        daoUtil.setInt( 2, nIdResponse );
        daoUtil.setBoolean( 3, bIsImage );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> findListIdResponse( int nIdAnnounce, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ANNOUNCE_RESPONSE_LIST, plugin );
        daoUtil.setInt( 1, nIdAnnounce );
        daoUtil.executeQuery( );

        List<Integer> listIdResponse = new ArrayList<Integer>( );

        while ( daoUtil.next( ) )
        {
            listIdResponse.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );

        return listIdResponse;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> findListIdImageResponse( int nIdAnnounce, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ANNOUNCE_IMAGE_RESPONSE_LIST, plugin );
        daoUtil.setInt( 1, nIdAnnounce );
        daoUtil.setBoolean( 2, Boolean.TRUE );
        daoUtil.executeQuery( );

        List<Integer> listIdResponse = new ArrayList<Integer>( );

        while ( daoUtil.next( ) )
        {
            listIdResponse.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );

        return listIdResponse;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteAnnounceResponse( int nIdAnnounce, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ANNOUNCE_RESPONSE, plugin );
        daoUtil.setInt( 1, nIdAnnounce );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Get an announce with its category. Only the id, the label and the price
     * boolean of the category is loaded.
     * @param daoUtil The daoUtil to load data from. Note that the cursor of the
     *            daoUtil will NOT be moved by this method, and that the call to
     *            the daoUtil.free( ) will NOT be performed.
     * @return
     */
    private Announce getAnnounceWithCategory( DAOUtil daoUtil )
    {
        Announce announce = new Announce( );
        Category category = new Category( );

        int nIndex = 1;
        announce.setId( daoUtil.getInt( nIndex++ ) );
        announce.setTitle( daoUtil.getString( nIndex++ ) );
        announce.setDescription( daoUtil.getString( nIndex++ ) );
        announce.setPrice( daoUtil.getString( nIndex++ ) );
        announce.setDateCreation( daoUtil.getTimestamp( nIndex++ ) );
        announce.setUserName( daoUtil.getString( nIndex++ ) );
        announce.setContactInformation( daoUtil.getString( nIndex++ ) );
        announce.setPublished( daoUtil.getBoolean( nIndex++ ) );
        announce.setSuspended( daoUtil.getBoolean( nIndex++ ) );
        announce.setSuspendedByUser( daoUtil.getBoolean( nIndex++ ) );
        announce.setTags( daoUtil.getString( nIndex++ ) );
        announce.setHasPictures( daoUtil.getBoolean( nIndex++ ) );

        category.setId( daoUtil.getInt( nIndex++ ) );
        category.setLabel( daoUtil.getString( nIndex++ ) );
        category.setDisplayPrice( daoUtil.getBoolean( nIndex++ ) );

        announce.setCategory( category );
        return announce;
    }
}
