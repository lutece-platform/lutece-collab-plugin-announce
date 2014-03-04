/*
 * AnnounceUtils.java
 *
 * Created on 23 octobre 2009, 11:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.announce.utils;

import fr.paris.lutece.plugins.announce.service.AnnouncePlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Utility class for announce plugin
 */
public final class AnnounceUtils
{
    //Constants
    public static final String CONSTANT_WHERE = " WHERE ";
    public static final String CONSTANT_AND = " AND ";
    public static final int CONSTANT_ID_NULL = -1;

    //properties
    public static final String MESSAGE_MANDATORY_QUESTION = "announce.mandatory_message";

    //parameters
    /**
     * Name of the XPage
     */
    public static final String PARAMETER_PAGE_ANNOUNCE = "announce";

    /**
     * Private default constructor
     */
    private AnnounceUtils(  )
    {
        // Do nothing
    }

    /**
     * Builds a query with filters placed in parameters
     * @param strSelect the select of the query
     * @param listStrFilter the list of filter to add in the query
     * @param strOrder the order by of the query
     * @return a query
     */
    public static String buildRequetteWithFilter( String strSelect, List<String> listStrFilter, String strOrder )
    {
        StringBuffer strBuffer = new StringBuffer(  );
        strBuffer.append( strSelect );

        int nCount = 0;

        for ( String strFilter : listStrFilter )
        {
            if ( ++nCount == 1 )
            {
                strBuffer.append( CONSTANT_WHERE );
            }

            strBuffer.append( strFilter );

            if ( nCount != listStrFilter.size(  ) )
            {
                strBuffer.append( CONSTANT_AND );
            }
        }

        if ( strOrder != null )
        {
            strBuffer.append( strOrder );
        }

        return strBuffer.toString(  );
    }

    /**
     * write the http header in the response
     *
     * @param request
     *            the httpServletRequest
     * @param response
     *            the http response
     * @param strFileName
     *            the name of the file who must insert in the response
     *
     */
    public static void addHeaderResponse( HttpServletRequest request, HttpServletResponse response, String strFileName )
    {
        response.setHeader( "Content-Disposition", "attachment ;filename=\"" + strFileName + "\"" );
        response.setHeader( "Pragma", "public" );
        response.setHeader( "Expires", "0" );
        response.setHeader( "Cache-Control", "must-revalidate,post-check=0,pre-check=0" );
    }

    /**
     * Gets the plugin
     *
     * @return the plugin
     */
    public static Plugin getPlugin(  )
    {
        return PluginService.getPlugin( AnnouncePlugin.PLUGIN_NAME );
    }
}
