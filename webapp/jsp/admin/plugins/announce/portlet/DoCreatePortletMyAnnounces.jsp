<%@ page errorPage="../../../ErrorPage.jsp" %>

<jsp:useBean id="myAnnouncesPortlet" scope="session" class="fr.paris.lutece.plugins.announce.web.portlet.MyAnnouncesPortletJspBean" />

<%
	myAnnouncesPortlet.init( request, myAnnouncesPortlet.RIGHT_MANAGE_ADMIN_SITE );
    response.sendRedirect( myAnnouncesPortlet.doCreate( request ) );
%>

