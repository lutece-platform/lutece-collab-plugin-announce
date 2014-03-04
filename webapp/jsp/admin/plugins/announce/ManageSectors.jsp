<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="announceSector" scope="session" class="fr.paris.lutece.plugins.announce.web.SectorJspBean" />

<% announceSector.init( request , announceSector.RIGHT_MANAGE_ANNOUNCE ); %>
<%= announceSector.getManageSectors( request )%>

<%@include file="../../AdminFooter.jsp" %>