<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="announceCategory" scope="session" class="fr.paris.lutece.plugins.announce.web.CategoryJspBean" />

<% announceCategory.init( request , announceCategory.RIGHT_MANAGE_ANNOUNCE ); %>
<%= announceCategory.getDuplicateCategory( request )%>

<%@include file="../../AdminFooter.jsp" %>