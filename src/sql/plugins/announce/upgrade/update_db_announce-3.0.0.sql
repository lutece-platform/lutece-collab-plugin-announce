-- liquibase formatted sql
-- changeset announce:update_db_announce-3.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN

ALTER TABLE announce_category
  ADD CONSTRAINT fk_category_sector
  FOREIGN KEY (id_sector) REFERENCES announce_sector(id_sector) ON DELETE RESTRICT;

ALTER TABLE announce_announce
  ADD CONSTRAINT fk_announce_category
  FOREIGN KEY (id_category) REFERENCES announce_category(id_category) ON DELETE RESTRICT;

ALTER TABLE announce_notify
  ADD CONSTRAINT fk_notify_announce
  FOREIGN KEY (id_announce) REFERENCES announce_announce(id_announce) ON DELETE CASCADE;
