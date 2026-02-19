-- liquibase formatted sql
-- changeset announce:upgrade_announce_autoincrement.sql
-- preconditions onFail:MARK_RAN onError:WARN

ALTER TABLE announce_sector MODIFY id_sector int AUTO_INCREMENT NOT NULL;
ALTER TABLE announce_category MODIFY id_category int AUTO_INCREMENT NOT NULL;
ALTER TABLE announce_announce MODIFY id_announce int AUTO_INCREMENT NOT NULL;
ALTER TABLE announce_indexer_action MODIFY id_action int AUTO_INCREMENT NOT NULL;
ALTER TABLE announce_search_filters MODIFY id_filter int AUTO_INCREMENT NOT NULL;
ALTER TABLE announce_notify MODIFY id int AUTO_INCREMENT NOT NULL;
