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

import java.util.Collection;


/**
 * Sector
 */
public class Sector implements RBACResource
{
    /**
     */
    public static final String RESOURCE_TYPE = "SECTOR";

    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    private int _nId;
    private String _strLabel;
    private String _strDescription;
    private int _nNumberCategories;
    private Collection<Category> _listCategories;
    private boolean _bAnnouncesValidation;
    private int _nOrder;
    private int _nNumberAnnounces;
    private String _strTags;

    /**
     * gets the id of sector
     * @return the sector id
     */
    public int getId(  )
    {
        return _nId;
    }

    /**
     * sets the id of sector
     * @param nId the sector id
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * gets the label of sector
     * @return gets the label of sector
     */
    public String getLabel(  )
    {
        return _strLabel;
    }

    /**
     * sets the label of sector
     * @param strLabel the label of sector
     */
    public void setLabel( String strLabel )
    {
        _strLabel = strLabel;
    }

    /**
     * gets the description of sector
     * @return the description of sector
     */
    public String getDescription(  )
    {
        return _strDescription;
    }

    /**
     * gets the description of sector
     * @param strDescription the description of sector
     */
    public void setDescription( String strDescription )
    {
        _strDescription = strDescription;
    }

    /**
     * gets the number of categories for the sector
     * @return the number of categories for the sector
     */
    public int getNumberCategories(  )
    {
        return _nNumberCategories;
    }

    /**
     * sets the number of categories for the sector
     * @param nNumberCategories the number of categories for the sector
     */
    public void setNumberCategories( int nNumberCategories )
    {
        _nNumberCategories = nNumberCategories;
    }

    /**
     * gets the list of categories of the sector
     * @return the list of categories of the sector
     */
    public Collection<Category> getListCategories(  )
    {
        return _listCategories;
    }

    /**
     * sets the list of categories of the sector
     * @param listCategories the list of categories of the sector
     */
    public void setListCategories( Collection<Category> listCategories )
    {
        _listCategories = listCategories;
    }

    /**
     * gets weather or not the announces of the filed must be moderated
     * @return weather or not the announces of the filed must be moderated
     */
    public boolean getAnnouncesValidation(  )
    {
        return _bAnnouncesValidation;
    }

    /**
     * sets weather or not the announces of the filed must be moderated
     * @param bAnnouncesValidation weather or not the announces of the filed must be moderated
     */
    public void setAnnouncesValidation( boolean bAnnouncesValidation )
    {
        _bAnnouncesValidation = bAnnouncesValidation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceId(  )
    {
        return "" + getId(  );
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
     * Get the order of the announce
     * @return The order of the announce
     */
    public int getOrder(  )
    {
        return _nOrder;
    }

    /**
     * Set the order of the announce
     * @param nOrder The order of the announce
     */
    public void setOrder( int nOrder )
    {
        this._nOrder = nOrder;
    }

    /**
     * Get the number of announces
     * @return The number of announces
     */
    public int getNumberAnnounces(  )
    {
        return _nNumberAnnounces;
    }

    /**
     * Set the number of announces
     * @param nNumberAnnounces The number of announces
     */
    public void setNumberAnnounces( int nNumberAnnounces )
    {
        this._nNumberAnnounces = nNumberAnnounces;
    }

    /**
     * gets the tags of sector
     * @return gets the tags of sector
     */
    public String getTags(  )
    {
        return _strTags;
    }

    /**
     * sets the Tags of sector
     * @param strTags the Tags of sector
     */
    public void setTags( String strTags )
    {
        _strTags = strTags;
    }
}
