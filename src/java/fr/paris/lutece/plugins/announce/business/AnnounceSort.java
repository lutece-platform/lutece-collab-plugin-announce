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
package fr.paris.lutece.plugins.announce.business;

import org.apache.commons.lang.StringUtils;

/**
 * Announces sort
 */
public class AnnounceSort
{
    /**
     * Sort by creation date
     */
    public static final String SORT_DATE_CREATION = "date_creation";

    /**
     * Sort by creation date
     */
    public static final String SORT_DATE_PUBLICATION = "publication_time";

    /**
     * Sort by modification date
     */
    public static final String SORT_DATE_MODIFICATION = "date_modification";

    /**
     * Sort by title
     */
    public static final String SORT_TITLE = "title_announce";

    /**
     * Sort by price
     */
    public static final String SORT_PRICE = "price_announce";

    /**
     * Sort by description
     */
    public static final String SORT_DESCRIPTION = "description_announce";

    /**
     * Default sort to use
     */
    public static final AnnounceSort DEFAULT_SORT = new AnnounceSort( SORT_DATE_MODIFICATION, false );
    private final String _strSortColumn;
    private final boolean _bSortAsc;

    /**
     * Private constructor
     * 
     * @param strSortColumn
     *            The name of the database column to use to sort
     * @param bSortAsc
     *            True to sort ascending, false otherwise
     */
    private AnnounceSort( String strSortColumn, boolean bSortAsc )
    {
        this._strSortColumn = strSortColumn;
        this._bSortAsc = bSortAsc;
    }

    /**
     * Get the name of the sorted column
     * 
     * @return The name of the sorted column
     */
    public String getSortColumn( )
    {
        return _strSortColumn;
    }

    /**
     * Get the name of the sorted column
     * 
     * @return The name of the sorted column
     */
    public boolean getSortAsc( )
    {
        return _bSortAsc;
    }

    /**
     * Get an announce sort from a string of the name of the column to sort
     * 
     * @param strSort
     *            The name of the column to sort
     * @param bSortAsc
     *            True to sort ascending false otherwise
     * @return The corresponding sort, or the default sort if the given parameter does not match any sortable column name
     */
    public static AnnounceSort getAnnounceSort( String strSort, boolean bSortAsc )
    {
        if ( StringUtils.equals( SORT_DATE_CREATION, strSort ) || StringUtils.equals( SORT_DATE_PUBLICATION, strSort )
                || StringUtils.equals( SORT_DATE_MODIFICATION, strSort ) || StringUtils.equals( SORT_TITLE, strSort )
                || StringUtils.equals( SORT_DESCRIPTION, strSort ) || StringUtils.equals( SORT_PRICE, strSort ) )
        {
            return new AnnounceSort( strSort, bSortAsc );
        }

        return DEFAULT_SORT;
    }
}
