/*
 * Copyright (c) 2002-2026, City of Paris
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import fr.paris.lutece.plugins.announce.business.Announce;
import fr.paris.lutece.plugins.announce.business.AnnounceNotify;
import fr.paris.lutece.plugins.announce.business.AnnounceNotifyHome;
import fr.paris.lutece.portal.business.mailinglist.Recipient;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * Service handling announce notifications: admin moderation emails and subscription notification queuing
 */
public class AnnounceNotificationService
{
    public static final String BEAN_NAME = "announce.announceNotificationService";

    private static final String KEY_WEBMASTER_EMAIL = "portal.site.site_property.email";
    private static final String PROPERTY_SENDER_EMAIL = "announce.mail.senderEmail";
    private static final String KEY_SITE_NAME = "portal.site.site_property.name";
    private static final String PROPERTY_SENDER_NAME = "announce.mail.senderName";
    private static final String PROPERTY_ANNOUNCE_NOTIFY_SUBJECT = "announce.notification.subject";
    private static final String PROPERTY_PROD_URL = "lutece.prod.url";

    private static final String TEMPLATE_ANNOUNCE_NOTIFY_MESSAGE = "skin/plugins/announce/announce_notify_message.html";

    private static final String MARK_PROD_URL = "prod_url";
    private static final String MARK_ANNOUNCE = "announce";
    private static final String MARK_LIST_FIELDS = "list_sectors";
    private static final String MARK_LOCALE = "locale";

    /**
     * Send an admin moderation notification email for an announce
     *
     * @param announce
     *            The announce
     * @param locale
     *            The locale for i18n
     */
    public void sendModerationNotification( Announce announce, Locale locale )
    {
        int nIdMailingList = announce.getCategory( ).getIdMailingList( );

        if ( nIdMailingList <= 0 )
        {
            AppLogService.info( "sendModerationNotification: no mailing list configured for category '{}' (id={}), admin notification skipped for announce {}",
                    announce.getCategory( ).getLabel( ), announce.getCategory( ).getId( ), announce.getId( ) );
            return;
        }

        Collection<Recipient> listRecipients = AdminMailingListService.getRecipients( nIdMailingList );

        if ( listRecipients.isEmpty( ) )
        {
            AppLogService.info( "sendModerationNotification: mailing list {} has no recipients, admin notification skipped for announce {}", nIdMailingList,
                    announce.getId( ) );
            return;
        }

        String strSenderEmail = DatastoreService.getDataValue( KEY_WEBMASTER_EMAIL, AppPropertiesService.getProperty( PROPERTY_SENDER_EMAIL ) );
        String strSenderName = DatastoreService.getDataValue( KEY_SITE_NAME, AppPropertiesService.getProperty( PROPERTY_SENDER_NAME ) );
        String strSubject = I18nService.getLocalizedString( PROPERTY_ANNOUNCE_NOTIFY_SUBJECT, locale );

        strSubject += ( " " + announce.getCategory( ).getLabel( ) );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_PROD_URL, AppPropertiesService.getProperty( PROPERTY_PROD_URL ) );
        model.put( MARK_ANNOUNCE, announce );
        model.put( MARK_LIST_FIELDS, AnnounceService.getSectorList( ) );
        model.put( MARK_LOCALE, locale );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_ANNOUNCE_NOTIFY_MESSAGE, locale, model );
        String strBody = template.getHtml( );

        for ( Recipient recipient : listRecipients )
        {
            MailService.sendMailHtml( recipient.getEmail( ), strSenderName, strSenderEmail, strSubject, strBody );
        }
    }

    /**
     * Queue a subscription notification for an announce (to be processed by the notification daemon)
     *
     * @param announce
     *            The announce
     */
    public void queueSubscriptionNotification( Announce announce )
    {
        AnnounceNotify announceNotify = new AnnounceNotify( );
        announceNotify.setIdAnnounce( announce.getId( ) );
        AnnounceNotifyHome.create( announceNotify );
    }
}
