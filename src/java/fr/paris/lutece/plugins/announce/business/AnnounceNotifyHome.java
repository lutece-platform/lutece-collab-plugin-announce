package fr.paris.lutece.plugins.announce.business;

import fr.paris.lutece.plugins.announce.service.AnnouncePlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;

public final class AnnounceNotifyHome {
    // Static variable pointed at the DAO instance
    private static IAnnounceNotifyDAO _dao = SpringContextService.getBean( "announce.announceNotifyDAO" );
    private static Plugin _plugin = PluginService.getPlugin( AnnouncePlugin.PLUGIN_NAME );

    private AnnounceNotifyHome ()
    {
    }

    public static void create( AnnounceNotify announce )
    {
        _dao.insert( announce, _plugin );
    }

    public static void delete (int nIdAnnounceNotify){
        _dao.delete(nIdAnnounceNotify, _plugin);
    }

    public static List<AnnounceNotify> slecteAll() {
       return _dao.load(_plugin);
    }
}
