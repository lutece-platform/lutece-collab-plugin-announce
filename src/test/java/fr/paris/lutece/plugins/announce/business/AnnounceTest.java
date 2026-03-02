/*
 * Copyright (c) 2002-2026, City of Paris
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

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import fr.paris.lutece.plugins.announce.service.AnnounceCacheService;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Announce business layer CRUD tests
 */
public class AnnounceTest extends LuteceTestCase
{
    private static final String SECTOR_LABEL1 = "Test Sector 1";
    private static final String SECTOR_LABEL2 = "Test Sector 2";
    private static final String SECTOR_DESCRIPTION1 = "Description of test sector 1";
    private static final String SECTOR_DESCRIPTION2 = "Description of test sector 2";
    private static final String TAGS1 = "tag1 tag2";
    private static final String TAGS2 = "tag3 tag4";
    private static final String CATEGORY_LABEL1 = "Test Category 1";
    private static final String CATEGORY_LABEL2 = "Test Category 2";
    private static final String ANNOUNCE_TITLE1 = "Test Announce 1";
    private static final String ANNOUNCE_TITLE2 = "Test Announce 2";
    private static final String ANNOUNCE_DESCRIPTION1 = "Description of test announce 1";
    private static final String ANNOUNCE_DESCRIPTION2 = "Description of test announce 2";
    private static final String ANNOUNCE_USER = "junit_user";
    private static final String ANNOUNCE_CONTACT = "junit@test.fr";

