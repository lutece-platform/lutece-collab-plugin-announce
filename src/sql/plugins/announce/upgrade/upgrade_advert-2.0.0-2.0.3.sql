ALTER TABLE announce_announce ADD COLUMN has_notified INT NULL AFTER publication_time;
CREATE INDEX announce_response_file ON announce_announce_response (id_response);

DROP TABLE IF EXISTS announce_notify;
CREATE TABLE announce_notify(
id int default '0' NOT NULL,
id_announce int default '0' NOT NULL,
PRIMARY KEY (id)
);
