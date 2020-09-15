-- driver-class-name: org.postgresql.Driver
-- url: jdbc:postgresql://ip:5432/jsf?currentSchema=public
-- 请勿照搬本人数据库设计，应按所在公司规范化设计

-- ----------------------------
-- Table structure for s_admin
-- ----------------------------
DROP TABLE IF EXISTS "public"."s_admin";
CREATE TABLE "public"."s_admin" (
  "id" int4 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
  "role_id" int4 NOT NULL,
  "login_name" varchar(32) COLLATE "pg_catalog"."default",
  "realname" varchar(16) COLLATE "pg_catalog"."default",
  "password" varchar(32) COLLATE "pg_catalog"."default",
  "phone" varchar(16) COLLATE "pg_catalog"."default",
  "rights" varchar(1000) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) DEFAULT now(),
  "update_time" timestamp(6) DEFAULT now(),
  "last_login_time" timestamp(6),
  "last_login_ip" varchar(64) COLLATE "pg_catalog"."default",
  "deleted" bool DEFAULT false,
  PRIMARY KEY("id")
);
ALTER TABLE "public"."s_admin" OWNER TO "postgres";
COMMENT ON COLUMN "public"."s_admin"."id" IS 'id';
COMMENT ON COLUMN "public"."s_admin"."role_id" IS '用户组id';
COMMENT ON COLUMN "public"."s_admin"."login_name" IS '用户名';
COMMENT ON COLUMN "public"."s_admin"."realname" IS '真实姓名';
COMMENT ON COLUMN "public"."s_admin"."password" IS '登录密码';
COMMENT ON COLUMN "public"."s_admin"."phone" IS '手机号';
COMMENT ON COLUMN "public"."s_admin"."rights" IS '权限';
COMMENT ON COLUMN "public"."s_admin"."create_time" IS '账号创建日期';
COMMENT ON COLUMN "public"."s_admin"."last_login_time" IS '上次登录时间';
COMMENT ON COLUMN "public"."s_admin"."last_login_ip" IS '上次登录ip';
COMMENT ON COLUMN "public"."s_admin"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."s_admin" IS '后台管理员';

-- ----------------------------
-- Records of s_admin
-- ----------------------------
BEGIN;
INSERT INTO "public"."s_admin" VALUES (10000, 10000, 'admin', '超管', 'e10adc3949ba59abbe56e057f20f883e', '17730215422', NULL, NOW(), NULL, NULL, 'f');
COMMIT;

-- ----------------------------
-- Table structure for s_config
-- ----------------------------

CREATE TABLE "public"."s_config" (
  "id" int4 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
  "grp" varchar(16) COLLATE "pg_catalog"."default" NOT NULL,
  "key" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "val" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "descr" varchar(255) COLLATE "pg_catalog"."default",
  "type" char(8) COLLATE "pg_catalog"."default" NOT NULL,
  PRIMARY KEY ("id")
);

ALTER TABLE "public"."s_config" OWNER TO "postgres";
COMMENT ON COLUMN "public"."s_config"."key" IS '键';
COMMENT ON COLUMN "public"."s_config"."val" IS '值';
COMMENT ON COLUMN "public"."s_config"."descr" IS '描述';
COMMENT ON COLUMN "public"."s_config"."grp" IS '分组';
COMMENT ON COLUMN "public"."s_config"."type" IS '字段类型';
COMMENT ON TABLE "public"."s_config" IS '配置表';

-- ----------------------------
-- Records of s_config
-- ----------------------------

BEGIN;

COMMIT;

-- ----------------------------
-- Table structure for s_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."s_log";
CREATE TABLE "public"."s_log" (
  "id" int4 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
  "operator" varchar(64) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "ip" varchar(32) COLLATE "pg_catalog"."default",
  "params" varchar(255) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) DEFAULT now(),
  PRIMARY KEY("id")
);
ALTER TABLE "public"."s_log" OWNER TO "postgres";
COMMENT ON COLUMN "public"."s_log"."operator" IS '操作人';
COMMENT ON COLUMN "public"."s_log"."remark" IS '备注';
COMMENT ON COLUMN "public"."s_log"."ip" IS 'ip';
COMMENT ON COLUMN "public"."s_log"."params" IS '参数';
COMMENT ON COLUMN "public"."s_log"."create_time" IS '创建时间';
COMMENT ON TABLE "public"."s_log" IS '日志表';

