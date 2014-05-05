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
package fr.paris.lutece.plugins.announce.web;

import fr.paris.lutece.plugins.announce.utils.AnnounceUtils;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.sort.AttributeComparator;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 * Announce User Jsp Bean
 */
public class AnnounceUserJspBean extends PluginAdminPageJspBean
{
    /**
     * Right to manage announces
     */
    public static final String RIGHT_MANAGE_ANNOUNCE = "ANNOUNCE_MANAGEMENT";
    private static final long serialVersionUID = 302676061356843811L;

    /* Templates */
    private static final String TEMPLATE_ANNOUNCE_LIST_USERS = "admin/plugins/announce/list_users.html";

    /* Properties */
    private static final String PROPERTY_PAGE_TITLE_LIST_USERS = "announce.list_users.pageTitle";
    private static final String PROPERTY_ITEM_PER_PAGE = "module.mylutece.directory.items_per_page";

    // Parameters
    private static final String PARAMETER_PAGE_INDEX = "page_index";

    // Markers
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_USERS_LIST = "users_list";
    private static final String MARK_NAME_FOUND = "name_found";
    private static final String MARK_NO_USERS = "no_users";

    // Jsp
    private static final String JSP_URL_MANAGE_USERS = "jsp/admin/plugins/announce/ShowAnnounceUsers.jsp";

    // Session sectors
    private int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;
    private AttributeComparator _attributeComparator;

    /**
     * {@inheritDoc}
     */
    @Override
    public Plugin getPlugin(  )
    {
        Plugin plugin = super.getPlugin(  );

        if ( plugin == null )
        {
            plugin = AnnounceUtils.getPlugin(  );
        }

        return plugin;
    }

    /**
     * Get the list of the Announce users
     * @return a list containing all the Announce users
     * @param request The http request
     * @throws AccessDeniedException Message displayed if an exception occures
     */
    public String getList( HttpServletRequest request )
        throws AccessDeniedException
    {
        Boolean nameFound = false;
        Boolean noUsers = false;

        HashMap<String, Object> model = new HashMap<String, Object>(  );

        setPageTitleProperty( PROPERTY_PAGE_TITLE_LIST_USERS );
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        List<LuteceUser> listUsers = (List<LuteceUser>) SecurityService.getInstance(  ).getUsers(  );

        if ( listUsers != null )
        {
            for ( LuteceUser user : listUsers )
            {
                if ( StringUtils.isNotEmpty( user.getUserInfo( LuteceUser.NAME_GIVEN ) ) ||
                        StringUtils.isNotEmpty( user.getUserInfo( LuteceUser.NAME_FAMILY ) ) )
                {
                    nameFound = true;

                    break;
                }
            }
        }
        else
        {
            noUsers = true;
        }

        String strSort = request.getParameter( Parameters.SORTED_ATTRIBUTE_NAME );

        if ( StringUtils.isNotEmpty( strSort ) )
        {
            _attributeComparator = new AttributeComparator( strSort,
                    Boolean.parseBoolean( request.getParameter( Parameters.SORTED_ASC ) ) );
        }

        if ( _attributeComparator != null )
        {
            Collections.sort( listUsers, _attributeComparator );
        }

        Paginator<LuteceUser> paginator = new Paginator<LuteceUser>( listUsers, _nItemsPerPage, getUrlPage(  ),
                PARAMETER_PAGE_INDEX, _strCurrentPageIndex );

        model.put( MARK_NAME_FOUND, nameFound );
        model.put( MARK_NO_USERS, noUsers );
        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_USERS_LIST, paginator.getPageItems(  ) );
        model.put( MARK_LOCALE, getLocale(  ) );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_ANNOUNCE_LIST_USERS, getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Return UrlPage Url
     * @return url
     */
    private String getUrlPage(  )
    {
        UrlItem url = new UrlItem( JSP_URL_MANAGE_USERS );

        return url.getUrl(  );
    }
}
