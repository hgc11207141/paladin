/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50624
Source Host           : localhost:3306
Source Database       : health

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2018-03-28 16:55:05
*/

SET FOREIGN_KEY_CHECKS=0;

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
