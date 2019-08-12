package fr.paris.lutece.plugins.announce.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;

public class AnnounceNotifyDAO implements IAnnounceNotifyDAO {

    private static final String SQL_QUERY_SELECT_ALL = "SELECT id, id_announce FROM announce_notify";
    private static final String SQL_QUERY_INSERT = "INSERT INTO announce_notify ( id, id_announce) VALUES (?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM announce_notify WHERE id = ? ";

    private static final String SQL_QUERY_NEW_PK = "SELECT max( id ) FROM announce_notify";

    @Override
    public void insert(AnnounceNotify announce, Plugin plugin) {

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        daoUtil.setInt( 1, newPrimaryKey( plugin ));
        daoUtil.setInt( 2, announce.getIdAnnounce( ));

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    @Override
    public void delete( int nIdAnnounce, Plugin plugin) {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdAnnounce );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    @Override
    public List<AnnounceNotify> load(Plugin plugin) {

        List<AnnounceNotify> announceList = new ArrayList<AnnounceNotify>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL,  plugin );
        daoUtil.executeQuery( );

        AnnounceNotify announceNotify;
        while ( daoUtil.next( ) )
        {
            announceNotify = new AnnounceNotify();
            announceNotify.setId(daoUtil.getInt(1));
            announceNotify.setIdAnnounce(daoUtil.getInt(2));
            announceList.add( announceNotify );
        }

        daoUtil.free( );

        return announceList;
    }

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
}
