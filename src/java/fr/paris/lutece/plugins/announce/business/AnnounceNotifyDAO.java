/*
 * Copyright (c) 2002-2021, City of Paris
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

import java.util.ArrayList;
import java.util.List;

public class AnnounceNotifyDAO implements IAnnounceNotifyDAO
{

    private static final String SQL_QUERY_SELECT_ALL = "SELECT id, id_announce FROM announce_notify";
    private static final String SQL_QUERY_INSERT = "INSERT INTO announce_notify ( id, id_announce) VALUES (?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM announce_notify WHERE id = ? ";

    private static final String SQL_QUERY_NEW_PK = "SELECT max( id ) FROM announce_notify";

    @Override
    public void insert( AnnounceNotify announce, Plugin plugin )
    {

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            daoUtil.setInt( 1, newPrimaryKey( plugin ) );
            daoUtil.setInt( 2, announce.getIdAnnounce( ) );

            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void delete( int nIdAnnounce, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdAnnounce );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public List<AnnounceNotify> load( Plugin plugin )
    {

        List<AnnounceNotify> announceList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL, plugin ) )
        {
            daoUtil.executeQuery( );

            AnnounceNotify announceNotify;
            while ( daoUtil.next( ) )
            {
                announceNotify = new AnnounceNotify( );
                announceNotify.setId( daoUtil.getInt( 1 ) );
                announceNotify.setIdAnnounce( daoUtil.getInt( 2 ) );
                announceList.add( announceNotify );
            }

        }

        return announceList;
    }

    public int newPrimaryKey( Plugin plugin )
    {
        int nKey = 1;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin ) )
        {
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                nKey = daoUtil.getInt( 1 ) + 1;
            }
        }
        return nKey;
    }
}
