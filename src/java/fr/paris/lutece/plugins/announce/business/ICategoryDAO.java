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
package fr.paris.lutece.plugins.announce.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;

/**
 *
 * @author kpomdagu
 */
public interface ICategoryDAO
{
    /**
     * Delete a record from the table
     *
     * @param category
     *            The category object
     * @param plugin
     *            The plugin
     */
    void delete( Category category, Plugin plugin );

    /**
     * Insert a new record in the table.
     *
     *
     * @param category
     *            The Category object
     * @param plugin
     *            The plugin
     */
    void insert( Category category, Plugin plugin );

    /**
     * Load the data of Category from the table
     * 
     * @param nCategoryId
     *            The identifier of Category
     * @param plugin
     *            The plugin
     * @return the instance of the Category
     */
    Category load( int nCategoryId, Plugin plugin );

    /**
     * Load the list of categorys
     *
     *
     * @param plugin
     *            The plugin
     * @return The Collection of the Categories
     */
    List<Category> selectAll( Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param category
     *            The reference of category
     * @param plugin
     *            The plugin
     */
    void store( Category category, Plugin plugin );

    /**
     * Selects the list of categories associated to the specified sector
     * 
     * @param sector
     *            The concerned sector
     * @param plugin
     *            The plugin announce
     * @return The collection of categories
     */
    List<Category> selectCategoriesForSector( Sector sector, Plugin plugin );

    /**
     * gets the categories reference list
     * 
     * @param plugin
     *            the plugin
     * @return the categories reference list
     */
    ReferenceList selectCategoriesReferenceList( Plugin plugin );

    /**
     * counts the entries for a given category form
     * 
     * @param category
     *            the category
     * @param plugin
     *            the plugin
     * @return number of entries for a given category form
     */
    int countEntriesForCategory( Category category, Plugin plugin );

    /**
     * Count the number of published announce of a given category
     * 
     * @param category
     *            The category to get the number of published announce of
     * @param plugin
     *            The plugin
     * @return The number of published announce of the category
     */
    int countPublishedAnnouncesForCategory( Category category, Plugin plugin );

    int copyCategory( Category category, Plugin plugin );
}
