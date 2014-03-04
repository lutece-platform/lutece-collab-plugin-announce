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

import fr.paris.lutece.portal.service.rbac.RBACResource;


/**
 * Business class for category
 */
public class Category implements RBACResource
{
    /**
     * The resource type of categories
     */
    public static String RESOURCE_TYPE = "CATEGORY";

    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    private int _nId;
    private int _nIdSector;
    private String _strLabel;
    private int _nNumberAnnounces;
    private int _nAnnouncesValidation;
    private boolean _bDisplayPrice;
    private int _nIdMailingList;
    private String _strTags;
    private String _strLabelSector;

    /**
     * gets the id of category
     * @return the id of category
     */
    public int getId(  )
    {
        return _nId;
    }

    /**
     * sets the id of category
     * @param nId the id of category
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * gets the id of category sector
     * @return the id of category sector
     */
    public int getIdSector(  )
    {
        return _nIdSector;
    }

    /**
     * sets the id of category sector
     * @param nIdSector the id of category sector
     */
    public void setIdSector( int nIdSector )
    {
        _nIdSector = nIdSector;
    }

    /**
     * gets the label
     * @return the label
     */
    public String getLabel(  )
    {
        return _strLabel;
    }

    /**
     * sets the label
     * @param strLabel the label
     */
    public void setLabel( String strLabel )
    {
        _strLabel = strLabel;
    }

    /**
     * gets the number of announces
     * @return the number of announces
     */
    public int getNumberAnnounces(  )
    {
        return _nNumberAnnounces;
    }

    /**
     * sets the number of announces
     * @param nNumberAnnounces the number of announces
     */
    public void setNumberAnnounces( int nNumberAnnounces )
    {
        _nNumberAnnounces = nNumberAnnounces;
    }

    /**
     * weather or not the announces must be moderated before publishing
     * @return weather or not the announces must be moderated before publishing
     */
    public int getAnnouncesValidation(  )
    {
        return _nAnnouncesValidation;
    }

    /**
     * sets weather or not the announces must be moderated before publishing
     * @param nAnnouncesValidation weather or not the announces must be
     *            moderated before publishing
     */
    public void setAnnouncesValidation( int nAnnouncesValidation )
    {
        _nAnnouncesValidation = nAnnouncesValidation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceId(  )
    {
        return Integer.toString( getId(  ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceTypeCode(  )
    {
        return RESOURCE_TYPE;
    }

    /**
     * weather or not the category must display the price of announce
     * @return weather or not the category must display the price of announce
     */
    public boolean getDisplayPrice(  )
    {
        return _bDisplayPrice;
    }

    /**
     * sets weather or not the category must display the price of announce
     * @param displayPrice weather or not the category must display the price of
     *            announce
     */
    public void setDisplayPrice( boolean displayPrice )
    {
        this._bDisplayPrice = displayPrice;
    }

    /**
     * Get the id of the mailing list associated with this category
     * @return The id of the mailing list associated with this category
     */
    public int getIdMailingList(  )
    {
        return _nIdMailingList;
    }

    /**
     * Set the id of the mailing list associated with this category
     * @param nIdMailingList The id of the mailing list associated with this
     *            category
     */
    public void setIdMailingList( int nIdMailingList )
    {
        this._nIdMailingList = nIdMailingList;
    }

    /**
     * gets the tags of category
     * @return gets the tags of category
     */
    public String getTags(  )
    {
        return _strTags;
    }

    /**
     * sets the Tags of category
     * @param strTags the Tags of category
     */
    public void setTags( String strTags )
    {
        _strTags = strTags;
    }

    /**
     * @return the _strLabelSector
     */
    public String getLabelSector( )
    {
        return _strLabelSector;
    }

    /**
     * @param strLabelSector the _strLabelSector to set
     */
    public void setLabelSector( String strLabelSector )
    {
        this._strLabelSector = strLabelSector;
    }
}
