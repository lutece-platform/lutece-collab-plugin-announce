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
package fr.paris.lutece.plugins.announce.service.daemon;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.announce.business.Announce;
import fr.paris.lutece.plugins.announce.business.AnnounceHome;
import fr.paris.lutece.plugins.announce.service.AnnounceLifecycleService;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * Daemon to notify users of upcoming expiration and remove expired announces
 */
public class AnnounceExpirationDaemon extends Daemon
{
    private static final String PROPERTY_NB_DAYS_BEFORE_ANNOUNCES_REMOVAL = "announce.nbDaysBeforeAnnouncesRemoval";
    private static final String KEY_WEBMASTER_EMAIL = "portal.site.site_property.email";
    private static final String PROPERTY_SENDER_EMAIL = "announce.mail.senderEmail";
    private static final String KEY_SITE_NAME = "portal.site.site_property.name";
    private static final String PROPERTY_SENDER_NAME = "announce.mail.senderName";
    private static final String PROPERTY_PROD_URL = "lutece.prod.url";
    private static final String PROPERTY_EXPIRATION_MAIL_SUBJECT = "announce.daemon.expiration.mail.subject";
    private static final String PROPERTY_MAX_ANNOUNCES_PER_EMAIL = "announce.daemon.expiration.maxAnnouncesPerEmail";
    private static final int DEFAULT_NB_DAYS_BEFORE_ANNOUNCES_REMOVAL = 90;
    private static final int DEFAULT_MAX_ANNOUNCES_PER_EMAIL = 10;
    private static final Pattern PATTERN_EMAIL = Pattern.compile( "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$" );

    private static final String TEMPLATE_EXPIRATION_MESSAGE = "skin/plugins/announce/announce_expiration_message.html";
    private static final String MARK_ANNOUNCES = "announces";
    private static final String MARK_PROD_URL = "prod_url";

    private AnnounceLifecycleService _announceLifecycleService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void run( )
    {
        if ( _announceLifecycleService == null )
        {
            _announceLifecycleService = SpringContextService.getBean( AnnounceLifecycleService.BEAN_NAME );
        }

        Calendar calendar = new GregorianCalendar( );
        Calendar calendarNotification = new GregorianCalendar( );
        int nNbDaysBeforeAnnouncesRemoval = AppPropertiesService.getPropertyInt( PROPERTY_NB_DAYS_BEFORE_ANNOUNCES_REMOVAL,
                DEFAULT_NB_DAYS_BEFORE_ANNOUNCES_REMOVAL );
        int nNbDaysBeforeAnnouncesNotify = nNbDaysBeforeAnnouncesRemoval - 7;

        calendar.add( Calendar.DATE, -1 * nNbDaysBeforeAnnouncesRemoval );
        calendarNotification.add( Calendar.DATE, -1 * nNbDaysBeforeAnnouncesNotify );

        Timestamp timestamp = new Timestamp( calendar.getTimeInMillis( ) );
        Timestamp timestampNotify = new Timestamp( calendarNotification.getTimeInMillis( ) );

        int nNotified = sendExpirationNotifications( timestampNotify );

        List<Integer> listIdExpiredAnnounces = AnnounceHome.findIdAnnouncesByLastActivity( timestamp );

        for ( Integer nIdExpiredAnnounce : listIdExpiredAnnounces )
        {
            _announceLifecycleService.remove( nIdExpiredAnnounce );
        }

        setLastRunLogs( nNotified + " notified and " + listIdExpiredAnnounces.size( ) + " expired announces have been removed" );
    }

    /**
     * Send expiration notification emails, grouped by contact email
     *
     * @param timestampNotify
     *            The threshold timestamp for notification
     * @return The number of announces notified
     */
    private int sendExpirationNotifications( Timestamp timestampNotify )
    {
        List<Integer> listIdNotifyAnnounces = AnnounceHome.findIdAnnouncesByLastActivity( timestampNotify );

        // Group announces to notify by contact email
        Map<String, List<Announce>> mapAnnouncesByEmail = new HashMap<>( );

        for ( Integer nIdAnnounce : listIdNotifyAnnounces )
        {
            Announce announce = AnnounceHome.findByPrimaryKey( nIdAnnounce );

            if ( announce == null )
            {
                AppLogService.info( "AnnounceExpirationDaemon: announce {} no longer exists, skipping", nIdAnnounce );
                continue;
            }

            if ( announce.getHasNotify( ) == 0 )
            {
                String strEmail = announce.getContactInformation( );

                if ( StringUtils.isBlank( strEmail ) || !PATTERN_EMAIL.matcher( strEmail ).matches( ) )
                {
                    AppLogService.info( "AnnounceExpirationDaemon: invalid contact '{}' for announce {}, skipping notification",
                            strEmail, nIdAnnounce );
                    announce.setHasNotify( 1 );
                    _announceLifecycleService.setHasNotified( announce );
                    continue;
                }

                mapAnnouncesByEmail.computeIfAbsent( strEmail, k -> new ArrayList<>( ) ).add( announce );
            }
        }

        // Send emails per user, batched by max announces per email
        String strSenderName = DatastoreService.getDataValue( KEY_SITE_NAME, AppPropertiesService.getProperty( PROPERTY_SENDER_NAME ) );
        String strSenderEmail = DatastoreService.getDataValue( KEY_WEBMASTER_EMAIL, AppPropertiesService.getProperty( PROPERTY_SENDER_EMAIL ) );
        String strSubject = I18nService.getLocalizedString( PROPERTY_EXPIRATION_MAIL_SUBJECT, I18nService.getDefaultLocale( ) );
        String strProdUrl = AppPropertiesService.getProperty( PROPERTY_PROD_URL );
        int nMaxPerEmail = AppPropertiesService.getPropertyInt( PROPERTY_MAX_ANNOUNCES_PER_EMAIL, DEFAULT_MAX_ANNOUNCES_PER_EMAIL );

        int nNotified = 0;

        for ( Map.Entry<String, List<Announce>> entry : mapAnnouncesByEmail.entrySet( ) )
        {
            String strRecipientEmail = entry.getKey( );
            List<Announce> listAnnounces = entry.getValue( );

            // Split into batches
            for ( int i = 0; i < listAnnounces.size( ); i += nMaxPerEmail )
            {
                List<Announce> batch = listAnnounces.subList( i, Math.min( i + nMaxPerEmail, listAnnounces.size( ) ) );

                Map<String, Object> model = new HashMap<>( );
                model.put( MARK_ANNOUNCES, batch );
                model.put( MARK_PROD_URL, strProdUrl );

                HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_EXPIRATION_MESSAGE, I18nService.getDefaultLocale( ), model );

                MailService.sendMailHtml( strRecipientEmail, strSenderName, strSenderEmail, strSubject, template.getHtml( ) );
            }

            // Mark all announces as notified
            for ( Announce announce : listAnnounces )
            {
                announce.setHasNotify( 1 );
                _announceLifecycleService.setHasNotified( announce );
                nNotified++;
            }
        }

        return nNotified;
    }
}
