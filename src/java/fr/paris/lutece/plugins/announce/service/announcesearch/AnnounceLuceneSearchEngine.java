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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.analysis.Analyzer;

import fr.paris.lutece.plugins.announce.business.Announce;
import fr.paris.lutece.plugins.announce.business.AnnounceHome;
import fr.paris.lutece.plugins.announce.business.AnnounceSearchFilter;
import fr.paris.lutece.plugins.announce.business.AnnounceSort;
import fr.paris.lutece.plugins.announce.service.AnnouncePlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.search.SearchResult;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
/**
 * LuceneSearchEngine
 */
public class AnnounceLuceneSearchEngine implements IAnnounceSearchEngine
{
    private static final int NO_CATEGORY = 0;
    private static final int NO_SECTOR = 0;
    private final SimpleDateFormat _dayFormat = new SimpleDateFormat( "yyyyMMdd", Locale.US );
    private static final String PROPERTY_LUCENE_MIN_SCORE = "announce.lucene.minScore";
    /**
     * {@inheritDoc}
     */
    @Override
    public int getSearchResults( AnnounceSearchFilter filter, Plugin plugin, List<SearchResult> listSearchResult, int nPage, int nItemsPerPage )
    {
        ArrayList<SearchItem> listResults = new ArrayList<>( );
        IndexSearcher searcher;

        int nNbResults = 0;

        try
        {
            searcher = AnnounceSearchService.getInstance( ).getSearcher( );

            Collection<String> queries = new ArrayList<>( );
            Collection<String> sectors = new ArrayList<>( );
            Collection<BooleanClause.Occur> flags = new ArrayList<>( );

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
            queryTypeBuilder.add( new Term( SearchItem.FIELD_TYPE, AnnouncePlugin.PLUGIN_NAME + "e" ) );
            queries.add( queryTypeBuilder.build( ).toString( ) );
            sectors.add( SearchItem.FIELD_TYPE );
            flags.add( BooleanClause.Occur.MUST );

            // Keywords in title or description
            if ( StringUtils.isNotBlank( filter.getKeywords( ) ) )
            {
                PhraseQuery.Builder queryContentBuilder = new PhraseQuery.Builder( );
                queryContentBuilder.add( new Term( SearchItem.FIELD_CONTENTS, filter.getKeywords( ) ) );
                queries.add( queryContentBuilder.build( ).toString( ) );
                sectors.add( SearchItem.FIELD_CONTENTS );
                flags.add( BooleanClause.Occur.MUST );
            }

            // contains range date
            if ( ( filter.getDateMin( ) != null ) || ( filter.getDateMax( ) != null ) )
            {
                Date dateMinToSearch = new Date( 0L );
                Date dateMaxToSearch = new Date( );
                if ( filter.getDateMin( ) != null )
                {
                    dateMinToSearch = filter.getDateMin( );
                }

                if ( filter.getDateMax( ) != null )
                {
                    dateMaxToSearch = filter.getDateMax( );
                }

                // String stringDateMin = DateUtil.
                String strLowerTerm = _dayFormat.format( dateMinToSearch );
                String strUpperTerm = _dayFormat.format( dateMaxToSearch );
                BytesRef bRLowerTerm = new BytesRef( strLowerTerm );
                BytesRef bRUpperTerm = new BytesRef( strUpperTerm );
                Query queryRangeDate = new TermRangeQuery( SearchItem.FIELD_DATE, bRLowerTerm, bRUpperTerm, true, true );
                queries.add( queryRangeDate.toString( ) );
                sectors.add( SearchItem.FIELD_DATE );
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
        ArrayList<SearchItem> listResults = new ArrayList<>( );
        List<Integer> listIdAnnounces = new ArrayList<>( );
        IndexSearcher searcher;

        Date dateMinToSearch;
        Date dateMaxToSearch;
        int nNbResults = 0;
        try
        {
            searcher = AnnounceSearchService.getInstance( ).getSearcher( );
            BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();

            // Category id
            if ( filter.getIdCategory( ) != NO_CATEGORY )
            {
                Query queryCategoryId = new TermQuery( new Term( AnnounceSearchItem.FIELD_CATEGORY_ID, String.valueOf( filter.getIdCategory( ) ) ) );

                booleanQueryBuilder.add(queryCategoryId, BooleanClause.Occur.MUST);
            }


            // Sector id
            if ( filter.getIdSector( ) != NO_SECTOR )
            {
                Query querySectorId = new TermQuery( new Term( AnnounceSearchItem.FIELD_SECTOR_ID, String.valueOf( filter.getIdSector( ) ) ) );
                booleanQueryBuilder.add(querySectorId, BooleanClause.Occur.MUST);
            }

            // Type (=announce)
            Query queryType = new TermQuery(new Term(SearchItem.FIELD_TYPE, AnnouncePlugin.PLUGIN_NAME));
            booleanQueryBuilder.add(queryType, BooleanClause.Occur.MUST);

            // Keywords in title or description
            if ( StringUtils.isNotBlank( filter.getKeywords( ) ) )
            {
                Analyzer analyzer = AnnounceSearchService.getInstance().getAnalyzer();
                MultiFieldQueryParser parser = new MultiFieldQueryParser(
                        new String[]{SearchItem.FIELD_TITLE, SearchItem.FIELD_SUMMARY, SearchItem.FIELD_CONTENTS},
                        analyzer
                );
                Query queryKeywords = parser.parse(filter.getKeywords());
                booleanQueryBuilder.add(queryKeywords, BooleanClause.Occur.MUST);
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
                String strLowerTerm = _dayFormat.format(dateMinToSearch);
                String strUpperTerm = _dayFormat.format(dateMaxToSearch);
                BytesRef bRLowerTerm = new BytesRef(strLowerTerm);
                BytesRef bRUpperTerm = new BytesRef(strUpperTerm);
                Query queryRangeDate = TermRangeQuery.newStringRange(SearchItem.FIELD_DATE, strLowerTerm, strUpperTerm, true, true);
                booleanQueryBuilder.add(queryRangeDate, BooleanClause.Occur.MUST);
            }

            // contains range price
            if ( ( filter.getPriceMin( ) > 0 ) || ( filter.getPriceMax( ) > 0 ) )
            {
                int nPriceMin = (filter.getPriceMin() > 0) ? filter.getPriceMin() : 0;
                int nPriceMax = (filter.getPriceMax() > 0) ? filter.getPriceMax() : Integer.MAX_VALUE;
                Query queryRangePrice = TermRangeQuery.newStringRange(
                        AnnounceSearchItem.FIELD_PRICE,
                        AnnounceSearchService.formatPriceForIndexer(nPriceMin),
                        AnnounceSearchService.formatPriceForIndexer(nPriceMax),
                        true,
                        true
                );
                booleanQueryBuilder.add(queryRangePrice, BooleanClause.Occur.MUST);
            }

            Query queryMulti = booleanQueryBuilder.build();

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
            // if keywords are not empty, we use lucene score to filter results to sort results
            if(filter.getKeywords() != null && filter.getKeywords().length() > 0)
            {
                float luceneMinScore = Float.parseFloat( AppPropertiesService.getProperty( PROPERTY_LUCENE_MIN_SCORE, "0.5" ) );

             for ( int i = nFrom; i < nTo; i++ )
             {
                 int docId = hits[i].doc;
                 float score = hits[i].score;
                 if( score < luceneMinScore )
                 {
                     break;
                 }
                 Document document = searcher.doc( docId );
                 SearchItem si = new SearchItem( document );
                 listAnnouncesResult.add(AnnounceHome.findByPrimaryKey(Integer.parseInt(si.getId())));
                 listResults.add( si );
             }
              nNbResults = listAnnouncesResult.size();
            }
         else
            {
             for ( int i = nFrom; i < nTo; i++ )
             {
                 int docId = hits[i].doc;
                 Document document = searcher.doc( docId );
                 SearchItem si = new SearchItem( document );
                 listIdAnnounces.add( Integer.parseInt( si.getId( ) ) );
                 listResults.add( si );
             }
             listAnnouncesResult.addAll(AnnounceHome.findByListId( listIdAnnounces, anSort ));
             nNbResults = listAnnouncesResult.size();
            }
        }
        catch( Exception e )
        {
            AppLogService.error( e.getMessage( ), e );
        }

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
