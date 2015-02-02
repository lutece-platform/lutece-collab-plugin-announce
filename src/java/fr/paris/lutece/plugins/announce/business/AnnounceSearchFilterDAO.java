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

import java.sql.Date;

import java.util.ArrayList;
import java.util.List;


/**
 * DAO for announce search filters
 */
public class AnnounceSearchFilterDAO implements IAnnounceSearchFilterDAO
{
    private static final String SQL_QUERY_NEW_PRIMARY_KEY = " SELECT max(id_filter) FROM announce_search_filters ";
    private static final String SQL_QUERY_SELECT = " SELECT id_filter, id_category, keywords, date_min, date_max, price_min, price_max FROM announce_search_filters ";
    private static final String SQL_QUERY_SELECT_LIST_ID = SQL_QUERY_SELECT + " WHERE id_filter IN ( ";
    private static final String SQL_QUERY_SELECT_BY_PRIMARY_KEY = SQL_QUERY_SELECT + " WHERE id_filter = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO announce_search_filters ( id_filter, id_category, keywords, date_min, date_max, price_min, price_max ) VALUES (?,?,?,?,?,?,?) ";
    private static final String SQL_QUERY_UPDATE = " UPDATE announce_search_filters SET id_category = ?, keywords = ?, date_min = ?, date_max = ?, price_min = ?, price_max = ? WHERE id_filter = ? ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM announce_search_filters WHERE id_filter = ? ";
    private static final String SQL_QUERY_DELETE_BY_ID_CATEGORY = " DELETE FROM announce_search_filters WHERE id_category = ? ";
    private static final String CONSTANT_COMA = ",";
    private static final String CONSTANT_CLOSE_PARENTHESIS = ")";

    /**
     * Get a new primary key
     * @param plugin The plugin
     * @return the new value of the primary key
     */
    private int getNewPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PRIMARY_KEY, plugin );
        daoUtil.executeQuery(  );

        int nRes = 1;

        if ( daoUtil.next(  ) )
        {
            nRes = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free(  );

        return nRes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnnounceSearchFilter findByPrimaryKey( int nIdFilter, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nIdFilter );
        daoUtil.executeQuery(  );

        AnnounceSearchFilter filter = null;

        if ( daoUtil.next(  ) )
        {
            filter = getFilterFromDAO( daoUtil );
        }

        daoUtil.free(  );

        return filter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void create( AnnounceSearchFilter filter, Plugin plugin )
    {
        filter.setIdFilter( getNewPrimaryKey( plugin ) );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        int nIndex = 1;
        daoUtil.setInt( nIndex++, filter.getIdFilter(  ) );
        daoUtil.setInt( nIndex++, filter.getIdCategory(  ) );
        daoUtil.setString( nIndex++, filter.getKeywords(  ) );
        daoUtil.setDate( nIndex++,
            ( filter.getDateMin(  ) == null ) ? null : new Date( filter.getDateMin(  ).getTime(  ) ) );
        daoUtil.setDate( nIndex++,
            ( filter.getDateMax(  ) == null ) ? null : new Date( filter.getDateMax(  ).getTime(  ) ) );
        daoUtil.setInt( nIndex++, filter.getPriceMin(  ) );
        daoUtil.setInt( nIndex, filter.getPriceMax(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update( AnnounceSearchFilter filter, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;
        daoUtil.setInt( nIndex++, filter.getIdCategory(  ) );
        daoUtil.setString( nIndex++, filter.getKeywords(  ) );
        daoUtil.setDate( nIndex++, new Date( filter.getDateMin(  ).getTime(  ) ) );
        daoUtil.setDate( nIndex++, new Date( filter.getDateMax(  ).getTime(  ) ) );
        daoUtil.setInt( nIndex++, filter.getPriceMin(  ) );
        daoUtil.setInt( nIndex++, filter.getPriceMax(  ) );
        daoUtil.setInt( nIndex, filter.getIdFilter(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdFilter, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdFilter );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByIdCategory( int nIdCategory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID_CATEGORY, plugin );
        daoUtil.setInt( 1, nIdCategory );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AnnounceSearchFilter> findAll( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.executeQuery(  );

        List<AnnounceSearchFilter> listFilters = new ArrayList<AnnounceSearchFilter>(  );

        while ( daoUtil.next(  ) )
        {
            listFilters.add( getFilterFromDAO( daoUtil ) );
        }

        daoUtil.free(  );

        return listFilters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AnnounceSearchFilter> findByListId( List<Integer> listIdFilters, Plugin plugin )
    {
        if ( ( listIdFilters == null ) || ( listIdFilters.size(  ) == 0 ) )
        {
            return null;
        }

        StringBuilder sbSql = new StringBuilder( SQL_QUERY_SELECT_LIST_ID );
        boolean bHasContent = false;

        for ( int nIdFilters : listIdFilters )
        {
            if ( bHasContent )
            {
                sbSql.append( CONSTANT_COMA );
            }

            sbSql.append( nIdFilters );
        }

        sbSql.append( CONSTANT_CLOSE_PARENTHESIS );

        DAOUtil daoUtil = new DAOUtil( sbSql.toString(  ), plugin );
        daoUtil.executeQuery(  );

        List<AnnounceSearchFilter> listFilters = new ArrayList<AnnounceSearchFilter>(  );

        while ( daoUtil.next(  ) )
        {
            listFilters.add( getFilterFromDAO( daoUtil ) );
        }

        daoUtil.free(  );

        return listFilters;
    }

    /**
     * Get a filter from a DAOUtil
     * @param daoUtil The daoUtil to read data from. Note that the daoUtil will
     *            NOT be freed by this method, and that the call to the
     *            daoUtil.next( ) must have been done before calling this
     *            method.
     * @return The filter
     */
    private AnnounceSearchFilter getFilterFromDAO( DAOUtil daoUtil )
    {
        int nIndex = 1;
        AnnounceSearchFilter filter = new AnnounceSearchFilter(  );
        filter.setIdFilter( daoUtil.getInt( nIndex++ ) );
        filter.setIdCategory( daoUtil.getInt( nIndex++ ) );
        filter.setKeywords( daoUtil.getString( nIndex++ ) );
        filter.setDateMin( daoUtil.getDate( nIndex++ ) );
        filter.setDateMax( daoUtil.getDate( nIndex++ ) );
        filter.setPriceMin( daoUtil.getInt( nIndex++ ) );
        filter.setPriceMax( daoUtil.getInt( nIndex ) );

        return filter;
    }
}
