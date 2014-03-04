<jsp:useBean id="categoryEntry" scope="session" class="fr.paris.lutece.plugins.announce.web.CategoryEntryJspBean" />
<% String strContent = categoryEntry.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
