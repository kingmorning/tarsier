create database tarsier;
use tarsier;

CREATE TABLE `collect` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `ip` varchar(15) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `config` varchar(5000) DEFAULT NULL,
  `disabled` int(11) NOT NULL DEFAULT '0',
  `create_time` timestamp(6) NULL DEFAULT NULL,
  `update_time` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `user_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_logstash_ip` (`ip`,`type`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `persons` (
  `name` varchar(200) NOT NULL,
  `mobile` varchar(20) NOT NULL,
  `email` varchar(200) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `project_name` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `channel` varchar(500) COLLATE utf8_unicode_ci NOT NULL,
  `filter` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `group` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `recover` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `timewin` int(11) NOT NULL,
  `trigger` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `interval` int(11) NOT NULL DEFAULT '30',
  `type` int(11) NOT NULL DEFAULT '7',
  `persons` varchar(2000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `mobiles` varchar(2000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `emails` varchar(2000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `date_range` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `time_range` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `weekly` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `monthly` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `disabled` int(11) NOT NULL DEFAULT '0',
  `user_name` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `ip` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `template` varchar(2000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;