<%@ page errorPage="../../../ErrorPage.jsp" %>

<jsp:useBean id="lastAnnouncesPortlet" scope="session" class="fr.paris.lutece.plugins.announce.web.portlet.LastAnnouncesPortletJspBean" />

<%
	lastAnnouncesPortlet.init( request, lastAnnouncesPortlet.RIGHT_MANAGE_ADMIN_SITE );
    response.sendRedirect( lastAnnouncesPortlet.doModify( request ) );
%>


