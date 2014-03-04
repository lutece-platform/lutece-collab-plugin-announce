<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="announceApp" scope="request" class="fr.paris.lutece.plugins.announce.web.AnnounceApp" />

<%= announceApp.doRemoveAsynchronousUploadedFile( request ) %>