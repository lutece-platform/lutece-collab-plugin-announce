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

import fr.paris.lutece.util.filesystem.FileSystemUtil;

import org.apache.commons.fileupload.FileItem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


/**
 * AnnounceFileItem : builds fileItem from json response.
 */
public class AnnounceFileItem implements FileItem
{
    private static final long serialVersionUID = 1L;
    private byte[] _bValue;
    private String _strFileName;

    /**
     * AnnounceFileItem
     * @param bValue the byte value
     * @param strFileName the file name
     */
    public AnnounceFileItem( byte[] bValue, String strFileName )
    {
        _bValue = bValue;
        _strFileName = strFileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( )
    {
        _bValue = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] get( )
    {
        return _bValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentType( )
    {
        return FileSystemUtil.getMIMEType( _strFileName );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFieldName( )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream( ) throws IOException
    {
        return new ByteArrayInputStream( _bValue );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName( )
    {
        return _strFileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream getOutputStream( ) throws IOException
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize( )
    {
        return _bValue.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString( )
    {
        return new String( _bValue );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString( String encoding ) throws UnsupportedEncodingException
    {
        return new String( _bValue, encoding );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFormField( )
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInMemory( )
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFieldName( String strName )
    {
        // nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFormField( boolean bState )
    {
        // nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write( java.io.File file ) throws Exception
    {
        // nothing
    }
}
