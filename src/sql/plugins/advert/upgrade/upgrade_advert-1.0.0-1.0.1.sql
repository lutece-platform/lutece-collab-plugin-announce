ALTER TABLE announce_entry_img 
ADD
(
lowdef_width int(11) NOT NULL,
lowdef_height int(11) NOT NULL,
lowdef_quality VARCHAR(5),
thumb_width int(11) NOT NULL,
thumb_height int(11) NOT NULL,
thumb_quality VARCHAR(5)
);
ALTER TABLE `announce_entry_value` ADD `value_lowdef` MEDIUMBLOB NULL AFTER `value` ;
ALTER TABLE `announce_entry_value` ADD `value_thumb` MEDIUMBLOB NULL AFTER `value_lowdef` ;

UPDATE announce_entry_value SET value_lowdef = value, value_thumb = value;

UPDATE announce_entry_img SET lowdef_width = 800, lowdef_height = 800, lowdef_quality = 0.9, thumb_width = 80, thumb_height = 80, thumb_quality = 0.9;