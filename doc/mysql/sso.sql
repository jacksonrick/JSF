DROP TABLE IF EXISTS `oauth_access_token`;
CREATE TABLE `oauth_access_token`
(
    `token_id`          varchar(256) DEFAULT NULL,
    `token`             blob,
    `authentication_id` varchar(250) NOT NULL,
    `user_name`         varchar(256) DEFAULT NULL,
    `client_id`         varchar(256) DEFAULT NULL,
    `authentication`    blob,
    `refresh_token`     varchar(256) DEFAULT NULL,
    PRIMARY KEY (`authentication_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC;


DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE `oauth_client_details`
(
    `client_id` varchar(250) NOT NULL,
    `client_desc` varchar(255) DEFAULT NULL,
    `resource_ids` varchar(256) DEFAULT NULL,
    `client_secret` varchar(256) DEFAULT NULL,
    `authorized_grant_types` varchar(256) DEFAULT NULL,
    `web_server_redirect_uri` varchar(256) DEFAULT NULL,
    `scope` varchar(256) DEFAULT NULL,
    `authorities` varchar(256) DEFAULT NULL,
    `access_token_validity` int(11) DEFAULT NULL,
    `refresh_token_validity` int(11) DEFAULT NULL,
    `additional_information` varchar(4096) DEFAULT NULL,
    `autoapprove` varchar(256) DEFAULT NULL,
    PRIMARY KEY (`client_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC;

BEGIN;
INSERT INTO `oauth_client_details` (`client_id`, `client_desc`, `resource_ids`, `client_secret`, `authorized_grant_types`, `web_server_redirect_uri`, `scope`, `authorities`, `access_token_validity`, `refresh_token_validity`, `additional_information`, `autoapprove`) VALUES ('client', 'desc', NULL, '$2a$10$2QaFSy4T84/06c2uREOqxeTSNRsA1z6YYsGM/NJl..ZbjrOP9lL02', 'client_credentials', NULL, 'all', 'ROLE_SYS,ROLE_DB', 1800, 86400, NULL, 'true');
INSERT INTO `oauth_client_details` (`client_id`, `client_desc`, `resource_ids`, `client_secret`, `authorized_grant_types`, `web_server_redirect_uri`, `scope`, `authorities`, `access_token_validity`, `refresh_token_validity`, `additional_information`, `autoapprove`) VALUES ('pwd', 'desc', NULL, '$2a$10$2QaFSy4T84/06c2uREOqxeTSNRsA1z6YYsGM/NJl..ZbjrOP9lL02', 'password,refresh_token', NULL, 'all', NULL, 3600, 86400, NULL, 'true');
INSERT INTO `oauth_client_details` (`client_id`, `client_desc`, `resource_ids`, `client_secret`, `authorized_grant_types`, `web_server_redirect_uri`, `scope`, `authorities`, `access_token_validity`, `refresh_token_validity`, `additional_information`, `autoapprove`) VALUES ('sso01', 'desc', NULL, '$2a$10$2QaFSy4T84/06c2uREOqxeTSNRsA1z6YYsGM/NJl..ZbjrOP9lL02', 'authorization_code,refresh_token', 'http://127.0.0.1:8091/sso/login', 'all', 'ROLE_SSO1', 1800, 86400, NULL, 'true');
INSERT INTO `oauth_client_details` (`client_id`, `client_desc`, `resource_ids`, `client_secret`, `authorized_grant_types`, `web_server_redirect_uri`, `scope`, `authorities`, `access_token_validity`, `refresh_token_validity`, `additional_information`, `autoapprove`) VALUES ('sso02', 'desc', NULL, '$2a$10$2QaFSy4T84/06c2uREOqxeTSNRsA1z6YYsGM/NJl..ZbjrOP9lL02', 'authorization_code,refresh_token', 'http://127.0.0.1:8092/sso/login', 'all', 'ROLE_SSO2', 1800, 86400, NULL, 'true');
INSERT INTO `oauth_client_details` (`client_id`, `client_desc`, `resource_ids`, `client_secret`, `authorized_grant_types`, `web_server_redirect_uri`, `scope`, `authorities`, `access_token_validity`, `refresh_token_validity`, `additional_information`, `autoapprove`) VALUES ('web', 'desc', NULL, NULL, 'implicit', 'https://www.baidu.com', 'all', NULL, NULL, NULL, NULL, 'true');
COMMIT;


DROP TABLE IF EXISTS `oauth_refresh_token`;
CREATE TABLE `oauth_refresh_token`
(
    `token_id`       varchar(256) DEFAULT NULL,
    `token`          blob,
    `authentication` blob
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC;


DROP TABLE IF EXISTS `oauth_client_token`;
CREATE TABLE `oauth_client_token`
(
    `token_id`          varchar(256) DEFAULT NULL,
    `token`             blob,
    `authentication_id` varchar(256) NOT NULL,
    `user_name`         varchar(256) DEFAULT NULL,
    `client_id`         varchar(256) DEFAULT NULL,
    PRIMARY KEY (`authentication_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


DROP TABLE IF EXISTS `oauth_code`;
CREATE TABLE `oauth_code`
(
    `code`           varchar(256) DEFAULT NULL,
    `authentication` blob
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


DROP TABLE IF EXISTS `oauth_approvals`;
CREATE TABLE `oauth_approvals`
(
    `userId`         varchar(256)   DEFAULT NULL,
    `clientId`       varchar(256)   DEFAULT NULL,
    `scope`          varchar(256)   DEFAULT NULL,
    `status`         varchar(10)    DEFAULT NULL,
    `expiresAt`      timestamp NULL DEFAULT NULL,
    `lastModifiedAt` timestamp NULL DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


DROP TABLE IF EXISTS `oauth_user`;
CREATE TABLE `oauth_user`
(
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `role` varchar(255) DEFAULT NULL,
    `name` varchar(255) DEFAULT NULL,
    `pwd` varchar(255) DEFAULT NULL,
    `origin` varchar(255) DEFAULT NULL,
    `disabled` bit(1) DEFAULT b'0',
    `locks` tinyint(2) DEFAULT '0',
    `updatedate` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `createdate` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8;

BEGIN;
INSERT INTO `oauth_user` (`id`, `role`, `name`, `pwd`, `origin`, `disabled`, `locks`, `updatedate`, `createdate`) VALUES (1, 'ROLE_ADMIN,ROLE_USER', 'admin', '$2a$10$2QaFSy4T84/06c2uREOqxeTSNRsA1z6YYsGM/NJl..ZbjrOP9lL02', 'sso01', b'0', 0, '2022-07-06 10:56:45', '2022-07-06 09:29:29');
INSERT INTO `oauth_user` (`id`, `role`, `name`, `pwd`, `origin`, `disabled`, `locks`, `updatedate`, `createdate`) VALUES (2, 'ROLE_USER', 'test', '$2a$10$2QaFSy4T84/06c2uREOqxeTSNRsA1z6YYsGM/NJl..ZbjrOP9lL02', 'sso01', b'0', 0, '2022-07-06 10:56:48', '2022-07-06 09:29:29');
COMMIT;


