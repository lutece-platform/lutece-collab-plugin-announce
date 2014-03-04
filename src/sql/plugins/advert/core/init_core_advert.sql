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

--
-- Dumping data for table core_user_role
--
INSERT INTO core_user_role (role_key,id_user) VALUES ('announce_manager',1);
INSERT INTO core_user_role (role_key,id_user) VALUES ('announce_manager',2);