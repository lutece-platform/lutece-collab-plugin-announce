<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="announceCategory" scope="session" class="fr.paris.lutece.plugins.announce.web.CategoryJspBean" />

<%
    announceCategory.init( request, announceCategory.RIGHT_MANAGE_ANNOUNCE );
    response.sendRedirect( announceCategory.doCreateCategory( request ) );
%>