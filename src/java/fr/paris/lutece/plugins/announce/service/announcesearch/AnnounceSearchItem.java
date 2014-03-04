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

import fr.paris.lutece.portal.service.search.SearchItem;

import org.apache.lucene.document.Document;


/**
 * AnnounceSearchItem
 */
public class AnnounceSearchItem extends SearchItem
{
    /**
     * Sector category id
     */
    public static final String FIELD_CATEGORY_ID = "category_id";

    /**
     * Sector id announce
     */
    public static final String FIELD_ID_ANNOUNCE = "id_announce";

    /**
     * Sector tags
     */
    public static final String FIELD_TAGS = "tags";

    // Variables declarations
    private String _strCategoryId;

    /**
     * Constructor
     *
     * @param document The Lucene {@link Document}
     */
    public AnnounceSearchItem( Document document )
    {
        super( document );
        setCategoryId( document.get( FIELD_CATEGORY_ID ) );
    }

    /**
     * gets id catgory
     * @return the _strSubjectId
     */
    public String getCategoryId(  )
    {
        return _strCategoryId;
    }

    /**
     * sets id category
     * @param strCategoryId the _strSubjectId to set
     */
    public void setCategoryId( String strCategoryId )
    {
        _strCategoryId = strCategoryId;
    }
}
