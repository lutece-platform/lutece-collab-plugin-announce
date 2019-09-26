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
WHERE t.class_name IN( 'forms.entryTypeSelect', 'forms.entryTypeRadioButton', 'forms.entryTypeCheckBox')
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

UPDATE genatt_entry e SET e.is_only_display_back = '1'
WHERE e.id_entry IN (
SELECT f.id_entry FROM genatt_field f
WHERE f.CODE = 'only_display_in_back'
AND f.VALUE = '1');

DELETE FROM genatt_field WHERE CODE = 'only_display_in_back';

ALTER TABLE genatt_field MODIFY id_field INT AUTO_INCREMENT;
ALTER TABLE genatt_entry DROP COLUMN is_shown_in_completeness;