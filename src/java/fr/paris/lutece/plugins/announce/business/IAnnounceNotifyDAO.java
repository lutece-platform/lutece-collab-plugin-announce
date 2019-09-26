package fr.paris.lutece.plugins.announce.business;

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.List;

public interface IAnnounceNotifyDAO
{

    void insert( AnnounceNotify announce, Plugin plugin );

    void delete( int nIdAnnounce, Plugin plugin );

    List<AnnounceNotify> load( Plugin plugin );
}
