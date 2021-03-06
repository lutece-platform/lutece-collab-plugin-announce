

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


DROP TABLE IF EXISTS announce_category;
CREATE TABLE announce_category(
	id_category int default '0' NOT NULL,
	id_sector int default '0' NOT NULL,
	label_category varchar(50) NOT NULL,
	display_price smallint default '0',
	price_mandatory smallint default '0',
	announces_validation smallint default '0',
	id_mailing_list int default NULL,
	id_workflow INT default 0,
	display_captcha smallint default '0',
	PRIMARY KEY (id_category)
);


DROP TABLE IF EXISTS announce_announce;
CREATE TABLE announce_announce(
	id_announce int default '0' NOT NULL,
	user_name varchar(255) NOT NULL,
	user_lastname varchar(255) default '' NOT NULL,
	user_secondname varchar(255) default '' NOT NULL,
	contact_information varchar(255) NOT NULL,
	id_category int default '0' NOT NULL,
	title_announce varchar(255) NOT NULL ,
	description_announce LONG VARCHAR NOT NULL ,
	price_announce DOUBLE,
	date_creation timestamp default CURRENT_TIMESTAMP NOT NULL,
	date_modification timestamp default CURRENT_TIMESTAMP NOT NULL,
	published smallint default '0',
	suspended smallint default '0',
	suspended_by_user smallint default '0',
	tags varchar(255) NOT NULL,
	has_pictures smallint default '0',
	publication_time BIGINT default 0,
	has_notified int ,
	PRIMARY KEY (id_announce)
);

CREATE INDEX announce_user_name ON announce_announce (user_name);

DROP TABLE IF EXISTS announce_indexer_action;
CREATE TABLE announce_indexer_action (
  id_action INT DEFAULT 0 NOT NULL,
  id_announce INT DEFAULT 0 NOT NULL,
  id_task INT DEFAULT 0 NOT NULL ,
  PRIMARY KEY (id_action)
  );
CREATE INDEX announce_id_indexer_task ON announce_indexer_action (id_task);


DROP TABLE IF EXISTS announce_announce_response;
CREATE TABLE announce_announce_response (
	id_announce INT NOT NULL,
	id_response INT NOT NULL,
	is_image SMALLINT DEFAULT '0',
	PRIMARY KEY (id_announce,id_response)
);
CREATE INDEX announce_response_file ON announce_announce_response (id_response);

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

DROP TABLE IF EXISTS announce_portlet_last_announces;
CREATE TABLE announce_portlet_last_announces(
	id_portlet int NOT NULL,
	nb_announces int NOT NULL,
	PRIMARY KEY (id_portlet)
);

DROP TABLE IF EXISTS announce_notify;
CREATE TABLE announce_notify(
id int default '0' NOT NULL,
id_announce int default '0' NOT NULL,
PRIMARY KEY (id)
);

