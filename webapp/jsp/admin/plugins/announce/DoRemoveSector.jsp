<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="announceSector" scope="session" class="fr.paris.lutece.plugins.announce.web.SectorJspBean" />

<%
    announceSector.init( request, announceSector.RIGHT_MANAGE_ANNOUNCE );
    response.sendRedirect( announceSector.doDeleteSector( request ) );
%>