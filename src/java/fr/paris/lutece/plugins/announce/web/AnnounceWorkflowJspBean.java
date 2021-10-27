/*
 * Copyright (c) 2002-2020, City of Paris
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

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.announce.business.Announce;
import fr.paris.lutece.plugins.announce.business.AnnounceHome;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Announce workflow JSP Bean
 */
@Controller( controllerJsp = "ManageAnnounceWorkflow.jsp", controllerPath = "jsp/admin/plugins/announce/", right = AnnounceUserJspBean.RIGHT_MANAGE_ANNOUNCE )
public class AnnounceWorkflowJspBean extends MVCAdminJspBean
{
    private static final long serialVersionUID = -173284996603895865L;

    // Parameters
    private static final String PARAMETER_ID_ACTION = "id_action";
    private static final String PARAMETER_ID_ANNOUNCE = "id_announce";
    private static final String PARAMETER_BACK = "back";

    // Marks
    private static final String MARK_TASKS_FORM = "tasks_form";

    // Properties
    private static final String PROPERTY_PAGE_TITLE_TASKS_FORM_WORKFLOW = "announce.taskFormWorkflow.pageTitle";

    // Templates
    private static final String TEMPLATE_TASKS_FORM_WORKFLOW = "admin/plugins/announce/tasks_form_workflow.html";

    // Views
    private static final String VIEW_WORKFLOW_ACTION_FORM = "viewWorkflowActionForm";

    // Actions
    private static final String ACTION_DO_PROCESS_WORKFLOW_ACTION = "doProcessWorkflowAction";

    /**
     * Get the workflow action form before processing the action. If the action does not need to display any form, then redirect the user to the workflow action
     * processing page.
     * 
     * @param request
     *            The request
     * @return The HTML content to display, or the next URL to redirect the user to
     */
    @View( VIEW_WORKFLOW_ACTION_FORM )
    public String getWorkflowActionForm( HttpServletRequest request )
    {
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        String strIdAnnounce = request.getParameter( PARAMETER_ID_ANNOUNCE );
        
        if ( StringUtils.isNotEmpty( strIdAction ) && StringUtils.isNumeric( strIdAction ) && StringUtils.isNotEmpty( strIdAnnounce )
                && StringUtils.isNumeric( strIdAnnounce ) )
        {
            int nIdAction = Integer.parseInt( strIdAction );
            int nIdAnnounce = Integer.parseInt( strIdAnnounce );
            User user = getUser( );

            if ( WorkflowService.getInstance( ).isDisplayTasksForm( nIdAction, getLocale( ) ) )
            {
                String strHtmlTasksForm = WorkflowService.getInstance( ).getDisplayTasksForm( nIdAnnounce, Announce.RESOURCE_TYPE, nIdAction, request,
                        getLocale( ), user );

                Map<String, Object> model = new HashMap<>( );

                model.put( MARK_TASKS_FORM, strHtmlTasksForm );
                model.put( PARAMETER_ID_ACTION, nIdAction );
                model.put( PARAMETER_ID_ANNOUNCE, nIdAnnounce );

                return getPage( PROPERTY_PAGE_TITLE_TASKS_FORM_WORKFLOW, TEMPLATE_TASKS_FORM_WORKFLOW, model );
            }

            return doProcessWorkflowAction( request );
        }

        return redirect( request, AppPathService.getBaseUrl( request ) + AnnounceJspBean.getURLManageAnnounces( request ) );
    }

    /**
     * Do process a workflow action over an appointment
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_PROCESS_WORKFLOW_ACTION )
    public String doProcessWorkflowAction( HttpServletRequest request )
    {
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        String strIdAnnounce = request.getParameter( PARAMETER_ID_ANNOUNCE );

        if ( StringUtils.isNotEmpty( strIdAction ) && StringUtils.isNumeric( strIdAction ) && StringUtils.isNotEmpty( strIdAnnounce )
                && StringUtils.isNumeric( strIdAnnounce ) )
        {
            int nIdAction = Integer.parseInt( strIdAction );
            int nIdAnnounce = Integer.parseInt( strIdAnnounce );
            User user = getUser( );

            Announce announce = AnnounceHome.findByPrimaryKey( nIdAnnounce );

            if ( request.getParameter( PARAMETER_BACK ) == null )
            {
                if ( WorkflowService.getInstance( ).isDisplayTasksForm( nIdAction, getLocale( ) ) )
                {
                    String strError = WorkflowService.getInstance( ).doSaveTasksForm( nIdAnnounce, Announce.RESOURCE_TYPE, nIdAction,
                            announce.getCategory( ).getId( ), request, getLocale( ), user );

                    if ( strError != null )
                    {
                        return redirect( request, strError );
                    }
                }

                WorkflowService.getInstance( ).doProcessAction( nIdAnnounce, Announce.RESOURCE_TYPE, nIdAction, announce.getCategory( ).getId( ), request,
                        getLocale( ), false, user );
            }
        }

        return redirect( request, AnnounceJspBean.getURLManageAnnounces( request ) );
    }
}
