-- 请勿照搬本人数据库设计，应按所在公司规范化设计

DROP DATABASE IF EXISTS `jsf`;

CREATE DATABASE `jsf` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `jsf`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for s_admin
-- ----------------------------
DROP TABLE IF EXISTS `s_admin`;
CREATE TABLE `s_admin` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `role_id` int(11) DEFAULT NULL COMMENT '用户组id',
  `login_name` varchar(32) NOT NULL COMMENT '用户名',
  `password` varchar(128) NOT NULL COMMENT '登录密码',
  `realname` varchar(16) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(16) DEFAULT NULL COMMENT '手机号',
  `rights` varchar(1024) DEFAULT NULL COMMENT '权限',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '账号创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '上次登录时间',
  `last_login_ip` varchar(64) DEFAULT NULL COMMENT '上次登录ip',
  `login_error_count` tinyint(4) DEFAULT '0' COMMENT '登录错误次数',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除 1-是 0-否',
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_admin_username` (`login_name`) USING BTREE
) ENGINE=InnoDB COMMENT='后台管理员';

-- ----------------------------
-- Records of s_admin
-- ----------------------------
BEGIN;
INSERT INTO `s_admin` VALUES (10000, 10000, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '超管', '', NULL, '2016-04-23 15:36:55', NULL, '2019-04-03 16:00:03', '127.0.0.1', 0, b'0');
COMMIT;

-- ----------------------------
-- Table structure for s_config
-- ----------------------------
DROP TABLE IF EXISTS `s_config`;
CREATE TABLE `s_config` (
  `id` tinyint(4) NOT NULL AUTO_INCREMENT,
  `grp` varchar(16) NOT NULL COMMENT '分组',
  `key` varchar(65) NOT NULL COMMENT '键',
  `val` varchar(255) NOT NULL COMMENT '值',
  `descr` varchar(255) DEFAULT NULL COMMENT '描述',
  `type` char(8) NOT NULL COMMENT '字段类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='配置表';

-- ----------------------------
-- Records of s_config
-- ----------------------------
BEGIN;
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (1, 'aliyun', 'accessId', 'accessId', '阿里云accessId', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (2, 'aliyun', 'accessSecret', 'accessSecret', '阿里云accessSecret', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (3, 'alipay', 'appid', '2016xxx', '支付宝-APPID', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (4, 'sys', 'appkey', 'appkey', 'APP访问校验码', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (5, 'sys', 'dev', 'true', '是否开发环境', 'boolean');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (6, 'alipay', 'gateway', 'https://openapi.alipaydev.com/gateway.do', '支付宝网关-生产环境/沙箱环境', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (7, 'alipay', 'notifyUrl', 'http://xxx/pay/callback/alipay', '支付宝-接收通知的接口', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (8, 'alipay', 'partner', '2088xxx', '支付宝-合作身份者ID，以2088开头由16位纯数字组成的字符串', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (9, 'alipay', 'publicKey', 'pubkey', '支付宝-公钥，使用支付宝提供工具生成后上传至开发者平台', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (10, 'alipay', 'returnUrl', 'http://xxx/pay/page/success', '支付宝-通知页面跳转', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (11, 'alipay', 'rsaPrivateKey', 'prikey', '支付宝-商户私钥,需要PKCS8格式,使用支付宝提供工具生成', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (12, 'alipay', 'seller', 'xxx@sandbox.com', '支付宝-账号', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (13, 'sys', 'uploadHost', '', '上传后拼接的服务器地址，可留空；如果配置了FastDFS，此项可以不用配置', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (14, 'sys', 'version', '6.1', '系统版本', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (15, 'wxpay', 'appid', 'wx47xxx', '微信-在开发平台登记的app应用', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (16, 'wxpay', 'partner', '1482xxx', '微信-商户号、MCH_ID', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (17, 'wxpay', 'partnerKey', 'partner_key', '微信-商户在微信平台设置的32位长度的api秘钥、API_KEY', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (18, 'wxpay', 'notifyUrl', 'http://xxx/pay/callback/wxpay', '微信-异步通知地址', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (19, 'wechat', 'mp_id', 'mp_id', '微信公众号id', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (20, 'wechat', 'mp_key', 'mp_key', '微信公众号key', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (21, 'geetest', 'id', 'geetid', 'geet验证-id', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (22, 'geetest', 'key', 'geetkey', 'geet验证-key', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (23, 'jpush', 'name', 'name', '极光推送', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (24, 'jpush', 'appkey', 'appkey', '极光推送', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (25, 'jpush', 'secret', 'secret', '极光推送', 'string');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (26, 'jpush', 'ios_product', 'false', 'IOS开发环境-false，生产环境-true', 'boolean');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (27, 'upload', 'fdfs', 'false', '如果为false,则上传到本地upload目录', 'boolean');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (28, 'upload', 'imgSize', '3', '图片大小，单位M', 'int');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (29, 'upload', 'imgType', 'jpg,jpeg,png,gif', '图片类型', 'list');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (30, 'upload', 'fileSize', '10', '文件大小【非图片】', 'int');
INSERT INTO `s_config` (`id`, `grp`, `key`, `val`, `descr`, `type`) VALUES (31, 'upload', 'fileType', 'zip,rar', '文件类型', 'list');
COMMIT;

-- ----------------------------
-- Table structure for s_log
-- ----------------------------
DROP TABLE IF EXISTS `s_log`;
CREATE TABLE `s_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `operator` varchar(64) NOT NULL COMMENT '操作人',
  `remark` varchar(128) NOT NULL COMMENT '备注',
  `ip` varchar(32) DEFAULT NULL COMMENT 'ip',
  `params` varchar(255) DEFAULT NULL COMMENT '参数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='日志表';

