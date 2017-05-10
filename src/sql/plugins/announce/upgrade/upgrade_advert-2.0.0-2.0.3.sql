ALTER TABLE announce_announce ADD COLUMN has_notified INT NULL AFTER publication_time;
CREATE INDEX announce_response_file ON announce_announce_response (id_response);
