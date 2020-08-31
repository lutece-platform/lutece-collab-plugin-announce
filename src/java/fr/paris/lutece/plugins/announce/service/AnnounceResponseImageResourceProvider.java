/*
 * Copyright (c) 2002-2020, City of Paris
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

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.announce.business.Announce;
import fr.paris.lutece.plugins.announce.business.AnnounceHome;
import fr.paris.lutece.plugins.announce.web.AnnounceJspBean;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.image.ImageResourceProvider;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.util.file.FileUtil;
import fr.paris.lutece.util.url.UrlItem;

/**
 * Resource provider for Announce images
 */
public class AnnounceResponseImageResourceProvider implements ImageResourceProvider
{

    private static final String RESOURCE_TYPE = "announce_img";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceTypeId( )
    {
        return RESOURCE_TYPE;
    }

    /**
     * Returns whether this image is authorized for this request
     *
     * @param nAnnounceId
     *            The id of the announce
     * @param request
     *            The request
     * @return true if this image is authorized for this request
     */
    private boolean isImageAuthorized( int nAnnounceId, HttpServletRequest request )
    {

        Announce announce = AnnounceHome.findByPrimaryKey( nAnnounceId );

        boolean bAllowAccess = false;
        boolean bUserIsAuthor = false;

        LuteceUser user = null;

        if ( SecurityService.isAuthenticationEnable( ) )
        {
            user = SecurityService.getInstance( ).getRegisteredUser( request );
        }

        if ( ( ( user != null ) && user.getName( ).equals( announce.getUserName( ) ) ) )
        {
            bUserIsAuthor = true;
        }

        if ( ( announce.getPublished( ) && !announce.getSuspended( ) && !announce.getSuspendedByUser( ) ) || bUserIsAuthor )
        {
            bAllowAccess = true;
        }

        // Is the announce visible in the front office ?
        if ( bAllowAccess )
        {
            return true;
        }

        // Is the announce visible in the back office ?
        AdminUser adminUser = AdminUserService.getAdminUser( request );
        if ( adminUser != null )
        {
            return adminUser.checkRight( AnnounceJspBean.RIGHT_MANAGE_ANNOUNCE );
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImageResource getImageResource( int nIdResource )
    {
        Integer nIdAnnounce = AnnounceHome.findIdByImageResponse( nIdResource );
        if ( nIdAnnounce != null )
        {
            // When using an older core version (before 5.1.5), the local variables will not
            // have been set by the image servlet. So we can get null or a request from another thread.
            // We could try to detect this by checking request.getServletPath( ) (or maybe other things?)
            // but it would break if we decide to expose this provider through another entrypoint.
            // Also, on tomcat (tested 8.5.5), it seems like the request object is reused just like
            // the thread, so that even if the local variables were set in another request,
            // the object we get here is the correct one (with the corect LuteceUser or AdminUser etc).
            // Also, Portal.jsp, the main entry point of the webapp, does clean up the local variables.
            // Note that the other request could even have run code from another webapp (not even a lutece webapp)
            // Also, we could log a warning here when request is null, but then it would prevent from using
            // this function from code not associated with a request. So no warnings.
            HttpServletRequest request = LocalVariables.getRequest( );

            if ( request == null || isImageAuthorized( nIdAnnounce, request ) )
            {
                Response response = ResponseHome.findByPrimaryKey( nIdResource );

                if ( response.getFile( ) != null )
                {
                    File file = FileHome.findByPrimaryKey( response.getFile( ).getIdFile( ) );

                    if ( ( file.getPhysicalFile( ) != null ) && FileUtil.hasImageExtension( file.getTitle( ) ) )
                    {
                        PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( file.getPhysicalFile( ).getIdPhysicalFile( ) );
                        ImageResource image = new ImageResource( );
                        image.setImage( physicalFile.getValue( ) );
                        image.setMimeType( file.getMimeType( ) );

                        return image;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Get the URL to download an image response
     *
     * @param nIdResponse
     *            The id of the response
     * @return The URl to download the image
     */
    public static String getUrlDownloadImageResponse( int nIdResponse )
    {
        UrlItem urlItem = new UrlItem( "image" );
        urlItem.addParameter( "resource_type", RESOURCE_TYPE );
        urlItem.addParameter( "id", nIdResponse );

        return urlItem.getUrl( );
    }
}
