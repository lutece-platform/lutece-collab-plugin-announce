<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="announce" scope="session" class="fr.paris.lutece.plugins.announce.web.AnnounceJspBean" />

<%
	announce.init( request, announce.RIGHT_MANAGE_ANNOUNCE );
    response.sendRedirect( announce.doSuspendAnnounce( request ) );
%>