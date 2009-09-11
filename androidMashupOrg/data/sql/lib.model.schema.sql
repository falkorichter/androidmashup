
# This is a fix for InnoDB in MySQL >= 4.1.x
# It "suspends judgement" for fkey relationships until are tables are set.
SET FOREIGN_KEY_CHECKS = 0;

#-----------------------------------------------------------------------------
#-- application
#-----------------------------------------------------------------------------

DROP TABLE IF EXISTS `application`;


CREATE TABLE `application`
(
	`id_application` INTEGER(11)  NOT NULL AUTO_INCREMENT,
	`package` VARCHAR(45)  NOT NULL,
	`name` VARCHAR(45)  NOT NULL,
	`url` VARCHAR(45),
	`icon` VARCHAR(45),
	`apk_url` VARCHAR(45),
	`developer_id` INTEGER(11)  NOT NULL,
	PRIMARY KEY (`id_application`),
	KEY `fk_application_developer`(`developer_id`),
	CONSTRAINT `application_FK_1`
		FOREIGN KEY (`developer_id`)
		REFERENCES `developer` (`id_developer`)
		ON UPDATE RESTRICT
		ON DELETE RESTRICT
)Type=InnoDB;

#-----------------------------------------------------------------------------
#-- application_has_intent
#-----------------------------------------------------------------------------

DROP TABLE IF EXISTS `application_has_intent`;


CREATE TABLE `application_has_intent`
(
	`id_application_has_intent` INTEGER(11)  NOT NULL AUTO_INCREMENT,
	`application_id` INTEGER(11)  NOT NULL,
	`intent_id` INTEGER(11)  NOT NULL,
	PRIMARY KEY (`id_application_has_intent`),
	KEY `fk_application_has_intent_application`(`application_id`),
	KEY `fk_application_has_intent_intent`(`intent_id`),
	CONSTRAINT `application_has_intent_FK_1`
		FOREIGN KEY (`application_id`)
		REFERENCES `application` (`id_application`)
		ON UPDATE RESTRICT
		ON DELETE RESTRICT,
	CONSTRAINT `application_has_intent_FK_2`
		FOREIGN KEY (`intent_id`)
		REFERENCES `intent` (`id_intent`)
		ON UPDATE RESTRICT
		ON DELETE RESTRICT
)Type=InnoDB;

#-----------------------------------------------------------------------------
#-- developer
#-----------------------------------------------------------------------------

DROP TABLE IF EXISTS `developer`;


CREATE TABLE `developer`
(
	`id_developer` INTEGER(11)  NOT NULL AUTO_INCREMENT,
	`username` VARCHAR(45)  NOT NULL,
	`url` VARCHAR(45),
	`email` VARCHAR(45)  NOT NULL,
	PRIMARY KEY (`id_developer`)
)Type=InnoDB;

#-----------------------------------------------------------------------------
#-- extra
#-----------------------------------------------------------------------------

DROP TABLE IF EXISTS `extra`;


CREATE TABLE `extra`
(
	`id_extra` INTEGER(11)  NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(45),
	`description` VARCHAR(45),
	`extraType_id` INTEGER(11)  NOT NULL,
	PRIMARY KEY (`id_extra`),
	KEY `fk_extra_extraType`(`extraType_id`),
	CONSTRAINT `extra_FK_1`
		FOREIGN KEY (`extraType_id`)
		REFERENCES `extraType` (`id_extraType`)
		ON UPDATE CASCADE
		ON DELETE CASCADE
)Type=InnoDB;

#-----------------------------------------------------------------------------
#-- extraType
#-----------------------------------------------------------------------------

DROP TABLE IF EXISTS `extraType`;


CREATE TABLE `extraType`
(
	`id_extraType` INTEGER(11)  NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(45),
	`description` VARCHAR(45),
	PRIMARY KEY (`id_extraType`)
)Type=InnoDB;

#-----------------------------------------------------------------------------
#-- intent
#-----------------------------------------------------------------------------

DROP TABLE IF EXISTS `intent`;


CREATE TABLE `intent`
(
	`id_intent` INTEGER(11)  NOT NULL AUTO_INCREMENT,
	`title` VARCHAR(45)  NOT NULL,
	`description` TEXT,
	`action` VARCHAR(45)  NOT NULL,
	`icon` VARCHAR(256)  NOT NULL,
	`mashup` TINYINT(1) default 0 NOT NULL,
	PRIMARY KEY (`id_intent`)
)Type=InnoDB;

#-----------------------------------------------------------------------------
#-- intentCategory
#-----------------------------------------------------------------------------

DROP TABLE IF EXISTS `intentCategory`;


CREATE TABLE `intentCategory`
(
	`id_intentCategory` INTEGER(11)  NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(45),
	`description` VARCHAR(45),
	PRIMARY KEY (`id_intentCategory`)
)Type=InnoDB;

#-----------------------------------------------------------------------------
#-- intent_belongsTo_intentCategory
#-----------------------------------------------------------------------------

DROP TABLE IF EXISTS `intent_belongsTo_intentCategory`;


CREATE TABLE `intent_belongsTo_intentCategory`
(
	`id_intent_belongsTo_intentCategory` INTEGER(11)  NOT NULL AUTO_INCREMENT,
	`intent_id` INTEGER(11),
	`intentCategory_id` INTEGER(11),
	PRIMARY KEY (`id_intent_belongsTo_intentCategory`),
	KEY `fk_intent_has_intentCategory_intent`(`intent_id`),
	KEY `fk_intent_has_intentCategory_intentCategory`(`intentCategory_id`),
	CONSTRAINT `intent_belongsTo_intentCategory_FK_1`
		FOREIGN KEY (`intent_id`)
		REFERENCES `intent` (`id_intent`)
		ON UPDATE RESTRICT
		ON DELETE RESTRICT,
	CONSTRAINT `intent_belongsTo_intentCategory_FK_2`
		FOREIGN KEY (`intentCategory_id`)
		REFERENCES `intentCategory` (`id_intentCategory`)
		ON UPDATE RESTRICT
		ON DELETE RESTRICT
)Type=InnoDB;

#-----------------------------------------------------------------------------
#-- intent_handles_extra
#-----------------------------------------------------------------------------

DROP TABLE IF EXISTS `intent_handles_extra`;


CREATE TABLE `intent_handles_extra`
(
	`id_intent_handles_extra` INTEGER(11)  NOT NULL AUTO_INCREMENT,
	`intent_id` INTEGER(11),
	`extra_id` INTEGER(11),
	`mandatory` VARCHAR(45),
	PRIMARY KEY (`id_intent_handles_extra`),
	KEY `fk_intent_has_extra_intent`(`intent_id`),
	KEY `fk_intent_has_extra_extra`(`extra_id`),
	CONSTRAINT `intent_handles_extra_FK_1`
		FOREIGN KEY (`intent_id`)
		REFERENCES `intent` (`id_intent`)
		ON UPDATE RESTRICT
		ON DELETE RESTRICT,
	CONSTRAINT `intent_handles_extra_FK_2`
		FOREIGN KEY (`extra_id`)
		REFERENCES `extra` (`id_extra`)
		ON UPDATE RESTRICT
		ON DELETE RESTRICT
)Type=InnoDB;

# This restores the fkey checks, after having unset them earlier
SET FOREIGN_KEY_CHECKS = 1;
