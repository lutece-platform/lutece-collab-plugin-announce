-- liquibase formatted sql
-- lutece runAfter:genericattributes
-- changeset announce:init_db_genericattributes_advert.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
('Bouton radio',0,0,0,'announce.entryTypeRadioButton','announce');
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
('Case à cocher',0,0,0,'announce.entryTypeCheckBox','announce');
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
('Commentaire',0,1,0,'announce.entryTypeComment','announce');
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
('Date',0,0,0,'announce.entryTypeDate','announce');
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
('Liste déroulante',0,0,0,'announce.entryTypeSelect','announce');
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
('Zone de texte court',0,0,0,'announce.entryTypeText','announce');
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
('Zone de texte long',0,0,0,'announce.entryTypeTextArea','announce');
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
('Numérotation',0,0,0,'announce.entryTypeNumbering','announce');
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
('Regroupement',1,0,0,'announce.entryTypeGroup','announce');
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
('Image',0,0,0,'announce.entryTypeImage','announce');
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
('Géolocalisation',0,0,0,'announce.entryTypeGeolocation','announce');
