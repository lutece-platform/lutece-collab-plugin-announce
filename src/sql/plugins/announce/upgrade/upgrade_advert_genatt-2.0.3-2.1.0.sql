-- liquibase formatted sql
-- lutece runAfter:genericattributes
-- LUT-33261 : formerly sql/plugins/genericattributes/upgrade/update_db_generic_attributes_forms_1.3.2-1.3.3.sql (copied from forms) ; first released in plugin-announce 2.1.0
-- changeset announce:update_db_generic_attributes_forms_1.3.2-1.3.3.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE genatt_field f SET f.CODE = 'default_date_value'
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'announce.entryTypeDate'
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = f.title
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'announce.entryTypeGeolocation'
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.title = null
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'announce.entryTypeGeolocation'
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.VALUE = f.title
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'announce.entryTypeNumbering'
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = 'prefix'
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'announce.entryTypeNumbering'
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.title = null
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'announce.entryTypeNumbering'
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = f.title
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'announce.entryTypeImage'
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = 'file_config'
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'announce.entryTypeImage'
AND f.code is null
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.title = null
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'announce.entryTypeImage'
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = 'answer_choice'
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name IN( 'announce.entryTypeSelect', 'announce.entryTypeRadioButton', 'announce.entryTypeCheckBox')
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = 'text_config'
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name IN( 'announce.entryTypeText')
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = 'text_config'
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name IN( 'announce.entryTypeTextArea')
AND e.id_entry = f.id_entry);
