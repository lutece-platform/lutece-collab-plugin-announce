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
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for Announce objects
 */
public final class AnnounceDAO implements IAnnounceDAO
{
    private static final String ORDER_BY = " ORDER BY ";
    private static final String ORDER_BY_ASCENDING = " ASC ";
    private static final String ORDER_BY_DESCENDING = " DESC ";

    // Select id
    private static final String SQL_QUERY_SELECT_ID = "SELECT a.id_announce FROM announce_announce a, announce_category b WHERE a.id_category = b.id_category";
    private static final String SQL_QUERY_SELECTALL_ID_PUBLISHED = SQL_QUERY_SELECT_ID +
        " AND a.published = 1 AND a.suspended = 0 AND a.suspended_by_user = 0 ";
    private static final String SQL_QUERY_SELECTALL = SQL_QUERY_SELECT_ID;
    private static final String SQL_QUERY_SELECTALL_PUBLISHED_FOR_CATEGORY = "SELECT a.id_announce FROM announce_announce a WHERE a.id_category = ? AND a.published = 1 AND a.suspended = 0 AND a.suspended_by_user = 0 ";
    private static final String SQL_QUERY_SELECT_ID_BY_DATE_CREATION = "SELECT id_announce FROM announce_announce WHERE date_creation < ?";
    private static final String SQL_QUERY_SELECT_ID_BY_TIME_PUBLICATION = "SELECT id_announce FROM announce_announce WHERE publication_time > ? ";
    
