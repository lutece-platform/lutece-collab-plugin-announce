/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.announce.business.portlet;

import fr.paris.lutece.plugins.announce.service.AnnouncePlugin;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.util.sql.DAOUtil;


/**
 * this class provides Data Access methods for LastAnnouncePortlet objects
 */
public final class LastAnnouncesPortletDAO implements ILastAnnouncesPortletDAO
{
    private static final String SQL_QUERY_INSERT = " INSERT INTO announce_portlet_last_announces ( id_portlet, nb_announces ) VALUES (?,?) ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM announce_portlet_last_announces WHERE id_portlet = ? ";
    private static final String SQL_QUERY_UPDATE = " UPDATE announce_portlet_last_announces SET nb_announces = ? WHERE id_portlet = ? ";
    private static final String SQL_QUERY_SELECT = " SELECT nb_announces FROM announce_portlet_last_announces WHERE id_portlet = ? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert( Portlet portlet )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, AnnouncePlugin.getPlugin(  ) );
        LastAnnouncesPortlet lastAnnouncesPortlet = (LastAnnouncesPortlet) portlet;
        int nIndex = 1;
        daoUtil.setInt( nIndex++, lastAnnouncesPortlet.getId(  ) );
        daoUtil.setInt( nIndex, lastAnnouncesPortlet.getNbAnnouncesToDisplay(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nPortletId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, AnnouncePlugin.getPlugin(  ) );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( Portlet portlet )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, AnnouncePlugin.getPlugin(  ) );
        LastAnnouncesPortlet lastAnnouncesPortlet = (LastAnnouncesPortlet) portlet;
        int nIndex = 1;
        daoUtil.setInt( nIndex++, lastAnnouncesPortlet.getNbAnnouncesToDisplay(  ) );
        daoUtil.setInt( nIndex, lastAnnouncesPortlet.getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Portlet load( int nIdPortlet )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, AnnouncePlugin.getPlugin(  ) );
        daoUtil.setInt( 1, nIdPortlet );
        daoUtil.executeQuery(  );
          
        LastAnnouncesPortlet portlet = new LastAnnouncesPortlet(  );
        portlet.setId( nIdPortlet );

        if ( daoUtil.next(  ) )
        {
            portlet.setNbAnnouncesToDisplay( daoUtil.getInt( 1 ) );
        }
        daoUtil.free(  );
        return portlet;
    }
}
