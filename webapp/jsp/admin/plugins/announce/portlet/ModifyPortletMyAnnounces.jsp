<%@ page errorPage="../../../ErrorPage.jsp" %>

<jsp:include page="../../../PortletAdminHeader.jsp" />

<jsp:useBean id="myAnnouncesPortlet" scope="session" class="fr.paris.lutece.plugins.announce.web.portlet.MyAnnouncesPortletJspBean" />

<% myAnnouncesPortlet.init( request, myAnnouncesPortlet.RIGHT_MANAGE_ADMIN_SITE ); %>
<%= myAnnouncesPortlet.getModify ( request ) %>

<%@ include file="../../../AdminFooter.jsp" %>


