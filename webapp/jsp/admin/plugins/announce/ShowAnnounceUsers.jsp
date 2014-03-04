<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="announceUser" scope="session" class="fr.paris.lutece.plugins.announce.web.AnnounceUserJspBean" />

<% announceUser.init( request , announceUser.RIGHT_MANAGE_ANNOUNCE ); %>
<%= announceUser.getList( request )%>

<%@include file="../../AdminFooter.jsp" %>