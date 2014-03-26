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

import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.ResponseImageResourceProvider;
import fr.paris.lutece.portal.service.resource.IExtendableResource;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;


/**
 * This is the business class for the object Announce
 */
public class Announce implements Serializable, IExtendableResource
{
    /**
     * Announce resource type
     */
    public static final String RESOURCE_TYPE = "announce";

    private static final long serialVersionUID = 4717207701123679302L;

    // Variables declarations
    private String _strTitle;
    private String _strDescription;
    private String _strContactInformation;
    private int _nIdAnnounce;
    private Category _category;
    private boolean _bPublished;
    private boolean _bSuspended;
    private boolean _bSuspendedByUser;
    private String _strUserName;
    private boolean _bHasPictures;
    private List<Integer> _listIdImageResponse;
    private Timestamp _dateCreation;
    private long _lTimePublication;
    private String _strPrice;
    private String _strTags;
    private List<Response> _listResponse;

    /**
     * Get the list of responses of this announce
     * @return The list of responses of this announce
     */
    public List<Response> getListResponse( )
    {
        return _listResponse;
    }

    /**
     * Set the list of responses of this announce
     * @param listResponse The list of responses of this announce
     */
    public void setListResponse( List<Response> listResponse )
    {
        this._listResponse = listResponse;
    }

    /**
     * Returns the IdAnnounce
     * @return The IdAnnounce
     */
    public int getId( )
    {
        return _nIdAnnounce;
    }

    /**
     * Sets the IdAnnounce
     * @param nIdAnnounce The IdAnnounce
     */
    public void setId( int nIdAnnounce )
    {
        _nIdAnnounce = nIdAnnounce;
    }

    /**
     * Returns the Published state
     * @return The Published state
     */
    public boolean getPublished( )
    {
        return _bPublished;
    }

    /**
     * Sets the Published state
     * @param bPublished the published state
     */
    public void setPublished( boolean bPublished )
    {
        _bPublished = bPublished;
    }

    /**
     * gets the suspended state
     * @return the suspended state of announce
     */
    public boolean getSuspended( )
    {
        return _bSuspended;
    }

    /**
     * sets the suspended state
     * @param bSuspended the suspended state
     */
    public void setSuspended( boolean bSuspended )
    {
        _bSuspended = bSuspended;
    }

    /**
     * gets the title
     * @return the title of announce
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * gets the description
     * @return the description of announce
     */
    public String getDescription( )
    {
        return _strDescription;
    }

    /**
     * gets the title
     * @param strTitle gets the suspended state
     */
    public void setTitle( String strTitle )
    {
        _strTitle = strTitle;
    }

    /**
     * sets the description
     * @param strDescription the description
     */
    public void setDescription( String strDescription )
    {
        _strDescription = strDescription;
    }

    /**
     * gets the category
     * @return the announce category
     */
    public Category getCategory( )
    {
        return _category;
    }

    /**
     * sets the announce category
     * @param category the announce category
     */
    public void setCategory( Category category )
    {
        _category = category;
    }

    /**
     * gets the user name
     * @return the username
     */
    public String getUserName( )
    {
        return _strUserName;
    }

    /**
     * sets the announce user name
     * @param strUserName the user name
     */
    public void setUserName( String strUserName )
    {
        _strUserName = strUserName;
    }

    /**
     * weather or not the announce has pictures in slideshow
     * @return weather or not the announce has pictures in slideshow
     */
    public boolean getHasPictures( )
    {
        return _bHasPictures;
    }

    /**
     * set weather or not the announce has pictures
     * @param bHasPictures weather or not the announce has pictures
     */
    public void setHasPictures( boolean bHasPictures )
    {
        _bHasPictures = bHasPictures;
    }

    /**
     * Gets the list of ids of images responses of this announce
     * @return The list of ids of images responses of this announce
     */
    public List<Integer> getListIdImageResponse( )
    {
        return _listIdImageResponse;
    }

    /**
     * Sets the list of ids of images responses of this announce
     * @param listIdImageResponse the list of ids of image responses of this
     *            announce
     */
    public void setListIdImageResponse( List<Integer> listIdImageResponse )
    {
        this._listIdImageResponse = listIdImageResponse;
    }

    /**
     * gets the creation date
     * @return the creation date
     */
    public Timestamp getDateCreation( )
    {
        return _dateCreation;
    }

    /**
     * sets the creation date
     * @param time the creation date
     */
    public void setDateCreation( Timestamp time )
    {
        this._dateCreation = time;
    }

    /**
     * gets the publication time
     * @return the publication time
     */
    public long getTimePublication( )
    {
        return _lTimePublication;
    }

    /**
     * sets the publication time
     * @param lTime the publication time
     */
    public void setTimePublication( long lTime )
    {
        this._lTimePublication = lTime;
    }

    /**
     * gets the contact information
     * @return the contact information
     */
    public String getContactInformation( )
    {
        return _strContactInformation;
    }

    /**
     * sets the contact information
     * @param strContactInformation the contact information
     */
    public void setContactInformation( String strContactInformation )
    {
        this._strContactInformation = strContactInformation;
    }

    /**
     * gets the price of the announce
     * @return the price of announce
     */
    public String getPrice( )
    {
        return _strPrice;
    }

    /**
     * sets the price of the announce
     * @param price the price of the announce
     */
    public void setPrice( String price )
    {
        this._strPrice = price;
    }

    /**
     * Check if the announce was suspended by the user or not
     * @return True if the announce was suspended by the user or not, false
     *         otherwise
     */
    public boolean getSuspendedByUser( )
    {
        return _bSuspendedByUser;
    }

    /**
     * Set the announce was suspended by the user or not
     * @param bSuspendedByUser True to set the announce was suspended by the
     *            user or not, false otherwise
     */
    public void setSuspendedByUser( boolean bSuspendedByUser )
    {
        this._bSuspendedByUser = bSuspendedByUser;
    }

    /**
     * gets the tags of announce
     * @return gets the tags of announce
     */
    public String getTags( )
    {
        return _strTags;
    }

    /**
     * sets the Tags of announce
     * @param strTags the Tags of announce
     */
    public void setTags( String strTags )
    {
        _strTags = strTags;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdExtendableResource( )
    {
        return Integer.toString( getId( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtendableResourceType( )
    {
        return RESOURCE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtendableResourceName( )
    {
        return getTitle( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtendableResourceDescription( )
    {
        return getDescription( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtendableResourceImageUrl( )
    {
        if ( getHasPictures( ) && getListIdImageResponse( ) != null && getListIdImageResponse( ).size( ) > 0 )
        {
            ResponseImageResourceProvider.getUrlDownloadImageResponse( getListIdImageResponse( ).get( 0 ) );
        }
        return null;
    }
}
