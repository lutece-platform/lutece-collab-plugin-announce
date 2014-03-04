<jsp:useBean id="categoryFields" scope="session" class="fr.paris.lutece.plugins.announce.web.CategoryFieldJspBean" />
<% String strContent = categoryFields.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