    /**
     * Test CRUD operations on Sector, Category and Announce entities
     */
    public void testBusinessAnnounce( )
    {
        // ---- SECTOR CRUD ----

        // Create
        Sector sector = new Sector( );
        sector.setLabel( SECTOR_LABEL1 );
        sector.setDescription( SECTOR_DESCRIPTION1 );
        sector.setTags( TAGS1 );
        sector.setAnnouncesValidation( false );
        sector.setOrder( 1 );
        SectorHome.create( sector );
        assertTrue( sector.getId( ) > 0 );

        // Read
        Sector sectorStored = SectorHome.findByPrimaryKey( sector.getId( ) );
        assertNotNull( sectorStored );
        assertEquals( SECTOR_LABEL1, sectorStored.getLabel( ) );
        assertEquals( SECTOR_DESCRIPTION1, sectorStored.getDescription( ) );
        assertEquals( TAGS1, sectorStored.getTags( ) );

        // Update
        sector.setLabel( SECTOR_LABEL2 );
        sector.setDescription( SECTOR_DESCRIPTION2 );
        sector.setTags( TAGS2 );
        sector.setAnnouncesValidation( true );
        SectorHome.update( sector );

        sectorStored = SectorHome.findByPrimaryKey( sector.getId( ) );
        assertNotNull( sectorStored );
        assertEquals( SECTOR_LABEL2, sectorStored.getLabel( ) );
        assertEquals( SECTOR_DESCRIPTION2, sectorStored.getDescription( ) );
        assertEquals( TAGS2, sectorStored.getTags( ) );
        assertTrue( sectorStored.getAnnouncesValidation( ) );

        // List
        Collection<Sector> listSectors = SectorHome.findAll( );
        assertNotNull( listSectors );
        assertTrue( listSectors.size( ) > 0 );

        // ---- CATEGORY CRUD ----

        // Create
        Category category = new Category( );
        category.setLabel( CATEGORY_LABEL1 );
        category.setIdSector( sector.getId( ) );
        category.setAnnouncesValidation( 0 );
        category.setDisplayPrice( true );
        category.setPriceMandatory( false );
        category.setDisplayCaptcha( false );
        CategoryHome.create( category );
        assertTrue( category.getId( ) > 0 );

        // Read
        Category categoryStored = CategoryHome.findByPrimaryKey( category.getId( ) );
        assertNotNull( categoryStored );
        assertEquals( CATEGORY_LABEL1, categoryStored.getLabel( ) );
        assertEquals( sector.getId( ), categoryStored.getIdSector( ) );
        assertTrue( categoryStored.getDisplayPrice( ) );

        // Update
        category.setLabel( CATEGORY_LABEL2 );
        category.setDisplayPrice( false );
        category.setAnnouncesValidation( 1 );
        CategoryHome.update( category );

        categoryStored = CategoryHome.findByPrimaryKey( category.getId( ) );
        assertNotNull( categoryStored );
        assertEquals( CATEGORY_LABEL2, categoryStored.getLabel( ) );
        assertFalse( categoryStored.getDisplayPrice( ) );
        assertEquals( 1, categoryStored.getAnnouncesValidation( ) );

        // List
        List<Category> listCategories = CategoryHome.findAll( );
        assertNotNull( listCategories );
        assertTrue( listCategories.size( ) > 0 );

        // ---- ANNOUNCE CRUD ----

        // Create
        Announce announce = new Announce( );
        announce.setTitle( ANNOUNCE_TITLE1 );
        announce.setDescription( ANNOUNCE_DESCRIPTION1 );
        announce.setCategory( category );
        announce.setUserName( ANNOUNCE_USER );
        announce.setContactInformation( ANNOUNCE_CONTACT );
        announce.setTags( TAGS1 );
        announce.setPrice( 10.0 );
        announce.setPublished( false );
        announce.setSuspended( false );
        announce.setSuspendedByUser( false );
        announce.setHasPictures( false );
        announce.setDateCreation( new Timestamp( System.currentTimeMillis( ) ) );
        announce.setDateModification( new Timestamp( System.currentTimeMillis( ) ) );
        announce.setTimePublication( System.currentTimeMillis( ) );
        AnnounceHome.create( announce );
        assertTrue( announce.getId( ) > 0 );

        // Read
        Announce announceStored = AnnounceHome.findByPrimaryKey( announce.getId( ) );
        assertNotNull( announceStored );
        assertEquals( ANNOUNCE_TITLE1, announceStored.getTitle( ) );
        assertEquals( ANNOUNCE_DESCRIPTION1, announceStored.getDescription( ) );
        assertEquals( ANNOUNCE_USER, announceStored.getUserName( ) );
        assertEquals( ANNOUNCE_CONTACT, announceStored.getContactInformation( ) );
        assertEquals( TAGS1, announceStored.getTags( ) );

        // Update
        announce.setTitle( ANNOUNCE_TITLE2 );
        announce.setDescription( ANNOUNCE_DESCRIPTION2 );
        announce.setTags( TAGS2 );
        announce.setPrice( 25.0 );
        announce.setPublished( true );
        AnnounceHome.update( announce );
        AnnounceCacheService.getService( ).removeKey( AnnounceCacheService.getAnnounceCacheKey( announce.getId( ) ) );

        announceStored = AnnounceHome.findByPrimaryKey( announce.getId( ) );
        assertNotNull( announceStored );
        assertEquals( ANNOUNCE_TITLE2, announceStored.getTitle( ) );
        assertEquals( ANNOUNCE_DESCRIPTION2, announceStored.getDescription( ) );
        assertEquals( TAGS2, announceStored.getTags( ) );

        // List
        List<Integer> listIds = AnnounceHome.findAll( AnnounceSort.DEFAULT_SORT );
        assertNotNull( listIds );
        assertTrue( listIds.size( ) > 0 );

        // Delete announce
        AnnounceHome.remove( announce.getId( ) );
        AnnounceCacheService.getService( ).removeKey( AnnounceCacheService.getAnnounceCacheKey( announce.getId( ) ) );
        announceStored = AnnounceHome.findByPrimaryKey( announce.getId( ) );
        assertNull( announceStored );

        // Cleanup category and sector
        CategoryHome.remove( category );
        SectorHome.remove( sector );
    }
}
