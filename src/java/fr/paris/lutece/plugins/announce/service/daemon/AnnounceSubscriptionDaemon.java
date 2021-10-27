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
package fr.paris.lutece.plugins.announce.service.daemon;

import fr.paris.lutece.plugins.announce.business.*;
import fr.paris.lutece.plugins.announce.service.AnnounceSubscriptionProvider;
import fr.paris.lutece.plugins.module.announce.subscribe.business.AnnounceSubscribtionDTO;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * Daemon to send notification to users when subscribed announces are created
 */
public class AnnounceSubscriptionDaemon extends Daemon
{
    private static final String MARK_ANNOUNCE = "announce";
    private static final String PROPERTY_SUBSCRIPTION_NOTIFICATION_SUBJECT = "announce.subscription.notificationSubject";
    private static final String PROPERTY_SUBSCRIPTION_NOTIFICATION_NUMBER = "announce.subscription.notificationNumber";
    private static final String TEMPLATE_EMAIL_ANNOUNCES = "skin/plugins/announce/email_notify_announces.html";

    public static final String MARK_PROD_URL = "prod_url";
    public static final String MARK_CATEGORY = "category";
    public static final String MARK_URL_SUBSCRIPTION = "url_subscription";
    public static final String MARK_QUERY_SPECIFIC_ANNOUNCE = "page=announce&action=view_announce&announce_id=";
    public static final String MARK_QUERY_URL_SUBSCRIPTION = "page=announce&amp;action=view_subscriptions";

    private static final String PROPERTY_BASE_URL = "lutece.base.url";
    private static final String BASE_URL = AppPropertiesService.getProperty( PROPERTY_BASE_URL ) + "/" + AppPathService.getPortalUrl( ) + "?";

    /**
     * {@inheritDoc}
     */
    @Override
    public void run( )
    {

        String numberOfAnnounceToNotify = AppPropertiesService.getProperty( PROPERTY_SUBSCRIPTION_NOTIFICATION_NUMBER );
        List<AnnounceNotify> listAnnounceToNotify = AnnounceNotifyHome.slecteAll( );
        if ( listAnnounceToNotify == null )
        {
            return;
        }

        for ( AnnounceNotify announceNotify : listAnnounceToNotify )
        {
            Announce announce = AnnounceHome.findByPrimaryKey( announceNotify.getIdAnnounce( ) );
            if ( announce == null )
            {
                continue;
            }
            processAnnounce( announce, numberOfAnnounceToNotify );
            AnnounceNotifyHome.delete( announceNotify.getId( ) );
        }
    }

    private void processAnnounce( Announce announce, String numberOfAnnounceToNotify )
    {
        List<AnnounceSubscribtionDTO> listSubscriptions = AnnounceSubscriptionProvider.getService( )
                .getSubscriptionsByAnnounce( Integer.toString( announce.getCategory( ).getId( ) ), announce.getUserName( ), numberOfAnnounceToNotify );
        listSubscriptions = ofNullable( listSubscriptions ).map(
                lst -> lst.stream( ).filter( e -> e.getEmailSubscribes( ) != null && !e.getEmailSubscribes( ).isEmpty( ) ).collect( Collectors.toList( ) ) )
                .orElse( null );

        if ( listSubscriptions != null )
        {
            String strSubject = AppPropertiesService.getProperty( PROPERTY_SUBSCRIPTION_NOTIFICATION_SUBJECT );
            String strSenderName = MailService.getNoReplyEmail( );
            String strSenderEmail = strSenderName;
            StringBuilder emailSubscribes = new StringBuilder( "" );

            int countElst = 1;
            int sizeLstSubNotify = listSubscriptions.size( );
            for ( AnnounceSubscribtionDTO subscription : listSubscriptions )
            {
                if ( subscription != null )
                {
                    emailSubscribes.append( subscription.getEmailSubscribes( ) );
                }
                if ( sizeLstSubNotify > countElst )
                {
                    countElst++;
                    emailSubscribes.append( ";" );
                }
            }
            notifyUser( announce, strSubject, strSenderName, strSenderEmail, emailSubscribes.toString( ) );
        }
    }

    /**
     * Notify a user that announces ha has subscribed to have been published
     * 
     * @param strUserName
     *            The name of the user to notify
     * @param announcesToNotify
     *            published announce
     * @param strSubject
     *            The subject of the email to send
     * @param strSenderName
     *            The name of the sender of the email
     * @param strSenderEmail
     *            The email address of the sender of the email
     */

    private void notifyUser( Announce announcesToNotify, String strSubject, String strSenderName, String strSenderEmail, String strUserEmail )
    {

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_ANNOUNCE, announcesToNotify );

        model.put( MARK_PROD_URL, BASE_URL + MARK_QUERY_SPECIFIC_ANNOUNCE );
        model.put( MARK_CATEGORY, CategoryHome.findByPrimaryKey( announcesToNotify.getCategory( ).getId( ) ) );
        model.put( MARK_URL_SUBSCRIPTION, BASE_URL + MARK_QUERY_URL_SUBSCRIPTION );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_EMAIL_ANNOUNCES, Locale.getDefault( ), model );

        if ( !strSenderEmail.equals( " " ) )
        {
            MailService.sendMailHtml( (String) null, (String) null, strUserEmail, strSenderName, strSenderEmail, strSubject, template.getHtml( ) );
        }

    }
}