-- ----------------------------
-- Table structure for s_module
-- ----------------------------
DROP TABLE IF EXISTS "public"."s_module";
CREATE TABLE "public"."s_module" (
  "id" int4 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
  "parent_id" int4 NOT NULL,
  "name" varchar(64) COLLATE "pg_catalog"."default",
  "action" varchar(128) COLLATE "pg_catalog"."default",
  "icon_name" varchar(128) COLLATE "pg_catalog"."default",
  "flag" int2,
  "sort" int2,
  "create_time" timestamp(6) DEFAULT now(),
  "deleted" bool DEFAULT false,
  PRIMARY KEY("id")
);
ALTER TABLE "public"."s_module" OWNER TO "postgres";
COMMENT ON COLUMN "public"."s_module"."id" IS 'id';
COMMENT ON COLUMN "public"."s_module"."parent_id" IS '父模块id 0表示一级分类';
COMMENT ON COLUMN "public"."s_module"."name" IS '模块名';
COMMENT ON COLUMN "public"."s_module"."action" IS '访问action';
COMMENT ON COLUMN "public"."s_module"."icon_name" IS '模块图标';
COMMENT ON COLUMN "public"."s_module"."flag" IS '1,2表示层级 | 3表示功能';
COMMENT ON COLUMN "public"."s_module"."sort" IS '排序';
COMMENT ON COLUMN "public"."s_module"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."s_module"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."s_module" IS '后台模块';

