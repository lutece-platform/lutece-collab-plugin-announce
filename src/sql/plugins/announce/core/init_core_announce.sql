/*==============================================================*/
/*	Init  table core_admin_right								*/
/*==============================================================*/
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url) VALUES ('ANNOUNCE_MANAGEMENT','announce.adminFeature.title',3,'jsp/admin/plugins/announce/ManageAnnounces.jsp','announce.adminFeature.description',0,'announce','CONTENT','images/admin/skin/plugins/announce/announce.png');

/*==============================================================*/
/*	Init  table core_user_right								*/
/*==============================================================*/
INSERT INTO core_user_right (id_right,id_user) VALUES ('ANNOUNCE_MANAGEMENT',1);
INSERT INTO core_user_right (id_right,id_user) VALUES('ANNOUNCE_MANAGEMENT',2);



/*	Init  table  core_admin_role								*/
/*==============================================================*/
INSERT INTO core_admin_role (role_key,role_description) VALUES ('announce_manager','Announce management');

/*==============================================================*/
/*	Init  table  core_admin_role_resource						*/
/*==============================================================*/
INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission) VALUES (1040,'announce_manager','SECTOR','*','*');
INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission) VALUES (1041,'announce_manager','CATEGORY','*','*');
INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission) VALUES (1042,'announce_manager','announce','*','*');

--
-- Dumping data for table core_user_role
--
INSERT INTO core_user_role (role_key,id_user) VALUES ('announce_manager',1);
INSERT INTO core_user_role (role_key,id_user) VALUES ('announce_manager',2);

INSERT INTO core_portlet_type VALUES ('ANNOUNCE_MYDASHBOARD_PORTLET','announce.portlet.MyAnnouncesPortlet.name','plugins/announce/portlet/CreatePortletMyAnnounces.jsp','plugins/announce/portlet/ModifyPortletMyAnnounces.jsp','fr.paris.lutece.plugins.announce.business.portlet.MyAnnouncesPortletHome','announce','plugins/announce/portlet/DoCreatePortletMyAnnounces.jsp','/admin/portlet/script_create_portlet.html','/admin/plugins/announce/portlet/create_portletmyannounces.html','','plugins/announce/portlet/DoModifyPortletMyAnnounces.jsp','/admin/portlet/script_modify_portlet.html','/admin/plugins/announce/portlet/modify_portletmyannounces.html','');
INSERT INTO core_portlet_type VALUES ('ANNOUNCE_LASTANNOUNCES_PORTLET','announce.portlet.LastAnnouncesPortlet.name','plugins/announce/portlet/CreatePortletLastAnnounces.jsp','plugins/announce/portlet/ModifyPortletLastAnnounces.jsp','fr.paris.lutece.plugins.announce.business.portlet.LastAnnouncesPortletHome','announce','plugins/announce/portlet/DoCreatePortletLastAnnounces.jsp','/admin/portlet/script_create_portlet.html','/admin/plugins/announce/portlet/create_portletlastannounces.html','','plugins/announce/portlet/DoModifyPortletLastAnnounces.jsp','/admin/portlet/script_modify_portlet.html','/admin/plugins/announce/portlet/modify_portletlastannounces.html','');

INSERT INTO core_datastore VALUES ( 'core.cache.status.announce.announceCacheService.enabled', '1' );
INSERT INTO core_datastore VALUES ( 'core.cache.status.announce.announceCacheService.maxElementsInMemory', '500' );
