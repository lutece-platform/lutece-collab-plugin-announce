<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="announce" scope="session" class="fr.paris.lutece.plugins.announce.web.AnnounceJspBean" />

<% announce.init( request , announce.RIGHT_MANAGE_ANNOUNCE ); %>
<%= announce.getPreviewAnnounce( request )%>

<%@include file="../../AdminFooter.jsp" %>