-- ----------------------------
-- Records of s_module
-- ----------------------------
BEGIN;
INSERT INTO "public"."s_module" VALUES (1, 0, '用户管理', NULL, 'fa fa-user', 1, 1, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (2, 0, '系统管理', NULL, 'fa fa-gear', 1, 10, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (100, 1, '用户列表', '/admin/user/page', 'fa fa-group', 2, 1, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (101, 100, '用户数据', '/admin/user/list', NULL, 3, NULL, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (102, 100, '用户编辑', '/admin/user/edit', NULL, 3, NULL, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (103, 100, '用户启用/禁用', '/admin/user/enable', NULL, 3, NULL, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (104, 100, '用户详情', '/admin/user/detail', NULL, 3, NULL, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (105, 100, '导出Excel', '/admin/user/export', NULL, 3, NULL, '2018-07-17 17:39:43', 'f');

INSERT INTO "public"."s_module" VALUES (106, 2, '管理员列表', '/admin/system/adminList', 'fa fa-user', 2, 1, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (107, 106, '管理员编辑', '/admin/system/adminEdit', NULL, 3, NULL, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (108, 106, '管理员冻结', '/admin/system/adminEnable', NULL, 3, NULL, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (109, 2, '权限管理', '/admin/system/rights', 'fa fa-delicious', 2, 2, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (110, 109, '编辑组', '/admin/system/roleEdit', NULL, 3, NULL, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (111, 109, '禁用/启用组', '/admin/system/roleEnable', NULL, 3, NULL, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (112, 109, '查看权限', '/admin/system/permits', NULL, 3, NULL, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (113, 109, '授权组', '/admin/system/permit', NULL, 3, NULL, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (114, 2, '操作日志', '/admin/system/logList', 'fa fa-keyboard-o', 2, 3, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (115, 114, '日志备份', '/admin/system/backupLog', NULL, 3, NULL, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (116, 2, '文件管理', '/admin/system/file', 'fa fa-archive', 2, 5, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (117, 116, '获取目录', '/admin/system/getDirectory', NULL, 3, NULL, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (118, 2, '系统工具', '/admin/system/tools', 'fa fa-crop', 2, 6, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (119, 118, 'SQL编辑', '/admin/system/executeUpdate', NULL, 3, NULL, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (120, 118, 'SQL查询', '/admin/system/executeQuery', NULL, 3, NULL, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (121, 2, '地址管理', '/admin/system/address', 'fa fa-map', 2, 8, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (122, 121, '编辑地址', '/admin/system/addrEdit', NULL, 3, NULL, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (123, 121, '删除地址', '/admin/system/addrDel', NULL, 3, NULL, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (124, 2, '模块管理', '/admin/system/module', 'fa fa-desktop', 2, 7, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (125, 124, '模块编辑', '/admin/system/moduleEdit', '', 3, NULL, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (126, 124, '模块删除', '/admin/system/moduleDel', '', 3, NULL, '2018-07-17 17:39:43', 'f');
INSERT INTO "public"."s_module" VALUES (127, 2, '系统日志', '/admin/system/syslogList', 'fa fa-laptop', 2, 4, '2018-08-30 17:23:41', 'f');
INSERT INTO "public"."s_module" VALUES (128, 2, '代码生成', '/admin/system/codeGen', 'fa fa-code', 2, 4, '2020-09-11 10:27:31', 'f');
INSERT INTO "public"."s_module" VALUES (129, 2, '系统配置', '/admin/system/sysConfig', 'fa fa-cogs', 2, 5, '2020-09-11 10:29:16', 'f');
INSERT INTO "public"."s_module" VALUES (130, 129, '修改配置', '/admin/system/sysConfigEdit', NULL, 3, NULL, '2020-09-11 10:31:45', 'f');
COMMIT;

-- ----------------------------
-- Table structure for s_msg
-- ----------------------------
DROP TABLE IF EXISTS "public"."s_msg";
CREATE TABLE "public"."s_msg" (
  "id" int4 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
  "admin_id" int4 NOT NULL,
  "content" text COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) DEFAULT now(),
  PRIMARY KEY("id")
);
ALTER TABLE "public"."s_msg" OWNER TO "postgres";
COMMENT ON COLUMN "public"."s_msg"."admin_id" IS '管理ID';
COMMENT ON COLUMN "public"."s_msg"."content" IS '内容';
COMMENT ON TABLE "public"."s_msg" IS '系统消息';

-- ----------------------------
-- Table structure for s_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."s_role";
CREATE TABLE "public"."s_role" (
  "id" int4 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
  "name" varchar(16) COLLATE "pg_catalog"."default",
  "flag" int2 DEFAULT 1,
  "rights" varchar(1000) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) DEFAULT now(),
  "deleted" bool DEFAULT false,
  PRIMARY KEY("id")
);
ALTER TABLE "public"."s_role" OWNER TO "postgres";
COMMENT ON COLUMN "public"."s_role"."id" IS 'id';
COMMENT ON COLUMN "public"."s_role"."name" IS '用户组名称';
COMMENT ON COLUMN "public"."s_role"."flag" IS '是否可编辑 1-可编辑 0-不可编辑';
COMMENT ON COLUMN "public"."s_role"."rights" IS '权限';
COMMENT ON COLUMN "public"."s_role"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."s_role"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."s_role" IS '后台用户组';

-- ----------------------------
-- Records of s_role
-- ----------------------------
BEGIN;
INSERT INTO "public"."s_role" VALUES (10000, '超级管理组', 0, NULL, '2018-07-17 17:55:11.928909', 'f');
INSERT INTO "public"."s_role" VALUES (10001, '运维组', 1, '1,106,108,107', '2018-07-17 17:55:11.928909', 'f');
COMMIT;

-- ----------------------------
-- Table structure for s_token
-- ----------------------------

CREATE TABLE "public"."s_token" (
  "uid" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "token" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "created" timestamp(6) DEFAULT now(),
  "expired" timestamp(6) NOT NULL,
  PRIMARY KEY ("uid")
);

-- ----------------------------
-- Table structure for t_sms
-- ----------------------------

CREATE TABLE "public"."t_sms" (
  "id" int8 NOT NULL,
  "phone" varchar(16) COLLATE "pg_catalog"."default" NOT NULL,
  "code" varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
  "source" varchar(10) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "create_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "update_time" timestamp(6),
  "valided" bool NOT NULL DEFAULT false,
  PRIMARY KEY ("id")
);

-- ----------------------------
-- Indexes structure for table s_admin
-- ----------------------------
CREATE INDEX "s_admin_name" ON "public"."s_admin" USING btree (
  "login_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

---------------------
-- Indexes structure for table s_module
-- ----------------------------
CREATE INDEX "s_module_action" ON "public"."s_module" USING btree (
  "action" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
