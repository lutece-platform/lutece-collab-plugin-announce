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
package fr.paris.lutece.plugins.announce.service.daemon;

import fr.paris.lutece.plugins.announce.business.Announce;
import fr.paris.lutece.plugins.announce.business.AnnounceHome;
import fr.paris.lutece.plugins.announce.business.AnnounceSort;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * Daemon to remove expired announces
 */
public class AnnounceExpirationDaemon extends Daemon
{
    private static final String PROPERTY_NB_DAYS_BEFORE_ANNOUNCES_REMOVAL = "announce.nbDaysBeforeAnnouncesRemoval";
    private static final int DEFAULT_NB_DAYS_BEFORE_ANNOUNCES_REMOVAL = 90;


    /**
     * {@inheritDoc}
     */
    @Override
    public void run(  )
    {
        Calendar calendar = new GregorianCalendar(  );
        Calendar calendarNotification = new GregorianCalendar(  );
        int nNbDaysBeforeAnnouncesRemoval = AppPropertiesService.getPropertyInt( PROPERTY_NB_DAYS_BEFORE_ANNOUNCES_REMOVAL,
                DEFAULT_NB_DAYS_BEFORE_ANNOUNCES_REMOVAL );
        int nNbDaysBeforeAnnouncesNotify = nNbDaysBeforeAnnouncesRemoval + 7;
        String email, 
        strSenderName = "strSenderName", 
        strSenderEmail = MailService.getNoReplyEmail(  ), 
        strSubject = "Suppression d'annouce", 
        strMessage = "Bonjour, "
        		+ "\nL'annouce : ";
        
        calendar.add( Calendar.DATE, -1 * nNbDaysBeforeAnnouncesRemoval );
        calendarNotification.add( Calendar.DATE, -1 * nNbDaysBeforeAnnouncesNotify );

        Timestamp timestamp = new Timestamp( calendar.getTimeInMillis(  ) );
        Timestamp timestampNotify = new Timestamp( calendarNotification.getTimeInMillis(  ) );
        
        List<Integer> listIdNotifiesAnnounces = AnnounceHome.findIdAnnouncesByDateCreation( timestampNotify );
        for(Integer idNotifieAnn : listIdNotifiesAnnounces){
        	Announce ann = AnnounceHome.findByPrimaryKey(idNotifieAnn.intValue());
        	int hasN = ann.getHasNotify();
        	if ( hasN == 0){
        		email = ann.getContactInformation();
        		String message = strMessage + ann.getTitle() + " sera supprim\u00E9e dans une semaine. "
                		+ "Veuillez la modifier si vous  voulez la maintenir \n";
            	MailService.sendMailHtml( email, strSenderName , strSenderEmail,
                        strSubject, message );
            	ann.setHasNotify(1);
            	AnnounceHome.setHasNotifed(ann);
        	}
        	
        }
        
        List<Integer> listIdExpiredAnnounces = AnnounceHome.findIdAnnouncesByDateCreation( timestamp );

        for ( Integer nIdExpiredAnnounce : listIdExpiredAnnounces )
        {
            AnnounceHome.remove( nIdExpiredAnnounce );
        }

        setLastRunLogs( listIdNotifiesAnnounces.size() + " notified and " + listIdExpiredAnnounces.size(  ) + " expired announces have been removed" );
    }
}
