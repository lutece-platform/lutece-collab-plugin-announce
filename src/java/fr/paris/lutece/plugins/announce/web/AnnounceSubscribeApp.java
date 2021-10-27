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
package fr.paris.lutece.plugins.announce.web;

import fr.paris.lutece.plugins.announce.business.AnnounceSearchFilter;
import fr.paris.lutece.plugins.announce.business.AnnounceSearchFilterHome;
import fr.paris.lutece.plugins.announce.service.AnnounceSubscriptionProvider;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Announce subscribe Application
 */
@Controller( xpageName = "announce-subscribe", pageTitleI18nKey = "module.announce.subscribe.announceSubscribeApp.defaultTitle", pagePathI18nKey = "module.announce.subscribe.announceSubscribeApp.defaultPath" )
public class AnnounceSubscribeApp extends MVCApplication
{
    private static final long serialVersionUID = -3612994086181738393L;
    private static final String ACTION_DO_CREATE_SUBSCRIPTION_FILTER = "doCreateSubscriptionFilter";
    private static final String ACTION_DO_CREATE_USER_SUBSCRIPTION = "doCreateUserSubscription";
    private static final String ACTION_DO_CREATE_CATEGORY_SUBSCRIPTION = "doCreateCategorySubscription";
    private static final String PARAMETER_USER_NAME = "username";
    private static final String PARAMETER_ID_CATEGORY = "id_category";
    private static final String PARAMETER_REFERER = "referer";
    private static final String MESSAGE_CATEGORY_ALREADY_SUBSCRIBED = "announce.error.subscribe.category_already_subscribed";
    private static final String MESSAGE_USER_ALREADY_SUBSCRIBED = "announce.error.subscribe.user_already_subscribed";

    /**
     * Do create a subscription filter and redirect the user to the search page
     * 
     * @param request
     *            The request
     * @return The XPage
     * @throws UserNotSignedException
     *             If the user has not signed in
     */
    @Action( ACTION_DO_CREATE_SUBSCRIPTION_FILTER )
    public XPage doCreateSubscriptionFilter( HttpServletRequest request ) throws UserNotSignedException
    {
        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

        if ( user == null )
        {
            throw new UserNotSignedException( );
        }

        AnnounceSearchFilter filter = AnnounceApp.getAnnounceFilterFromRequest( request );
        AnnounceSearchFilterHome.create( filter );

        AnnounceSubscriptionProvider.getService( ).createSubscriptionToFilter( user, filter.getIdFilter( ) );

        return redirect( request, AnnounceApp.getUrlSearchAnnounce( request ) );
    }

    /**
     * Do subscribe to a user
     * 
     * @param request
     *            The request
     * @return The XPage
     * @throws UserNotSignedException
     *             If the user has not signed in
     * @throws SiteMessageException
     *             If a site message needs to be displayed
     */
    @Action( ACTION_DO_CREATE_USER_SUBSCRIPTION )
    public XPage doSubscribeToUser( HttpServletRequest request ) throws UserNotSignedException, SiteMessageException
    {
        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

        if ( user == null )
        {
            throw new UserNotSignedException( );
        }

        String strUserName = request.getParameter( PARAMETER_USER_NAME );

        if ( AnnounceSubscriptionProvider.getService( ).hasSubscribedToUser( user, strUserName ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_USER_ALREADY_SUBSCRIBED, SiteMessage.TYPE_STOP );
        }

        AnnounceSubscriptionProvider.getService( ).createSubscriptionToUser( user, strUserName );

        String strReferer = request.getHeader( PARAMETER_REFERER );

        if ( StringUtils.isNotEmpty( strReferer ) )
        {
            return redirect( request, strReferer );
        }

        return redirect( request, AnnounceApp.getUrlSearchAnnounce( request ) );
    }

    /**
     * Do subscribe to a category
     * 
     * @param request
     *            The request
     * @return The XPage
     * @throws UserNotSignedException
     *             If the user has not signed in
     * @throws SiteMessageException
     *             If a site message needs to be displayed
     */
    @Action( ACTION_DO_CREATE_CATEGORY_SUBSCRIPTION )
    public XPage doSubscribeToCategory( HttpServletRequest request ) throws UserNotSignedException, SiteMessageException
    {
        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

        if ( user == null )
        {
            throw new UserNotSignedException( );
        }

        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );

        if ( StringUtils.isNotEmpty( strIdCategory ) && StringUtils.isNumeric( strIdCategory ) )
        {
            int nIdCategory = Integer.parseInt( strIdCategory );

            if ( AnnounceSubscriptionProvider.getService( ).hasSubscribedToCategory( user, nIdCategory ) )
            {
                SiteMessageService.setMessage( request, MESSAGE_CATEGORY_ALREADY_SUBSCRIBED, SiteMessage.TYPE_STOP );
            }

            AnnounceSubscriptionProvider.getService( ).createSubscriptionToCategory( user, nIdCategory );
        }

        String strReferer = request.getHeader( PARAMETER_REFERER );

        if ( StringUtils.isNotEmpty( strReferer ) )
        {
            return redirect( request, strReferer );
        }

        return redirect( request, AnnounceApp.getUrlSearchAnnounce( request ) );
    }
}
