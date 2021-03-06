DROP TABLE IF EXISTS announce_field;
DROP TABLE IF EXISTS announce_sector;
CREATE TABLE announce_sector(
id_sector int DEFAULT '0' NOT NULL,
label_sector varchar(50) NOT NULL,
description_sector varchar(255) NOT NULL ,
announces_validation smallint default '0',
sector_order int DEFAULT '0' NOT NULL,
tags varchar(255) NOT NULL,
PRIMARY KEY (id_sector)
);

--
-- Structure de la table announce_category
--
DROP TABLE IF EXISTS announce_category;
CREATE TABLE announce_category(
id_category int default '0' NOT NULL,
id_sector int default '0' NOT NULL,
label_category varchar(50) NOT NULL,
display_price smallint default '0',
announces_validation smallint default '0',
id_mailing_list int default NULL,
PRIMARY KEY (id_category)
);

UPDATE core_admin_role_resource SET resource_type = 'SECTOR' WHERE resource_type = 'FIELD';

DROP TABLE IF EXISTS announce_announce_response;
CREATE TABLE announce_announce_response (
	id_announce INT NOT NULL,
	id_response INT NOT NULL,
	is_image SMALLINT DEFAULT 0,
	PRIMARY KEY (id_announce,id_response)
);

INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(151,'Bouton radio',0,0,0,'announce.entryTypeRadioButton','announce');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(152,'Case à cocher',0,0,0,'announce.entryTypeCheckBox','announce');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(153,'Commentaire',0,1,0,'announce.entryTypeComment','announce');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(154,'Date',0,0,0,'announce.entryTypeDate','announce');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(155,'Liste déroulante',0,0,0,'announce.entryTypeSelect','announce');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(156,'Zone de texte court',0,0,0,'announce.entryTypeText','announce');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(157,'Zone de texte long',0,0,0,'announce.entryTypeTextArea','announce');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
(158,'Numérotation',0,0,0,'announce.entryTypeNumbering','announce');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(159,'Regroupement',1,0,0,'announce.entryTypeGroup','announce');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
(160,'Image',0,0,0,'announce.entryTypeImage','announce');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
(161,'Géolocalisation',0,0,0,'announce.entryTypeGeolocation','announce');

DROP TABLE IF EXISTS announce_search_filters;
CREATE TABLE announce_search_filters(
	id_filter int NOT NULL,
	id_category int NOT NULL,
	keywords long varchar NOT NULL ,
	date_min DATE null,
	date_max DATE null,
	price_min int not null default 0,
	price_max int not null default 0,
	PRIMARY KEY (id_filter)
);

ALTER TABLE announce_announce ADD COLUMN publication_time BIGINT default 0;
ALTER TABLE announce_announce ADD COLUMN user_email varchar(255) NOT NULL;
ALTER TABLE announce_announce ADD COLUMN date_modification timestamp NOT NULL;

UPDATE announce_announce SET date_modification = date_creation;

ALTER TABLE announce_category ADD COLUMN id_workflow INT default 0;
ALTER TABLE announce_category ADD COLUMN display_captcha smallint default '0';
ALTER TABLE announce_category ADD COLUMN price_mandatory smallint default '0';

CREATE INDEX announce_user_name ON announce_announce (user_name);

INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission) VALUES (1042,'announce_manager','announce','*','*');
INSERT INTO core_portlet_type VALUES ('ANNOUNCE_MYDASHBOARD_PORTLET','announce.portlet.MyAnnouncesPortlet.name','plugins/announce/portlet/CreatePortletMyAnnounces.jsp','plugins/announce/portlet/ModifyPortletMyAnnounces.jsp','fr.paris.lutece.plugins.announce.business.portlet.MyAnnouncesPortletHome','announce','plugins/announce/portlet/DoCreatePortletMyAnnounces.jsp','/admin/portlet/script_create_portlet.html','/admin/plugins/announce/portlet/create_portletmyannounces.html','','plugins/announce/portlet/DoModifyPortletMyAnnounces.jsp','/admin/portlet/script_modify_portlet.html','/admin/plugins/announce/portlet/modify_portletmyannounces.html','');
INSERT INTO core_portlet_type VALUES ('ANNOUNCE_LASTANNOUNCES_PORTLET','announce.portlet.LastAnnouncesPortlet.name','plugins/announce/portlet/CreatePortletLastAnnounces.jsp','plugins/announce/portlet/ModifyPortletLastAnnounces.jsp','fr.paris.lutece.plugins.announce.business.portlet.LastAnnouncesPortletHome','announce','plugins/announce/portlet/DoCreatePortletLastAnnounces.jsp','/admin/portlet/script_create_portlet.html','/admin/plugins/announce/portlet/create_portletlastannounces.html','','plugins/announce/portlet/DoModifyPortletLastAnnounces.jsp','/admin/portlet/script_modify_portlet.html','/admin/plugins/announce/portlet/modify_portletlastannounces.html','');
INSERT INTO core_datastore VALUES ( 'core.cache.status.announce.announceCacheService.enabled', '1' );
INSERT INTO core_datastore VALUES ( 'core.cache.status.announce.announceCacheService.maxElementsInMemory', '500' );
