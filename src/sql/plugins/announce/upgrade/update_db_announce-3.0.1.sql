-- liquibase formatted sql
-- changeset announce:update_db_announce-3.0.1.sql
-- preconditions onFail:MARK_RAN onError:WARN

ALTER TABLE announce_announce DROP COLUMN user_lastname;
ALTER TABLE announce_announce DROP COLUMN user_secondname;
