INSERT INTO `rule`(`id`, `name`, `project_name`, `channel`, `filter`, `group`, `recover`, `timewin`, `trigger`, `interval`, `type`, `persons`, `mobiles`, `emails`, `date_range`, `time_range`, `weekly`, `monthly`, `disabled`, `user_name`, `ip`, `template`, `create_time`, `update_time`) VALUES (6, 'test1', 'test', 'monitor', '1=1', NULL, NULL, 1, 'count()>10', 30, 1, 'wangchen78,meiqingguang', '13774307090,15306522460', 'wangchen78@wanda.cn,meiqingguang@wanda.cn', NULL, '0:24', '0,1,2,3,4,5,6', '', 0, 'root', '172.18.1.15', 'test规则 发生预警信息', '2017-06-21 02:58:39', '2017-06-22 18:00:30');