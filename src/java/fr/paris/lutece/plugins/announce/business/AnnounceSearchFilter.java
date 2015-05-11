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
package fr.paris.lutece.plugins.announce.business;

import java.util.Date;


/**
 * Search filter for announces
 */
public class AnnounceSearchFilter
{
    private int _nIdFilter;
    private String _strKeywords;
    private int _nIdSector;
    private int _nIdCategory;
    private Date _dateMin;
    private Date _dateMax;
    private int _nPriceMin;
    private int _nPriceMax;

    /**
     * Get the id of the filter
     * @return The id of the filter
     */
    public int getIdFilter(  )
    {
        return _nIdFilter;
    }

    /**
     * Set the id of the filter
     * @param nIdFilter The id of the filter
     */
    public void setIdFilter( int nIdFilter )
    {
        this._nIdFilter = nIdFilter;
    }

    /**
     * Get the keywords of the filter
     * @return The keywords of the filter
     */
    public String getKeywords(  )
    {
        return _strKeywords;
    }

    /**
     * Set the keywords of the filter
     * @param strKeywords The keywords of the filter
     */
    public void setKeywords( String strKeywords )
    {
        this._strKeywords = strKeywords;
    }
    /**
     * Get the id of the sector of the filter
     * @return The id of the sector of the filter
     */
    public int getIdSector(  )
    {
        return _nIdSector;
    }

    /**
     * Set the id of the sector of the filter
     * @param nIdSector The id of the sector of the filter
     */
    public void setIdSector( int nIdSector )
    {
        this._nIdSector = nIdSector;
    }

    /**
     * Get the id of the category of the filter
     * @return The id of the category of the filter
     */
    public int getIdCategory(  )
    {
        return _nIdCategory;
    }

    /**
     * Set the id of the category of the filter
     * @param nIdCategory The id of the category of the filter
     */
    public void setIdCategory( int nIdCategory )
    {
        this._nIdCategory = nIdCategory;
    }

    /**
     * Get the minimum publication date of the filter
     * @return The minimum publication date of the filter
     */
    public Date getDateMin(  )
    {
        return _dateMin;
    }

    /**
     * Set the minimum publication date of the filter
     * @param dateMin The minimum publication date of the filter
     */
    public void setDateMin( Date dateMin )
    {
        this._dateMin = dateMin;
    }

    /**
     * Get the maximum publication date of the filter
     * @return The maximum publication date of the filter
     */
    public Date getDateMax(  )
    {
        return _dateMax;
    }

    /**
     * Set the maximum publication date of the filter
     * @param dateMax The maximum publication date of the filter
     */
    public void setDateMax( Date dateMax )
    {
        this._dateMax = dateMax;
    }

    /**
     * Get the minimum price of the filter
     * @return The minimum price of the filter
     */
    public int getPriceMin(  )
    {
        return _nPriceMin;
    }

    /**
     * Set the minimum price of the filter
     * @param nPriceMin The minimum price of the filter
     */
    public void setPriceMin( int nPriceMin )
    {
        this._nPriceMin = nPriceMin;
    }

    /**
     * Get the maximum price of the filter
     * @return The maximum price of the filter
     */
    public int getPriceMax(  )
    {
        return _nPriceMax;
    }

    /**
     * Set the maximum price of the filter
     * @param nPriceMax The maximum price of the filter
     */
    public void setPriceMax( int nPriceMax )
    {
        this._nPriceMax = nPriceMax;
    }
}
