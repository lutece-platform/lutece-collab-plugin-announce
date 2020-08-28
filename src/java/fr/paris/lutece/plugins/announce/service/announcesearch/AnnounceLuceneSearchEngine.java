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
package fr.paris.lutece.plugins.announce.service.announcesearch;

import fr.paris.lutece.plugins.announce.business.Announce;
import fr.paris.lutece.plugins.announce.business.AnnounceHome;
import fr.paris.lutece.plugins.announce.business.AnnounceSearchFilter;
import fr.paris.lutece.plugins.announce.business.AnnounceSort;
import fr.paris.lutece.plugins.announce.service.AnnouncePlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.search.SearchResult;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

//import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.surround.parser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

//import org.apache.lucene.search.Searcher;//
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * LuceneSearchEngine
 */
public class AnnounceLuceneSearchEngine implements IAnnounceSearchEngine
{
    private static final int NO_CATEGORY = 0;
    private static final int NO_SECTOR = 0;
    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat( "yyyyMMdd", Locale.US );

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSearchResults( AnnounceSearchFilter filter, Plugin plugin, List<SearchResult> listSearchResult, int nPage, int nItemsPerPage )
    {
        ArrayList<SearchItem> listResults = new ArrayList<SearchItem>( );
        IndexSearcher searcher;

        // Searcher searcher = null;
        Date dateMinToSearch;
        Date dateMaxToSearch;
        int nNbResults = 0;

        try
        {
            searcher = AnnounceSearchService.getInstance( ).getSearcher( );

            Collection<String> queries = new ArrayList<String>( );
            Collection<String> sectors = new ArrayList<String>( );
            Collection<BooleanClause.Occur> flags = new ArrayList<BooleanClause.Occur>( );

            // Category id
            if ( filter.getIdCategory( ) != NO_CATEGORY )
            {
                Query queryCategoryId = new TermQuery( new Term( AnnounceSearchItem.FIELD_CATEGORY_ID, String.valueOf( filter.getIdCategory( ) ) ) );
                queries.add( queryCategoryId.toString( ) );
                sectors.add( AnnounceSearchItem.FIELD_CATEGORY_ID );
                flags.add( BooleanClause.Occur.MUST );
            }

            // Category id
            if ( filter.getIdSector( ) != NO_SECTOR )
            {
                Query querySectorId = new TermQuery( new Term( AnnounceSearchItem.FIELD_SECTOR_ID, String.valueOf( filter.getIdSector( ) ) ) );
                queries.add( querySectorId.toString( ) );
                sectors.add( AnnounceSearchItem.FIELD_SECTOR_ID );
                flags.add( BooleanClause.Occur.MUST );
            }

            // Type (=announce)
            PhraseQuery.Builder queryTypeBuilder = new PhraseQuery.Builder( );
            // add character "e" to TYPE because field is not analyzed when added to lucene document, but it's analyzed then in MultiFieldQueryParser.parse
            // method
            queryTypeBuilder.add( new Term( AnnounceSearchItem.FIELD_TYPE, AnnouncePlugin.PLUGIN_NAME + "e" ) );
            queries.add( queryTypeBuilder.build( ).toString( ) );
            sectors.add( AnnounceSearchItem.FIELD_TYPE );
            flags.add( BooleanClause.Occur.MUST );

            // Keywords in title or description
            if ( StringUtils.isNotBlank( filter.getKeywords( ) ) )
            {
                PhraseQuery.Builder queryContentBuilder = new PhraseQuery.Builder( );
                queryContentBuilder.add( new Term( AnnounceSearchItem.FIELD_CONTENTS, filter.getKeywords( ) ) );
                queries.add( queryContentBuilder.build( ).toString( ) );
                sectors.add( AnnounceSearchItem.FIELD_CONTENTS );
                flags.add( BooleanClause.Occur.MUST );
            }

            // contains range date
            if ( ( filter.getDateMin( ) != null ) || ( filter.getDateMax( ) != null ) )
            {
                if ( filter.getDateMin( ) == null )
                {
                    dateMinToSearch = new Date( 0L );
                }
                else
                {
                    dateMinToSearch = filter.getDateMin( );
                }

                if ( filter.getDateMax( ) == null )
                {
                    dateMaxToSearch = new Date( );
                }
                else
                {
                    dateMaxToSearch = filter.getDateMax( );
                }

                // String stringDateMin = DateUtil.
                String strLowerTerm = DAY_FORMAT.format( dateMinToSearch );
                String strUpperTerm = DAY_FORMAT.format( dateMaxToSearch );
                BytesRef bRLowerTerm = new BytesRef( strLowerTerm );
                BytesRef bRUpperTerm = new BytesRef( strUpperTerm );
                Query queryRangeDate = new TermRangeQuery( AnnounceSearchItem.FIELD_DATE, bRLowerTerm, bRUpperTerm, true, true );
                queries.add( queryRangeDate.toString( ) );
                sectors.add( AnnounceSearchItem.FIELD_DATE );
                flags.add( BooleanClause.Occur.MUST );
            }

            // contains range price
            if ( ( filter.getPriceMin( ) > 0 ) || ( filter.getPriceMax( ) > 0 ) )
            {
                int nPriceMin = ( filter.getPriceMin( ) > 0 ) ? filter.getPriceMin( ) : 0;
                int nPriceMax = ( filter.getPriceMax( ) > 0 ) ? filter.getPriceMax( ) : Integer.MAX_VALUE;
                Query queryRangePrice = new TermRangeQuery( AnnounceSearchItem.FIELD_PRICE,
                        new BytesRef( AnnounceSearchService.formatPriceForIndexer( nPriceMin ) ),
                        new BytesRef( AnnounceSearchService.formatPriceForIndexer( nPriceMax ) ), true, true );
                queries.add( queryRangePrice.toString( ) );
                sectors.add( AnnounceSearchItem.FIELD_PRICE );
                flags.add( BooleanClause.Occur.MUST );
            }

            Query queryMulti = MultiFieldQueryParser.parse( queries.toArray( new String [ queries.size( )] ), sectors.toArray( new String [ sectors.size( )] ),
                    flags.toArray( new BooleanClause.Occur [ flags.size( )] ), AnnounceSearchService.getInstance( ).getAnalyzer( ) );

            TopDocs topDocs = searcher.search( queryMulti, 1000000 );
            ScoreDoc [ ] hits = topDocs.scoreDocs;
            nNbResults = hits.length;

            // We only get the documents of the current page
            int nFrom = ( nPage - 1 ) * nItemsPerPage;

            if ( nFrom < 0 )
            {
                nFrom = 0;
            }

            int nTo = ( nPage * nItemsPerPage );

            if ( ( nTo == 0 ) || ( nTo > nNbResults ) )
            {
                nTo = nNbResults;
            }

            for ( int i = nFrom; i < nTo; i++ )
            {
                int docId = hits [i].doc;
                Document document = searcher.doc( docId );
                SearchItem si = new SearchItem( document );
                listResults.add( si );
            }
        }
        catch( Exception e )
        {
            AppLogService.error( e.getMessage( ), e );
        }

        /*
         * finally { if ( searcher != null ) { try { searcher.close( ); } catch ( IOException e ) { AppLogService.error( e.getMessage( ), e ); } } }
         */
        convertList( listResults, listSearchResult );

        return nNbResults;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSearchResultsBis( AnnounceSearchFilter filter, Plugin plugin, List<Announce> listAnnouncesResult, int nPage, int nItemsPerPage,
            AnnounceSort anSort )
    {
        ArrayList<SearchItem> listResults = new ArrayList<SearchItem>( );
        List<Integer> listIdAnnounces = new ArrayList<Integer>( );
        IndexSearcher searcher;

        // Searcher searcher = null;
        Date dateMinToSearch;
        Date dateMaxToSearch;
        int nNbResults = 0;
        try
        {
            searcher = AnnounceSearchService.getInstance( ).getSearcher( );

            Collection<String> queries = new ArrayList<String>( );
            Collection<String> sectors = new ArrayList<String>( );
            Collection<BooleanClause.Occur> flags = new ArrayList<BooleanClause.Occur>( );

            // Category id
            if ( filter.getIdCategory( ) != NO_CATEGORY )
            {
                Query queryCategoryId = new TermQuery( new Term( AnnounceSearchItem.FIELD_CATEGORY_ID, String.valueOf( filter.getIdCategory( ) ) ) );
                queries.add( queryCategoryId.toString( ) );
                sectors.add( AnnounceSearchItem.FIELD_CATEGORY_ID );
                flags.add( BooleanClause.Occur.MUST );
            }

            // Category id
            if ( filter.getIdSector( ) != NO_SECTOR )
            {
                Query querySectorId = new TermQuery( new Term( AnnounceSearchItem.FIELD_SECTOR_ID, String.valueOf( filter.getIdSector( ) ) ) );
                queries.add( querySectorId.toString( ) );
                sectors.add( AnnounceSearchItem.FIELD_SECTOR_ID );
                flags.add( BooleanClause.Occur.MUST );
            }

            // Type (=announce)
            PhraseQuery.Builder queryTypeBuilder = new PhraseQuery.Builder( );
            // add character "e" to TYPE because field is not analyzed when added to lucene document, but it's analyzed then in MultiFieldQueryParser.parse
            // method
            queryTypeBuilder.add( new Term( AnnounceSearchItem.FIELD_TYPE, AnnouncePlugin.PLUGIN_NAME + "e" ) );
            queries.add( queryTypeBuilder.build( ).toString( ) );
            sectors.add( AnnounceSearchItem.FIELD_TYPE );
            flags.add( BooleanClause.Occur.MUST );

            // Keywords in title or description
            if ( StringUtils.isNotBlank( filter.getKeywords( ) ) )
            {
                PhraseQuery.Builder queryContentBuilder = new PhraseQuery.Builder( );
                queryContentBuilder.add( new Term( AnnounceSearchItem.FIELD_CONTENTS, filter.getKeywords( ) ) );
                queries.add( queryContentBuilder.build( ).toString( ) );
                sectors.add( AnnounceSearchItem.FIELD_CONTENTS );
                flags.add( BooleanClause.Occur.MUST );
            }

            // contains range date
            if ( ( filter.getDateMin( ) != null ) || ( filter.getDateMax( ) != null ) )
            {
                if ( filter.getDateMin( ) == null )
                {
                    dateMinToSearch = new Date( 0L );
                }
                else
                {
                    dateMinToSearch = filter.getDateMin( );
                }

                if ( filter.getDateMax( ) == null )
                {
                    dateMaxToSearch = new Date( );
                }
                else
                {
                    dateMaxToSearch = filter.getDateMax( );
                }

                // String stringDateMin = DateUtil.
                String strLowerTerm = DAY_FORMAT.format( dateMinToSearch );
                String strUpperTerm = DAY_FORMAT.format( dateMaxToSearch );
                BytesRef bRLowerTerm = new BytesRef( strLowerTerm );
                BytesRef bRUpperTerm = new BytesRef( strUpperTerm );
                Query queryRangeDate = new TermRangeQuery( AnnounceSearchItem.FIELD_DATE, bRLowerTerm, bRUpperTerm, true, true );
                queries.add( queryRangeDate.toString( ) );
                sectors.add( AnnounceSearchItem.FIELD_DATE );
                flags.add( BooleanClause.Occur.MUST );
            }

            // contains range price
            if ( ( filter.getPriceMin( ) > 0 ) || ( filter.getPriceMax( ) > 0 ) )
            {
                int nPriceMin = ( filter.getPriceMin( ) > 0 ) ? filter.getPriceMin( ) : 0;
                int nPriceMax = ( filter.getPriceMax( ) > 0 ) ? filter.getPriceMax( ) : Integer.MAX_VALUE;
                Query queryRangePrice = new TermRangeQuery( AnnounceSearchItem.FIELD_PRICE,
                        new BytesRef( AnnounceSearchService.formatPriceForIndexer( nPriceMin ) ),
                        new BytesRef( AnnounceSearchService.formatPriceForIndexer( nPriceMax ) ), true, true );
                queries.add( queryRangePrice.toString( ) );
                sectors.add( AnnounceSearchItem.FIELD_PRICE );
                flags.add( BooleanClause.Occur.MUST );
            }

            Query queryMulti = MultiFieldQueryParser.parse( queries.toArray( new String [ queries.size( )] ), sectors.toArray( new String [ sectors.size( )] ),
                    flags.toArray( new BooleanClause.Occur [ flags.size( )] ), AnnounceSearchService.getInstance( ).getAnalyzer( ) );

            TopDocs topDocs = searcher.search( queryMulti, 1000000 );
            ScoreDoc [ ] hits = topDocs.scoreDocs;
            nNbResults = hits.length;

            // -------------------------------------------------
            for ( int i = 0; i < nNbResults; i++ )
            {
                int docId = hits [i].doc;
                Document document = searcher.doc( docId );
                SearchItem si = new SearchItem( document );
                listResults.add( si );
            }
            for ( SearchItem searchResult : listResults )
            {
                if ( searchResult.getId( ) != null )
                {
                    listIdAnnounces.add( Integer.parseInt( searchResult.getId( ) ) );
                }
            }

            List<Announce> listAnnounces = AnnounceHome.findByListId( listIdAnnounces, anSort );

            // -------------------------------------------------

            // We only get the documents of the current page
            int nFrom = ( nPage - 1 ) * nItemsPerPage;

            if ( nFrom < 0 )
            {
                nFrom = 0;
            }

            int nTo = ( nPage * nItemsPerPage );

            if ( ( nTo == 0 ) || ( nTo > nNbResults ) )
            {
                nTo = nNbResults;
            }

            for ( int i = nFrom; i < nTo; i++ )
            {
                listAnnouncesResult.add( listAnnounces.get( i ) );
            }
        }
        catch( Exception e )
        {
            AppLogService.error( e.getMessage( ), e );
        }

        /*
         * finally { if ( searcher != null ) { try { searcher.close( ); } catch ( IOException e ) { AppLogService.error( e.getMessage( ), e ); } } }
         */

        return nNbResults;
    }

    /**
     * Convert the SearchItem list on SearchResult list
     * 
     * @param listSource
     *            The source list
     * @param listSearchResult
     *            The result list
     */
    private void convertList( List<SearchItem> listSource, List<SearchResult> listSearchResult )
    {
        for ( SearchItem item : listSource )
        {
            SearchResult result = new SearchResult( );
            result.setId( item.getId( ) );

            try
            {
                result.setDate( DateTools.stringToDate( item.getDate( ) ) );
            }
            catch( ParseException e )
            {
                AppLogService.error( "Bad Date Format for indexed item \"" + item.getTitle( ) + "\" : " + e.getMessage( ) );
            }

            result.setUrl( item.getUrl( ) );
            result.setTitle( item.getTitle( ) );
            result.setSummary( item.getSummary( ) );
            result.setType( item.getType( ) );
            listSearchResult.add( result );
        }
    }
}
