/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import fr.paris.lutece.plugins.announce.business.Announce;
import fr.paris.lutece.plugins.announce.business.IndexerAction;
import fr.paris.lutece.plugins.announce.business.IndexerActionFilter;
import fr.paris.lutece.plugins.announce.business.IndexerActionHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.search.SearchResult;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 * AnnounceSearchService
 */
public class AnnounceSearchService
{
    private static final String BEAN_SEARCH_ENGINE = "announce.announceSearchEngine";
    private static final String PATH_INDEX = "announce.internalIndexer.lucene.indexPath";
    private static final String PROPERTY_WRITER_MERGE_FACTOR = "announce.internalIndexer.lucene.writer.mergeFactor";
    private static final String PROPERTY_WRITER_MAX_FIELD_LENGTH = "announce.internalIndexer.lucene.writer.maxSectorLength";
    private static final String PROPERTY_ANALYSER_CLASS_NAME = "announce.internalIndexer.lucene.analyser.className";
    private static final String PROPERTY_MAX_SKIPPED_INDEXATION = "announce.indexer.maxSkipedIndexation";

    // Default values
    private static final int DEFAULT_WRITER_MERGE_FACTOR = 20;
    private static final int DEFAULT_WRITER_MAX_FIELD_LENGTH = 1000000;

    private static volatile String _strIndex;
    private static int _nWriterMergeFactor;
    private static int _nWriterMaxSectorLength;
    private static Analyzer _analyzer;
    private static IAnnounceSearchIndexer _indexer;

    // Constants corresponding to the variables defined in the lutece.properties file
    private static volatile AnnounceSearchService _singleton;

    private static int _nSkipedIndexations;

    /** Creates a new instance of DirectorySearchService */
    private AnnounceSearchService( )
    {
        // Read configuration properties
        String strIndex = getIndex( );

        if ( StringUtils.isEmpty( strIndex ) )
        {
            throw new AppException( "Lucene index path not found in announce.properties", null );
        }

        _nWriterMergeFactor = AppPropertiesService.getPropertyInt( PROPERTY_WRITER_MERGE_FACTOR,
                DEFAULT_WRITER_MERGE_FACTOR );
        _nWriterMaxSectorLength = AppPropertiesService.getPropertyInt( PROPERTY_WRITER_MAX_FIELD_LENGTH,
                DEFAULT_WRITER_MAX_FIELD_LENGTH );

        String strAnalyserClassName = AppPropertiesService.getProperty( PROPERTY_ANALYSER_CLASS_NAME );

        if ( ( strAnalyserClassName == null ) || ( strAnalyserClassName.equals( "" ) ) )
        {
            throw new AppException( "Analyser class name not found in announce.properties", null );
        }

        _indexer = (IAnnounceSearchIndexer) SpringContextService.getBean( "announce.announceIndexer" );

        try
        {
            _analyzer = (Analyzer) Class.forName( strAnalyserClassName ).newInstance( );
        }
        catch ( Exception e )
        {
            throw new AppException( "Failed to load Lucene Analyzer class", e );
        }
    }

    /**
     * Get the HelpdeskSearchService instance
     * 
     * @return The {@link AnnounceSearchService}
     */
    public static AnnounceSearchService getInstance( )
    {
        if ( _singleton == null )
        {
            _singleton = new AnnounceSearchService( );
        }

        return _singleton;
    }

    /**
     * Return search results
     * @param strKeywords keywords of query
     * @param nIdCategory id category
     * @param dateMin minimum date
     * @param dateMax maximum date
     * @param request The {@link HttpServletRequest}
     * @param plugin The plugin
     * @return Results as a collection of id of {@link Announce}
     */
    public List<Integer> getSearchResults( String strKeywords, int nIdCategory, Date dateMin, Date dateMax,
            HttpServletRequest request, Plugin plugin )
    {
        List<Integer> listAnnounces = new ArrayList<Integer>( );

        try
        {
            IAnnounceSearchEngine engine = (IAnnounceSearchEngine) SpringContextService.getBean( BEAN_SEARCH_ENGINE );
            List<SearchResult> listResults = engine.getSearchResults( strKeywords, nIdCategory, dateMin, dateMax,
                    request, plugin );

            for ( SearchResult searchResult : listResults )
            {
                if ( searchResult.getId( ) != null )
                {
                    listAnnounces.add( Integer.parseInt( searchResult.getId( ) ) );
                }
            }
        }
        catch ( Exception e )
        {
            AppLogService.error( e.getMessage( ), e );
            // If an error occurred clean result list
            listAnnounces = new ArrayList<Integer>( );
        }

        return listAnnounces;
    }

