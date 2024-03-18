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
package fr.paris.lutece.plugins.announce.service.announcesearch;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import fr.paris.lutece.plugins.announce.business.Announce;
import fr.paris.lutece.plugins.announce.business.AnnounceHome;
import fr.paris.lutece.plugins.announce.business.AnnounceSort;
import fr.paris.lutece.plugins.announce.business.IndexerAction;
import fr.paris.lutece.plugins.announce.service.AnnouncePlugin;
import fr.paris.lutece.plugins.announce.utils.AnnounceUtils;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.url.UrlItem;

/**
 * DefaultAnnounceIndexer
 */
public class DefaultAnnounceIndexer implements IAnnounceSearchIndexer
{
    private static final String PROPERTY_INDEXER_NAME = "announce.indexer.name";
    private static final String PARAMETER_ANNOUNCE_ID = "announce_id";
    private static final String ENABLE_VALUE_TRUE = "1";
    private static final String PROPERTY_INDEXER_DESCRIPTION = "announce.indexer.description";
    private static final String PROPERTY_INDEXER_VERSION = "announce.indexer.version";
    private static final String PROPERTY_INDEXER_ENABLE = "announce.indexer.enable";
    private static final String BLANK_SPACE = " ";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription( )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_DESCRIPTION );
    }

    /**
     * Index given list of record
     * 
     * @param indexWriter
     *            the indexWriter
     * @param listIdAnounce
     *            The list of id announce
     * @param plugin
     *            the plugin
     * @throws IOException
     *             If an IO Exception occurred
     */
    private void indexListAnnounce( IndexWriter indexWriter, List<Integer> listIdAnounce, Plugin plugin ) throws IOException
    {
        String strPortalUrl = AppPathService.getPortalUrl( );
        Iterator<Integer> it = listIdAnounce.iterator( );

        while ( it.hasNext( ) )
        {
            Integer nAnnounceId = it.next( );
            Announce announce = AnnounceHome.findByPrimaryKey( nAnnounceId );
            
            List<Response> listResponses = AnnounceHome.findListResponse( nAnnounceId, false );
            announce.setListResponse(listResponses);

            UrlItem urlAnnounce = new UrlItem( strPortalUrl );
            urlAnnounce.addParameter( XPageAppService.PARAM_XPAGE_APP, AppPropertiesService.getProperty( AnnounceUtils.PARAMETER_PAGE_ANNOUNCE ) ); // FIXME
            urlAnnounce.addParameter( PARAMETER_ANNOUNCE_ID, announce.getId( ) );

            indexWriter.addDocument( getDocument( announce, urlAnnounce.getUrl( ), plugin ) );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void processIndexing( IndexWriter indexWriter, boolean bCreate, StringBuffer sbLogs )
            throws IOException, InterruptedException, SiteMessageException
    {
        Plugin plugin = PluginService.getPlugin( AnnouncePlugin.PLUGIN_NAME );
        List<Integer> listIdAnnounce = new ArrayList<>( );

        if ( !bCreate )
        {
            // incremental indexing
            // delete all record which must be deleted
            for ( IndexerAction action : AnnounceSearchService.getInstance( ).getAllIndexerActionByTask( IndexerAction.TASK_DELETE, plugin ) )
            {
                sbLogAnnounce( sbLogs, action.getIdAnnounce( ), IndexerAction.TASK_DELETE );

                Term term = new Term( AnnounceSearchItem.FIELD_ID_ANNOUNCE, Integer.toString( action.getIdAnnounce( ) ) );
                Term [ ] terms = {
                        term
                };

                indexWriter.deleteDocuments( terms );
                AnnounceSearchService.getInstance( ).removeIndexerAction( action.getIdAction( ), plugin );
            }

            // Update all record which must be updated
            for ( IndexerAction action : AnnounceSearchService.getInstance( ).getAllIndexerActionByTask( IndexerAction.TASK_MODIFY, plugin ) )
            {
                sbLogAnnounce( sbLogs, action.getIdAnnounce( ), IndexerAction.TASK_MODIFY );

                Term term = new Term( AnnounceSearchItem.FIELD_ID_ANNOUNCE, Integer.toString( action.getIdAnnounce( ) ) );
                Term [ ] terms = {
                        term
                };

                indexWriter.deleteDocuments( terms );

                listIdAnnounce.add( action.getIdAnnounce( ) );

                AnnounceSearchService.getInstance( ).removeIndexerAction( action.getIdAction( ), plugin );
            }

            this.indexListAnnounce( indexWriter, listIdAnnounce, plugin );

            listIdAnnounce = new ArrayList<>( );

            // add all record which must be added
            for ( IndexerAction action : AnnounceSearchService.getInstance( ).getAllIndexerActionByTask( IndexerAction.TASK_CREATE, plugin ) )
            {
                sbLogAnnounce( sbLogs, action.getIdAnnounce( ), IndexerAction.TASK_CREATE );
                listIdAnnounce.add( action.getIdAnnounce( ) );

                AnnounceSearchService.getInstance( ).removeIndexerAction( action.getIdAction( ), plugin );
            }

            this.indexListAnnounce( indexWriter, listIdAnnounce, plugin );
        }
        else
        {
            for ( Announce announce : AnnounceHome.findAllPublished( AnnounceSort.DEFAULT_SORT ) )
            {
                if ( !announce.getSuspended( ) && !announce.getSuspendedByUser( ) )
                {
                    sbLogs.append( "Indexing Announce" );
                    sbLogs.append( "\r\n" );

                    sbLogAnnounce( sbLogs, announce.getId( ), IndexerAction.TASK_CREATE );

                    listIdAnnounce.add( announce.getId( ) );
                }
            }

            this.indexListAnnounce( indexWriter, listIdAnnounce, plugin );
        }

        indexWriter.commit( );
    }

    /**
     * Get the subject document
     * 
     * @param strDocument
     *            id of the subject to index
     * @return The list of lucene documents
     * @throws IOException
     *             If an IO Exception occurred
     */
    public static List<Document> getDocuments( String strDocument ) throws IOException
    {
        List<org.apache.lucene.document.Document> listDocs = new ArrayList<>( );
        String strPortalUrl = AppPathService.getPortalUrl( );
        Plugin plugin = PluginService.getPlugin( AnnouncePlugin.PLUGIN_NAME );

        for ( Announce announce : AnnounceHome.findAllPublished( AnnounceSort.DEFAULT_SORT ) )
        {
            if ( !announce.getSuspended( ) && !announce.getSuspendedByUser( ) )
            {
                UrlItem urlAnnounce = new UrlItem( strPortalUrl );
                urlAnnounce.addParameter( XPageAppService.PARAM_XPAGE_APP, AppPropertiesService.getProperty( AnnounceUtils.PARAMETER_PAGE_ANNOUNCE ) ); // FIXME
                urlAnnounce.addParameter( PARAMETER_ANNOUNCE_ID, announce.getId( ) );

                org.apache.lucene.document.Document docAnnounce = getDocument( announce, urlAnnounce.getUrl( ), plugin );
                listDocs.add( docAnnounce );
                if ( docAnnounce != null )
                {
                    IndexationService.write( docAnnounce );
                }
            }
        }

        return listDocs;
    }

    /**
     * Builds a document which will be used by Lucene during the indexing of the announces list
     * 
     * @param announce
     *            the announce
     * @param strUrl
     *            the url
     * @param plugin
     *            the plugin
     * @throws IOException
     *             If an IO Exception occurred
     * @return the document
     */
    public static org.apache.lucene.document.Document getDocument( Announce announce, String strUrl, Plugin plugin ) throws IOException
    {
        // make a new, empty document
        org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document( );
        doc.add( new Field( AnnounceSearchItem.FIELD_SECTOR_ID, String.valueOf( announce.getCategory( ).getIdSector( ) ), TextField.TYPE_STORED ) );

        doc.add( new Field( AnnounceSearchItem.FIELD_CATEGORY_ID, String.valueOf( announce.getCategory( ).getId( ) ), TextField.TYPE_STORED ) );
        doc.add( new Field( AnnounceSearchItem.FIELD_ID_ANNOUNCE, Integer.toString( announce.getId( ) ), TextField.TYPE_STORED ) );

        doc.add( new Field( AnnounceSearchItem.FIELD_TAGS, announce.getTags( ), TextField.TYPE_STORED ) );

        // Add the url as a field named "url". Use an UnIndexed field, so
        // that the url is just stored with the question/answer, but is not searchable.
        doc.add( new Field( SearchItem.FIELD_URL, strUrl, TextField.TYPE_STORED ) );

        // Add the uid as a field, so that index can be incrementally maintained.
        // This field is not stored with question/answer, it is indexed, but it is not
        // tokenized prior to indexing.
        String strIdAnnounce = String.valueOf( announce.getId( ) );
        doc.add( new Field( SearchItem.FIELD_UID, strIdAnnounce, TextField.TYPE_STORED ) );

        // Add the last modified date of the file a field named "modified".
        // Use a field that is indexed (i.e. searchable), but don't tokenize
        // the field into words.
        String strDate = DateTools.dateToString(
                ( announce.getTimePublication( ) > 0 ) ? new Timestamp( announce.getTimePublication( ) ) : announce.getDateCreation( ),
                DateTools.Resolution.DAY );
        doc.add( new Field( SearchItem.FIELD_DATE, strDate, TextField.TYPE_STORED ) );

        if ( announce.getPrice( ) != 0.0 )
        {
            double dPrice = announce.getPrice( );
            // Add the price of the announce
            doc.add( new Field( AnnounceSearchItem.FIELD_PRICE, AnnounceSearchService.formatPriceForIndexer( dPrice ), TextField.TYPE_STORED ) );
        }

        String strContentToIndex = getContentToIndex( announce );

        // NOUVEAU
        ContentHandler handler = new BodyContentHandler( );
        Metadata metadata = new Metadata( );

        try
        {
            new HtmlParser( ).parse( new ByteArrayInputStream( strContentToIndex.getBytes( ) ), handler, metadata, new ParseContext( ) );
        }
        catch( SAXException | TikaException e )
        {
            throw new AppException( "Error during announce parsing." );
        }

        String strContent = handler.toString( );

        // Add the tag-stripped contents as a Reader-valued Text field so it will
        // get tokenized and indexed.
        doc.add( new Field( SearchItem.FIELD_CONTENTS, strContent, TextField.TYPE_NOT_STORED ) );

        // Add the subject name as a separate Text field, so that it can be searched
        // separately.
        doc.add( new StoredField( SearchItem.FIELD_TITLE, announce.getTitle( ) ) );

        doc.add( new Field( SearchItem.FIELD_TYPE, AnnouncePlugin.PLUGIN_NAME, StringField.TYPE_STORED ) );

        // return the document
        return doc;
    }

    /**
     * Set the Content to index
     * 
     * @param announce
     *            The {@link Announce} to index
     * @param plugin
     *            The {@link Plugin}
     * @return The content to index
     */
    private static String getContentToIndex( Announce announce )
    {
        StringBuffer sbContentToIndex = new StringBuffer( );
        // Do not index question here
        sbContentToIndex.append( announce.getTitle( ) );
        sbContentToIndex.append( BLANK_SPACE );
        sbContentToIndex.append( announce.getDescription( ) );
        sbContentToIndex.append( BLANK_SPACE );
        sbContentToIndex.append( announce.getTags( ) );
        
        if ( !CollectionUtils.isEmpty(announce.getListResponse()) )
        {       
        	String strAttributs = announce.getListResponse().stream()
                .filter( response -> StringUtils.isNotBlank(response.getResponseValue()) )
                .map( response -> BLANK_SPACE + response.getResponseValue() )
                .collect( Collectors.joining() );
        	sbContentToIndex.append( strAttributs );
        }

        return sbContentToIndex.toString( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName( )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_NAME );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVersion( )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_VERSION );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnable( )
    {
        boolean bReturn = false;
        String strEnable = AppPropertiesService.getProperty( PROPERTY_INDEXER_ENABLE );

        if ( ( strEnable != null ) && ( strEnable.equalsIgnoreCase( Boolean.TRUE.toString( ) ) || strEnable.equals( ENABLE_VALUE_TRUE ) )
                && PluginService.isPluginEnable( AnnouncePlugin.PLUGIN_NAME ) )
        {
            bReturn = true;
        }

        return bReturn;
    }

    /**
     * Indexing action performed on the recording
     * 
     * @param sbLogs
     *            the buffer log
     * @param nIdAnnounce
     *            the id of the announce
     * @param nAction
     *            the indexer action key performed
     */
    private void sbLogAnnounce( StringBuffer sbLogs, int nIdAnnounce, int nAction )
    {
        sbLogs.append( "Indexing Announce:" );

        switch( nAction )
        {
            case IndexerAction.TASK_CREATE:
                sbLogs.append( "Insert " );

                break;

            case IndexerAction.TASK_MODIFY:
                sbLogs.append( "Modify " );

                break;

            case IndexerAction.TASK_DELETE:
                sbLogs.append( "Delete " );

                break;

            default:
                break;
        }

        if ( nIdAnnounce != AnnounceUtils.CONSTANT_ID_NULL )
        {
            sbLogs.append( "id_announce=" );
            sbLogs.append( nIdAnnounce );
        }

        sbLogs.append( "\r\n" );
    }
}