-- ----------------------------
-- Table structure for s_module
-- ----------------------------
DROP TABLE IF EXISTS `s_module`;
CREATE TABLE `s_module` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `parent_id` int(11) NOT NULL COMMENT '父模块id 0表示一级分类',
  `name` varchar(64) NOT NULL COMMENT '模块名',
  `action` varchar(128) DEFAULT NULL COMMENT '访问地址',
  `icon_name` varchar(128) DEFAULT NULL COMMENT '模块图标',
  `flag` tinyint(2) DEFAULT NULL COMMENT '1,2表示菜单 | 3表示功能',
  `sort` tinyint(4) DEFAULT NULL COMMENT '菜单排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `deleted` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `module_path` (`action`) USING BTREE
) ENGINE=InnoDB COMMENT='后台模块';

-- ----------------------------
-- Records of s_module
-- ----------------------------
BEGIN;
INSERT INTO `s_module` VALUES (1, 0, '用户管理', NULL, 'fa fa-user', 1, 1, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (2, 0, '系统管理', NULL, 'fa fa-gear', 1, 10, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (100, 1, '用户列表', '/admin/user/page', 'fa fa-group', 2, 1, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (101, 100, '用户数据', '/admin/user/list', NULL, 3, NULL, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (102, 100, '用户编辑', '/admin/user/edit', NULL, 3, NULL, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (103, 100, '用户启用/禁用', '/admin/user/enable', NULL, 3, NULL, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (104, 100, '用户详情', '/admin/user/detail', NULL, 3, NULL, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (105, 100, '导出Excel', '/admin/user/export', NULL, 3, NULL, '2018-07-17 17:39:43', b'0');

INSERT INTO `s_module` VALUES (106, 2, '管理员列表', '/admin/system/adminList', 'fa fa-user', 2, 1, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (107, 106, '管理员编辑', '/admin/system/adminEdit', NULL, 3, NULL, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (108, 106, '管理员冻结', '/admin/system/adminEnable', NULL, 3, NULL, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (109, 2, '权限管理', '/admin/system/rights', 'fa fa-delicious', 2, 2, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (110, 109, '编辑组', '/admin/system/roleEdit', NULL, 3, NULL, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (111, 109, '禁用/启用组', '/admin/system/roleEnable', NULL, 3, NULL, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (112, 109, '查看权限', '/admin/system/permits', NULL, 3, NULL, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (113, 109, '授权组', '/admin/system/permit', NULL, 3, NULL, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (114, 2, '操作日志', '/admin/system/logList', 'fa fa-keyboard-o', 2, 3, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (115, 114, '日志备份', '/admin/system/backupLog', NULL, 3, NULL, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (116, 2, '文件管理', '/admin/system/file', 'fa fa-archive', 2, 5, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (117, 116, '获取目录', '/admin/system/getDirectory', NULL, 3, NULL, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (118, 2, '系统工具', '/admin/system/tools', 'fa fa-crop', 2, 6, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (119, 118, 'SQL编辑', '/admin/system/executeUpdate', NULL, 3, NULL, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (120, 118, 'SQL查询', '/admin/system/executeQuery', NULL, 3, NULL, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (121, 2, '地址管理', '/admin/system/address', 'fa fa-map', 2, 8, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (122, 121, '编辑地址', '/admin/system/addrEdit', NULL, 3, NULL, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (123, 121, '删除地址', '/admin/system/addrDel', NULL, 3, NULL, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (124, 2, '模块管理', '/admin/system/module', 'fa fa-desktop', 2, 7, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (125, 124, '模块编辑', '/admin/system/moduleEdit', '', 3, NULL, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (126, 124, '模块删除', '/admin/system/moduleDel', '', 3, NULL, '2018-07-17 17:39:43', b'0');
INSERT INTO `s_module` VALUES (127, 2, '系统日志', '/admin/system/syslogList', 'fa fa-laptop', 2, 4, '2018-08-30 17:23:41', b'0');
INSERT INTO `s_module` VALUES (128, 2, '代码生成', '/admin/system/codeGen', 'fa fa-code', 2, 4, '2020-09-11 10:27:31', b'0');
INSERT INTO `s_module` VALUES (129, 2, '系统配置', '/admin/system/sysConfig', 'fa fa-cogs', 2, 5, '2020-09-11 10:29:16', b'0');
INSERT INTO `s_module` VALUES (130, 129, '修改配置', '/admin/system/sysConfigEdit', NULL, 3, NULL, '2020-09-11 10:31:45', b'0');
COMMIT;

-- ----------------------------
-- Table structure for s_msg
-- ----------------------------
DROP TABLE IF EXISTS `s_msg`;
CREATE TABLE `s_msg` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `admin_id` int(11) DEFAULT NULL COMMENT '管理ID',
  `content` text COMMENT '内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='系统消息';

-- ----------------------------
-- Records of s_msg
-- ----------------------------
BEGIN;
INSERT INTO `s_msg` VALUES (1, 10000, '系统消息', '2018-08-31 16:39:01');
COMMIT;

-- ----------------------------
-- Table structure for s_role
-- ----------------------------
DROP TABLE IF EXISTS `s_role`;
CREATE TABLE `s_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(16) NOT NULL COMMENT '用户组名称',
  `rights` varchar(1024) DEFAULT NULL COMMENT '权限',
  `flag` tinyint(2) NOT NULL DEFAULT '1' COMMENT '是否可编辑 1-可编辑 0-不可编辑',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除 1-是 0-否',
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_role_name` (`name`) USING BTREE
) ENGINE=InnoDB COMMENT='后台用户组';

-- ----------------------------
-- Records of s_role
-- ----------------------------
BEGIN;
INSERT INTO `s_role` VALUES (10000, '超级管理组', NULL, 0, '2018-07-17 17:38:59', NULL, b'0');
INSERT INTO `s_role` VALUES (10002, '运维组', '2,124,125', 1, '2018-07-17 17:38:59', NULL, b'0');
COMMIT;

-- ----------------------------
-- Table structure for s_token
-- ----------------------------
DROP TABLE IF EXISTS `s_token`;
CREATE TABLE `s_token` (
  `uid` varchar(64) NOT NULL,
  `token` varchar(32) NOT NULL,
  `created` datetime DEFAULT CURRENT_TIMESTAMP,
  `expired` datetime NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB COMMENT='Token';

-- ----------------------------
-- Table structure for t_sms
-- ----------------------------
DROP TABLE IF EXISTS `t_sms`;
CREATE TABLE `t_sms` (
  `id` bigint(20) NOT NULL,
  `phone` varchar(16) NOT NULL,
  `code` varchar(10) NOT NULL,
  `source` varchar(10) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL,
  `valided` bit(1) NOT NULL DEFAULT b'0',
  UNIQUE KEY `t_sms_id_uindex` (`id`)
) ENGINE=InnoDB COMMENT='短信记录';

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `nickname` varchar(32) DEFAULT NULL COMMENT '昵称',
  `phone` varchar(16) DEFAULT NULL COMMENT '手机号',
  `email` varchar(32) DEFAULT NULL COMMENT '邮箱',
  `password` varchar(128) DEFAULT NULL COMMENT '密码',
  `avatar` varchar(128) DEFAULT NULL COMMENT '头像',
  `money` decimal(15,2) DEFAULT '0.00',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最近登陆时间',
  `realname` varchar(16) DEFAULT NULL COMMENT '真实姓名',
  `idcard` varchar(32) DEFAULT NULL COMMENT '身份证号码',
  `gender` bit(1) DEFAULT b'1' COMMENT '性别 1-男 0-女',
  `address` varchar(64) DEFAULT NULL COMMENT '住址',
  `birthday` varchar(32) DEFAULT NULL COMMENT '出生日期',
  `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除 1-是 0-否(默认)',
  `wrong_pwd` int(11) NOT NULL DEFAULT '0' COMMENT '密码错误次数',
  `locked` bit(1) NOT NULL DEFAULT b'0' COMMENT 'APP登陆锁定',
  `extend` json DEFAULT NULL COMMENT '拓展字段',
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_user_phone` (`phone`) USING BTREE
) ENGINE=InnoDB COMMENT='用户';

SET FOREIGN_KEY_CHECKS = 1;