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
package fr.paris.lutece.plugins.announce.service;

import fr.paris.lutece.plugins.announce.business.Announce;
import fr.paris.lutece.plugins.announce.business.AnnounceHome;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;

import java.util.Locale;

/**
 * Class AnnounceResourceIdService
 */
public class AnnounceResourceIdService extends ResourceIdService
{
    /* Permissions */

    /** Permission for publishing or unpublishing an announce */
    public static final String PERMISSION_PUBLISH = "PUBLISH";

    /** Permission for suspending an announce */
    public static final String PERMISSION_SUSPEND = "SUSPEND";

    /** Permission for deleting an announce */
    public static final String PERMISSION_DELETE = "DELETE";

    /** Permission for executing a workflow action on an announce */
    public static final String PERMISSION_EXECUTE_WORKFLOW_ACTION = "WORFKLOW_ACTION";

    /* Properties */
    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "announce.permission.resourceType.announce.label";
    private static final String PROPERTY_LABEL_PUBLISH = "announce.permission.resourceType.announce.publish";
    private static final String PROPERTY_LABEL_SUSPEND = "announce.permission.resourceType.announce.suspend";
    private static final String PROPERTY_LABEL_DELETE = "announce.permission.resourceType.announce.delete";
    private static final String PROPERTY_LABEL_WORKFLOW_ACTION = "announce.permission.resourceType.announce.workflowAction";

    /** Creates a new instance of SectorResourceIdService */
    public AnnounceResourceIdService( )
    {
        setPluginName( AnnouncePlugin.PLUGIN_NAME );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register( )
    {
        ResourceType rt = new ResourceType( );
        rt.setResourceIdServiceClass( AnnounceResourceIdService.class.getName( ) );
        rt.setPluginName( AnnouncePlugin.PLUGIN_NAME );
        rt.setResourceTypeKey( Announce.RESOURCE_TYPE );
        rt.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE );

        Permission p = new Permission( );
        p.setPermissionKey( PERMISSION_PUBLISH );
        p.setPermissionTitleKey( PROPERTY_LABEL_PUBLISH );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERMISSION_DELETE );
        p.setPermissionTitleKey( PROPERTY_LABEL_DELETE );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERMISSION_SUSPEND );
        p.setPermissionTitleKey( PROPERTY_LABEL_SUSPEND );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERMISSION_EXECUTE_WORKFLOW_ACTION );
        p.setPermissionTitleKey( PROPERTY_LABEL_WORKFLOW_ACTION );
        rt.registerPermission( p );

        ResourceTypeManager.registerResourceType( rt );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList getResourceIdList( Locale locale )
    {
        return new ReferenceList( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( String strId, Locale locale )
    {
        int nIdAnnounce = -1;

        try
        {
            nIdAnnounce = Integer.parseInt( strId );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        Announce announce = AnnounceHome.findByPrimaryKey( nIdAnnounce );

        return announce.getTitle( );
    }
}