    /**
     * return searcher
     * @return searcher
     */
    public Searcher getSearcher( )
    {
        Directory dir = null;
        Searcher searcher = null;
        try
        {
            dir = NIOFSDirectory.open( new File( getIndex( ) ) );
            searcher = new IndexSearcher( dir, true );
        }
        catch ( IOException e )
        {
            AppLogService.error( e.getMessage( ), e );
            if ( dir != null )
            {
                try
                {
                    dir.close( );
                }
                catch ( IOException e1 )
                {
                    AppLogService.error( e1.getMessage( ), e );
                }
            }
        }

        return searcher;
    }

    /**
     * Process indexing
     * @param bCreate true for start full indexing
     *            false for begin incremental indexing
     * @return the log
     */
    public String processIndexing( boolean bCreate )
    {
        StringBuffer sbLogs = new StringBuffer( );
        IndexWriter writer = null;
        boolean bCreateIndex = bCreate;

        try
        {
            sbLogs.append( "\r\nIndexing all contents ...\r\n" );

            Directory dir = NIOFSDirectory.open( new File( getIndex( ) ) );

            if ( !IndexReader.indexExists( dir ) )
            { //init index
                bCreateIndex = true;
            }

            boolean bIsLocked = false;

            if ( IndexWriter.isLocked( dir ) )
            {
                _nSkipedIndexations++;

                if ( bCreate
                        || _nSkipedIndexations >= AppPropertiesService.getPropertyInt( PROPERTY_MAX_SKIPPED_INDEXATION,
                                10 ) )
                {
                    IndexWriter.unlock( dir );
                    bIsLocked = false;
                }
                else
                {
                    bIsLocked = true;
                    _nSkipedIndexations = 0;
                }
            }

            if ( !bIsLocked )
            {

                writer = new IndexWriter( dir, _analyzer, bCreateIndex, IndexWriter.MaxFieldLength.UNLIMITED );
                writer.setMergeFactor( _nWriterMergeFactor );
                writer.setMaxFieldLength( _nWriterMaxSectorLength );

                Date start = new Date( );

                sbLogs.append( "\r\n<strong>Indexer : " );
                sbLogs.append( _indexer.getName( ) );
                sbLogs.append( " - " );
                sbLogs.append( _indexer.getDescription( ) );
                sbLogs.append( "</strong>\r\n" );
                _indexer.processIndexing( writer, bCreateIndex, sbLogs );

                Date end = new Date( );

                sbLogs.append( "Duration of the treatment : " );
                sbLogs.append( end.getTime( ) - start.getTime( ) );
                sbLogs.append( " milliseconds\r\n" );

                writer.optimize( );
            }
        }
        catch ( Exception e )
        {
            sbLogs.append( " caught a " );
            sbLogs.append( e.getClass( ) );
            sbLogs.append( "\n with message: " );
            sbLogs.append( e.getMessage( ) );
            sbLogs.append( "\r\n" );
            AppLogService.error( "Indexing error : " + e.getMessage( ), e );
        }
        finally
        {
            try
            {
                if ( writer != null )
                {
                    writer.close( );
                }
            }
            catch ( IOException e )
            {
                AppLogService.error( e.getMessage( ), e );
            }
        }

        return sbLogs.toString( );
    }

    /**
     * Add Indexer Action to perform on a record
     * @param nIdAnnounce announce id
     * @param nIdTask the key of the action to do
     * @param plugin the plugin
     */
    public void addIndexerAction( int nIdAnnounce, int nIdTask, Plugin plugin )
    {
        IndexerAction indexerAction = new IndexerAction( );
        indexerAction.setIdAnnounce( nIdAnnounce );
        indexerAction.setIdTask( nIdTask );
        IndexerActionHome.create( indexerAction, plugin );
    }

    /**
     * Remove a Indexer Action
     * @param nIdAction the key of the action to remove
     * @param plugin the plugin
     */
    public void removeIndexerAction( int nIdAction, Plugin plugin )
    {
        IndexerActionHome.remove( nIdAction, plugin );
    }

    /**
     * return a list of IndexerAction by task key
     * @param nIdTask the task kety
     * @param plugin the plugin
     * @return a list of IndexerAction
     */
    public List<IndexerAction> getAllIndexerActionByTask( int nIdTask, Plugin plugin )
    {
        IndexerActionFilter filter = new IndexerActionFilter( );
        filter.setIdTask( nIdTask );

        return IndexerActionHome.getList( filter, plugin );
    }

    /**
     * Get the path to the index of the search service
     * @return The path to the index of the search service
     */
    private String getIndex( )
    {
        if ( _strIndex == null )
        {
            _strIndex = AppPathService.getPath( PATH_INDEX );
        }
        return _strIndex;
    }
}
