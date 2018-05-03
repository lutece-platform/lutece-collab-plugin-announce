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
package fr.paris.lutece.plugins.announce.service;

import fr.paris.lutece.plugins.announce.business.Category;
import fr.paris.lutece.plugins.announce.business.CategoryHome;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;

import java.util.Locale;


/**
 *
 * class CategoryResourceIdService
 *
 */
public class CategoryResourceIdService extends ResourceIdService
{
    /* Permissions */

    /** Permission for creating a category */
    public static final String PERMISSION_CREATE = "CREATE";

    /** Permission for deleting a category */
    public static final String PERMISSION_DELETE = "DELETE";

    /** Permission for modifying a category */
    public static final String PERMISSION_MODIFY = "MODIFY";
    public static final String PERMISSION_COPY = "COPY";

    /* Properties */
    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "announce.permission.resourceType.category.label";
    private static final String PROPERTY_LABEL_CREATE = "announce.permission.resourceType.category.create";
    private static final String PROPERTY_LABEL_MODIFY = "announce.permission.resourceType.category.modify";
    private static final String PROPERTY_LABEL_COPY = "announce.permission.resourceType.category.copy";
    private static final String PROPERTY_LABEL_DELETE = "announce.permission.resourceType.category.delete";

    /** Creates a new instance of CategoryResourceIdService */
    public CategoryResourceIdService(  )
    {
        setPluginName( AnnouncePlugin.PLUGIN_NAME );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(  )
    {
        ResourceType rt = new ResourceType(  );
        rt.setResourceIdServiceClass( CategoryResourceIdService.class.getName(  ) );
        rt.setPluginName( AnnouncePlugin.PLUGIN_NAME );
        rt.setResourceTypeKey( Category.RESOURCE_TYPE );
        rt.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE );

        Permission p = new Permission(  );
        p.setPermissionKey( PERMISSION_CREATE );
        p.setPermissionTitleKey( PROPERTY_LABEL_CREATE );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_MODIFY );
        p.setPermissionTitleKey( PROPERTY_LABEL_MODIFY );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_DELETE );
        p.setPermissionTitleKey( PROPERTY_LABEL_DELETE );
        rt.registerPermission( p );
        
        p = new Permission(  );
        p.setPermissionKey( PERMISSION_COPY );
        p.setPermissionTitleKey( PROPERTY_LABEL_COPY );
        rt.registerPermission( p );

        ResourceTypeManager.registerResourceType( rt );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList getResourceIdList( Locale locale )
    {
        return CategoryHome.findCategoriesReferenceList(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( String strId, Locale locale )
    {
        int nIdCategory = -1;

        try
        {
            nIdCategory = Integer.parseInt( strId );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        Category category = CategoryHome.findByPrimaryKey( nIdCategory );

        return category.getLabel(  );
    }
}