    // New primary key
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_announce ) FROM announce_announce";

    // Select
    private static final String SQL_QUERY_SELECT_FIELD_LIST_WITH_CATEGORY = "SELECT a.id_announce, a.title_announce, a.description_announce, a.price_announce, a.date_creation, a.date_modification, a.user_name, a.user_lastname, a.user_secondname, a.contact_information, a.published, a.suspended, a.suspended_by_user, a.tags, a.has_pictures, a.publication_time, a.has_notifed, a.id_category, b.label_category, b.display_price, b.id_sector  FROM announce_announce a, announce_category b WHERE a.id_category = b.id_category ";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_FIELD_LIST_WITH_CATEGORY +
        " AND a.id_announce = ? ";
    private static final String SQL_QUERY_SELECTALL_PUBLISHED = SQL_QUERY_SELECT_FIELD_LIST_WITH_CATEGORY +
        "AND a.published = 1 AND a.suspended = 0 AND a.suspended_by_user = 0 ";
    private static final String SQL_QEURY_SELECT_BY_LIST_ID = SQL_QUERY_SELECT_FIELD_LIST_WITH_CATEGORY +
        " AND a.id_announce IN (";
    private static final String SQL_QUERY_SELECTALL_ANNOUNCES_FOR_USER = SQL_QUERY_SELECT_FIELD_LIST_WITH_CATEGORY +
        " AND a.user_name = ? ";

    // insert, delete
    private static final String SQL_QUERY_INSERT = "INSERT INTO announce_announce ( id_announce, user_name, user_lastname, user_secondname, contact_information, id_category, title_announce, description_announce, price_announce, date_creation, date_modification, published, tags, has_pictures, publication_time, has_notifed) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM announce_announce WHERE id_announce = ? ";

    // Update
    private static final String SQL_QUERY_UPDATE = "UPDATE announce_announce SET title_announce = ?, description_announce = ?, price_announce = ?, contact_information = ?, published = ?, tags = ?, has_pictures = ?, date_modification = ?, has_notified = ? WHERE id_announce = ?";
    
    private static final String SQL_QUERY_SET_PUBLISHED = "UPDATE announce_announce SET published = ?, publication_time = ? WHERE id_announce = ?";
    private static final String SQL_QUERY_SET_HASNOTIFED = "UPDATE announce_announce SET has_notifed = ?  WHERE id_announce = ?";
    private static final String SQL_QUERY_SET_SUSPENDED = "UPDATE announce_announce SET suspended = ?, publication_time = ? WHERE id_announce = ?";
    private static final String SQL_QUERY_SET_SUSPENDED_BY_USER = "UPDATE announce_announce SET suspended_by_user = ?, publication_time = ? WHERE id_announce = ?";

    // SQL commands to manage announce responses
    private static final String SQL_QUERY_INSERT_ANNOUNCE_RESPONSE = "INSERT INTO announce_announce_response (id_announce, id_response, is_image) VALUES (?,?,?)";
    private static final String SQL_QUERY_SELECT_ANNOUNCE_RESPONSE_LIST = "SELECT id_response FROM announce_announce_response WHERE id_announce = ?";
    private static final String SQL_QUERY_SELECT_ANNOUNCE_IMAGE_RESPONSE_LIST = SQL_QUERY_SELECT_ANNOUNCE_RESPONSE_LIST +
        " AND is_image = ?";
    private static final String SQL_QUERY_DELETE_ANNOUNCE_RESPONSE = "DELETE FROM announce_announce_response WHERE id_announce = ?";

    // Constants
    private static final String CONSTANT_COMA = ",";
    private static final String CONSTANT_CLOSE_PARENTHESIS = ")";
    private static final String CONSTANT_SPACE = " ";

    /**
     * Generates a new primary key
     * @param plugin The Plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
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
    public synchronized void insert( Announce announce, Plugin plugin )
    {
        announce.setId( newPrimaryKey( plugin ) );

        int nIndex = 1;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        /* Creation of Announce */
        daoUtil.setInt( nIndex++, announce.getId(  ) );
        daoUtil.setString( nIndex++, announce.getUserName(  ) );
        daoUtil.setString( nIndex++, announce.getUserLastName(  ) );
        daoUtil.setString( nIndex++, announce.getUserSecondName(  ) );
        daoUtil.setString( nIndex++, announce.getContactInformation(  ) );
        daoUtil.setInt( nIndex++, announce.getCategory(  ).getId(  ) );
        daoUtil.setString( nIndex++, announce.getTitle(  ) );
        daoUtil.setString( nIndex++, announce.getDescription(  ) );
        daoUtil.setString( nIndex++, announce.getPrice(  ) );
        daoUtil.setTimestamp( nIndex++, announce.getDateCreation(  ) );
        daoUtil.setTimestamp( nIndex++, announce.getDateModification(  ) );
        daoUtil.setBoolean( nIndex++, announce.getPublished(  ) );
        daoUtil.setString( nIndex++, announce.getTags(  ) );
        daoUtil.setBoolean( nIndex++, announce.getHasPictures(  ) );
        daoUtil.setLong( nIndex++, announce.getTimePublication(  ) );
        daoUtil.setInt( nIndex, announce.getHasNotify() );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Announce load( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery(  );

        Announce announce = null;

        if ( daoUtil.next(  ) )
        {
            announce = getAnnounceWithCategory( daoUtil );
        }

        daoUtil.free(  );

        return announce;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nAnnounceId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nAnnounceId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( Announce announce, Plugin plugin )
    {
        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setString( nIndex++, announce.getTitle(  ) );
        daoUtil.setString( nIndex++, announce.getDescription(  ) );
        daoUtil.setString( nIndex++, announce.getPrice(  ) );
        daoUtil.setString( nIndex++, announce.getContactInformation(  ) );
        daoUtil.setBoolean( nIndex++, announce.getPublished(  ) );
        daoUtil.setString( nIndex++, announce.getTags(  ) );
        daoUtil.setBoolean( nIndex++, announce.getHasPictures(  ) );
        daoUtil.setTimestamp( nIndex++, announce.getDateModification(  ) );
        daoUtil.setInt( nIndex++, announce.getHasNotify() );
        
        daoUtil.setInt( nIndex, announce.getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> selectAll( AnnounceSort announceSort, Plugin plugin )
    {
        List<Integer> announceList = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL + getOrderBy( announceSort ), plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            announceList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return announceList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> selectAllPublishedId( AnnounceSort announceSort, Plugin plugin )
    {
        List<Integer> listIdAnnounce = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID_PUBLISHED + getOrderBy( announceSort ), plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            listIdAnnounce.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return listIdAnnounce;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Announce> selectAllPublished( AnnounceSort announceSort, Plugin plugin )
    {
        List<Announce> announceList = new ArrayList<Announce>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_PUBLISHED + getOrderBy( announceSort ), plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            announceList.add( getAnnounceWithCategory( daoUtil ) );
        }

        daoUtil.free(  );

        return announceList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Announce> findByListId( List<Integer> listIdAnnounces, AnnounceSort announceSort, Plugin plugin )
    {
        List<Announce> announceList = new ArrayList<Announce>(  );

        if ( ( listIdAnnounces == null ) || ( listIdAnnounces.size(  ) == 0 ) )
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
        sbSql.append( getOrderBy( announceSort ) );

        DAOUtil daoUtil = new DAOUtil( sbSql.toString(  ), plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            announceList.add( getAnnounceWithCategory( daoUtil ) );
        }

        daoUtil.free(  );

        return announceList;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> selectAllPublishedForCategory( Category category, AnnounceSort announceSort, Plugin plugin )
    {
        List<Integer> announceList = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_PUBLISHED_FOR_CATEGORY + getOrderBy( announceSort ), plugin );
        daoUtil.setInt( 1, category.getId(  ) );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            announceList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return announceList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Announce> selectAllForUser( String strUsername, AnnounceSort announceSort, Plugin plugin )
    {
        List<Announce> announceList = new ArrayList<Announce>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ANNOUNCES_FOR_USER + getOrderBy( announceSort ), plugin );
        daoUtil.setString( 1, strUsername );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            announceList.add( getAnnounceWithCategory( daoUtil ) );
        }

        daoUtil.free(  );

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
        daoUtil.setBoolean( nParam++, announce.getPublished(  ) );
        daoUtil.setLong( nParam++, announce.getTimePublication(  ) );
        daoUtil.setInt( nParam, announce.getId(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setHasNotifed( Announce announce, Plugin plugin )
    {
        int nParam = 1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SET_HASNOTIFED, plugin );
        daoUtil.setInt( nParam++, announce.getHasNotify() );
        daoUtil.setInt( nParam, announce.getId(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSuspended( Announce announce, Plugin plugin )
    {
        int nParam = 1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SET_SUSPENDED, plugin );
        daoUtil.setBoolean( nParam++, announce.getSuspended(  ) );
        daoUtil.setLong( nParam++, announce.getTimePublication(  ) );
        daoUtil.setInt( nParam, announce.getId(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSuspendedByUser( Announce announce, Plugin plugin )
    {
        int nParam = 1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SET_SUSPENDED_BY_USER, plugin );
        daoUtil.setBoolean( nParam++, announce.getSuspendedByUser(  ) );
        daoUtil.setLong( nParam++, announce.getTimePublication(  ) );
        daoUtil.setInt( nParam, announce.getId(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> findIdAnnouncesByDateCreation( Timestamp timestamp, Plugin plugin )
    {
        List<Integer> announceIdList = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ID_BY_DATE_CREATION, plugin );
        daoUtil.setTimestamp( 1, timestamp );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            announceIdList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return announceIdList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> findIdAnnouncesByDatePublication( long lMinPublicationTime, Plugin plugin )
    {
        List<Integer> announceIdList = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ID_BY_TIME_PUBLICATION, plugin );
        daoUtil.setLong( 1, lMinPublicationTime );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            announceIdList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return announceIdList;
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
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> findListIdResponse( int nIdAnnounce, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ANNOUNCE_RESPONSE_LIST, plugin );
        daoUtil.setInt( 1, nIdAnnounce );
        daoUtil.executeQuery(  );

        List<Integer> listIdResponse = new ArrayList<Integer>(  );

        while ( daoUtil.next(  ) )
        {
            listIdResponse.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

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
        daoUtil.executeQuery(  );

        List<Integer> listIdResponse = new ArrayList<Integer>(  );

        while ( daoUtil.next(  ) )
        {
            listIdResponse.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

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
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Get an announce with its category. Only the id, the label and the price
     * boolean of the category is loaded.
     * @param daoUtil The daoUtil to load data from. Note that the cursor of the
     *            daoUtil will NOT be moved by this method, and that the call to
     *            the daoUtil.free( ) will NOT be performed.
     * @return The announce
     */
    private Announce getAnnounceWithCategory( DAOUtil daoUtil )
    {
        Announce announce = new Announce(  );
        Category category = new Category(  );

        int nIndex = 1;
        announce.setId( daoUtil.getInt( nIndex++ ) );
        announce.setTitle( daoUtil.getString( nIndex++ ) );
        announce.setDescription( daoUtil.getString( nIndex++ ) );
        announce.setPrice( daoUtil.getString( nIndex++ ) );
        announce.setDateCreation( daoUtil.getTimestamp( nIndex++ ) );
        announce.setDateModification( daoUtil.getTimestamp( nIndex++ ) );
        announce.setUserName( daoUtil.getString( nIndex++ ) );
        announce.setUserLastName( daoUtil.getString( nIndex++ ) );
        announce.setUserSecondName( daoUtil.getString( nIndex++ ) );
        announce.setContactInformation( daoUtil.getString( nIndex++ ) );
        announce.setPublished( daoUtil.getBoolean( nIndex++ ) );
        announce.setSuspended( daoUtil.getBoolean( nIndex++ ) );
        announce.setSuspendedByUser( daoUtil.getBoolean( nIndex++ ) );
        announce.setTags( daoUtil.getString( nIndex++ ) );
        announce.setHasPictures( daoUtil.getBoolean( nIndex++ ) );
        announce.setTimePublication( daoUtil.getLong( nIndex++ ) );
        announce.setHasNotify(daoUtil.getInt(nIndex++));

        category.setId( daoUtil.getInt( nIndex++ ) );
        category.setLabel( daoUtil.getString( nIndex++ ) );
        category.setDisplayPrice( daoUtil.getBoolean( nIndex++ ) );
        category.setIdSector(daoUtil.getInt(nIndex));
        

        announce.setCategory( category );

        return announce;
    }

    /**
     * Get the order by of a given announce sort
     * @param announceSort The announce sort
     * @return The order of the announce sort
     */
    private String getOrderBy( AnnounceSort announceSort )
    {
        return ORDER_BY + announceSort.getSortColumn(  ) + CONSTANT_SPACE +
        ( announceSort.getSortAsc(  ) ? ORDER_BY_ASCENDING : ORDER_BY_DESCENDING );
    }
}
