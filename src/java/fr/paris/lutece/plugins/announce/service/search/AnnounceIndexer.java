/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
package fr.paris.lutece.plugins.announce.service.search;

import fr.paris.lutece.plugins.announce.service.AnnouncePlugin;
import fr.paris.lutece.plugins.announce.service.announcesearch.AnnounceSearchService;
import fr.paris.lutece.plugins.announce.service.announcesearch.DefaultAnnounceIndexer;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.search.SearchIndexer;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.lucene.document.Document;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 * AnnounceIndexer
 */
public class AnnounceIndexer implements SearchIndexer
{
    private static final String INDEXER_NAME = "announce.indexer.name";
    private static final String INDEXER_DESCRIPTION = "announce.indexer.description";
    private static final String INDEXER_VERSION = "announce.indexer.version";
    private static final String PROPERTY_INDEXER_ENABLE = "announce.indexer.enable";
    private static final String JSP_SEARCH_ANNOUNCE = "jsp/site/Portal.jsp?page=announce";

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Document> getDocuments( String strDocument ) throws IOException, InterruptedException, SiteMessageException
    {
        return DefaultAnnounceIndexer.getDocuments( strDocument );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void indexDocuments( ) throws IOException, InterruptedException, SiteMessageException
    {
        AnnounceSearchService.getInstance( ).processIndexing( true );
        DefaultAnnounceIndexer.getDocuments( "" );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName( )
    {
        return AppPropertiesService.getProperty( INDEXER_NAME );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVersion( )
    {
        return AppPropertiesService.getProperty( INDEXER_VERSION );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription( )
    {
        return AppPropertiesService.getProperty( INDEXER_DESCRIPTION );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnable( )
    {
        String strEnable = AppPropertiesService.getProperty( PROPERTY_INDEXER_ENABLE, "true" );

        return ( strEnable.equalsIgnoreCase( "true" ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getListType( )
    {
        List<String> listType = new ArrayList<String>( );
        listType.add( AnnouncePlugin.PLUGIN_NAME );

        return listType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSpecificSearchAppUrl( )
    {
        return JSP_SEARCH_ANNOUNCE;
    }
}
