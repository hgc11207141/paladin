/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50624
Source Host           : localhost:3306
Source Database       : health

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2018-04-19 11:09:38
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `db_connection`
-- ----------------------------
DROP TABLE IF EXISTS `db_connection`;
CREATE TABLE `db_connection` (
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `url` varchar(400) COLLATE utf8_unicode_ci NOT NULL,
  `type` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `user_name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `password` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `note` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user_id` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_user_id` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of db_connection
-- ----------------------------
INSERT INTO `db_connection` VALUES ('健康教育数据库', 'jdbc:mysql://127.0.0.1:3306/health?useUnicode=true&characterEncoding=UTF-8', 'MYSQL', 'root', null, null, '2018-04-12 13:41:16', 'admin', '2018-04-12 13:41:23', 'admin');
INSERT INTO `db_connection` VALUES ('医德医风数据库', 'jdbc:mysql://127.0.0.1:3306/hangfeng_base?useUnicode=true&characterEncoding=UTF-8', 'MYSQL', 'root', null, null, '2018-04-08 09:19:25', 'admin', '2018-04-08 09:19:32', 'admin');

-- ----------------------------
-- Table structure for `generate_demo`
-- ----------------------------
DROP TABLE IF EXISTS `generate_demo`;
CREATE TABLE `generate_demo` (
  `id` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '名称',
  `count` int(11) DEFAULT NULL COMMENT '数量',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user_id` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_user_id` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of generate_demo
-- ----------------------------

-- ----------------------------
-- Table structure for `index_item`
-- ----------------------------
DROP TABLE IF EXISTS `index_item`;
CREATE TABLE `index_item` (
  `id` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '名称',
  `item_type` varchar(10) COLLATE utf8_unicode_ci NOT NULL COMMENT 'CATEGORY（分类）/STANDARD（标准，规格，指标）',
  `parent_id` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '父项ID',
  `item_key` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'STANDARD的key',
  `is_delete` tinyint(4) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_key` (`item_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of index_item
-- ----------------------------
INSERT INTO `index_item` VALUES ('1', '一般状况', 'CATEGORY', null, null, '0');
INSERT INTO `index_item` VALUES ('11', '体温', 'STANDARD', '1', 'temperature', '0');
INSERT INTO `index_item` VALUES ('12', '脉搏', 'STANDARD', '1', 'pulse', '0');
INSERT INTO `index_item` VALUES ('2', '生活方式', 'CATEGORY', null, null, '0');
INSERT INTO `index_item` VALUES ('21', '体育锻炼', 'CATEGORY', '2', null, '0');
INSERT INTO `index_item` VALUES ('211', '锻炼频率', 'STANDARD', '21', 'exercise_frequency', '0');
INSERT INTO `index_item` VALUES ('212', '每次锻炼时间', 'STANDARD', '21', 'exercise_time_eachtime', '0');

-- ----------------------------
-- Table structure for `index_item_dependence`
-- ----------------------------
DROP TABLE IF EXISTS `index_item_dependence`;
CREATE TABLE `index_item_dependence` (
  `id` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `target_id` varchar(32) COLLATE utf8_unicode_ci NOT NULL COMMENT '目标项ID',
  `dependence_id` varchar(32) COLLATE utf8_unicode_ci NOT NULL COMMENT '依赖项ID',
  `dependence_relation` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT '依赖关系：KEY_EQUAL/STRING_EQUAL/NUMBER_EQUAL/NUMBER_GREAT/NUMBER_GREAT_EQUAL/NUMBER_LESS/NUMBER_LESS_EQUAL',
  `dependence_value` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '依赖值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of index_item_dependence
-- ----------------------------
INSERT INTO `index_item_dependence` VALUES ('', '212', '211', 'KEY_NOT_EQUAL', 'never');

-- ----------------------------
-- Table structure for `index_item_standard`
-- ----------------------------
DROP TABLE IF EXISTS `index_item_standard`;
CREATE TABLE `index_item_standard` (
  `id` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `value_definition_id` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `standard_key` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT 'key',
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of index_item_standard
-- ----------------------------
INSERT INTO `index_item_standard` VALUES ('2111', '211', 'every_day', '每天');
INSERT INTO `index_item_standard` VALUES ('2112', '211', 'more_once_weekly', '每周一次以上');
INSERT INTO `index_item_standard` VALUES ('2113', '211', 'occasionally', '偶尔');
INSERT INTO `index_item_standard` VALUES ('2114', '211', 'never', '从不');

-- ----------------------------
-- Table structure for `index_item_value_definition`
-- ----------------------------
DROP TABLE IF EXISTS `index_item_value_definition`;
CREATE TABLE `index_item_value_definition` (
  `id` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `item_id` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `input_type` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'SELECT/INPUT',
  `is_single` tinyint(4) DEFAULT '0' COMMENT 'SELECT下是否单选',
  `template` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'INPUT下模板',
  `unit` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `value_type` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'NUMBER/TEXT',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of index_item_value_definition
-- ----------------------------
INSERT INTO `index_item_value_definition` VALUES ('11', '11', 'INPUT', '0', null, '℃', 'NUMBER');
INSERT INTO `index_item_value_definition` VALUES ('12', '12', 'INPUT', '0', null, '次/分钟', 'NUMBER');
INSERT INTO `index_item_value_definition` VALUES ('211', '211', 'SELECT', '1', null, null, null);
INSERT INTO `index_item_value_definition` VALUES ('212', '212', 'INPUT', '0', null, '分钟', 'NUMBER');

-- ----------------------------
-- Table structure for `sys_user`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` varchar(32) COLLATE utf8_unicode_ci NOT NULL COMMENT 'id',
  `account` varchar(30) COLLATE utf8_unicode_ci NOT NULL COMMENT '账号',
  `password` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '密码',
  `salt` varchar(32) COLLATE utf8_unicode_ci NOT NULL COMMENT '盐',
  `user_id` varchar(32) COLLATE utf8_unicode_ci NOT NULL COMMENT '用户信息表ID',
  `state` tinyint(4) DEFAULT '1' COMMENT '是否启用，0不启用',
  `type` tinyint(4) DEFAULT '0' COMMENT '0:默认，1：管理员',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user_id` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_user_id` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  `is_delete` tinyint(4) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('ad313d38fe9447ce863fe8584743a010', 'admin', 'bd168e346bb59730335ead5464ff5eb2', '3ad6c1dd71e0ec26c62ae1b7c81b3e82', '', '1', '1', null, null, '2018-03-13 16:20:29', null, '0');

-- ----------------------------
-- Table structure for `sys_visit_cache`
-- ----------------------------
DROP TABLE IF EXISTS `sys_visit_cache`;
CREATE TABLE `sys_visit_cache` (
  `id` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `ip` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT 'IP',
  `code` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT 'IP',
  `value` varchar(400) COLLATE utf8_unicode_ci NOT NULL COMMENT '内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of sys_visit_cache
-- ----------------------------
INSERT INTO `sys_visit_cache` VALUES ('a4bf142ed7b74ab0a9b881fe262b294f', '0:0:0:0:0:0:0:1', 'data_project_path', 'D:\\workspace\\paladin');
