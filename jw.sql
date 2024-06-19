/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80025
 Source Host           : localhost:3306
 Source Schema         : jw

 Target Server Type    : MySQL
 Target Server Version : 80025
 File Encoding         : 65001

 Date: 19/06/2024 10:52:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部门名称',
  `created_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `created_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `is_delete` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否删除：0未删除，1已删除（默认未删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of department
-- ----------------------------
INSERT INTO `department` VALUES (1, '研发部门', '1', '2024-06-05 12:12:33', 0);
INSERT INTO `department` VALUES (2, '测试部门', '2', '2024-06-05 12:12:48', 0);

-- ----------------------------
-- Table structure for jw_rule
-- ----------------------------
DROP TABLE IF EXISTS `jw_rule`;
CREATE TABLE `jw_rule`  (
  `rule_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `rule_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规则名称',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `sql_statement` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'SQL语句',
  `note` varchar(20000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `result_table` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '结果表名称',
  `create_by` int(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `status` tinyint(0) NULL DEFAULT NULL COMMENT '0私有 1公有',
  `is_on` tinyint(0) NULL DEFAULT NULL COMMENT '0未启用 1已启用',
  `type` tinyint(0) NULL DEFAULT NULL COMMENT '0人相关 1单位相关',
  PRIMARY KEY (`rule_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of jw_rule
-- ----------------------------
INSERT INTO `jw_rule` VALUES (1, '测试规则', '规则的备注', NULL, '规则的详细备注', NULL, 2, NULL, 1, NULL, NULL);
INSERT INTO `jw_rule` VALUES (8, '规则288', '规则的备注2888888', NULL, '规则的详细备注288888888', NULL, 2, '2024-06-14 16:11:50', 0, NULL, NULL);

-- ----------------------------
-- Table structure for jw_ruledetail
-- ----------------------------
DROP TABLE IF EXISTS `jw_ruledetail`;
CREATE TABLE `jw_ruledetail`  (
  `ruledetail_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `table_id` int(0) NULL DEFAULT NULL COMMENT '左表id',
  `field_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '左字段名称',
  `match_type` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规则类型',
  `pattern` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '运算符',
  `match_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '匹配常量',
  `matchtable_id` int(0) NULL DEFAULT NULL COMMENT '右表id',
  `matchfield_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '右字段名称',
  `note` varchar(20000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `rule_id` int(0) NULL DEFAULT NULL COMMENT '外键',
  PRIMARY KEY (`ruledetail_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 63 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of jw_ruledetail
-- ----------------------------
INSERT INTO `jw_ruledetail` VALUES (63, 1, 'id', '1', '>', '0', 2, '字段2', NULL, NULL, 1);

-- ----------------------------
-- Table structure for jw_table
-- ----------------------------
DROP TABLE IF EXISTS `jw_table`;
CREATE TABLE `jw_table`  (
  `table_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `table_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表名',
  `tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '详细描述',
  `forward_table` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '前置表ID,用逗号分隔',
  `type` tinyint(0) NULL DEFAULT NULL COMMENT '类型：0 人物；1 单位；2 事件',
  `mark` tinyint(0) NULL DEFAULT NULL COMMENT '标记：1 入口；1 非入口',
  PRIMARY KEY (`table_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of jw_table
-- ----------------------------
INSERT INTO `jw_table` VALUES (1, 'test', '测试表', '测试描述', '0', 0, 1);

-- ----------------------------
-- Table structure for test
-- ----------------------------
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test`  (
  `id.sff` int(0) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id.sff`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of test
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `user_account` bigint(0) NOT NULL COMMENT '账户',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `department_id` int(0) NULL DEFAULT NULL COMMENT '部门id',
  `position` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '职务',
  `role` tinyint(0) NOT NULL DEFAULT 0 COMMENT '角色：1管理员，0普通用户（默认创建为普通用户）',
  `portrait` tinyint(0) NOT NULL DEFAULT 0 COMMENT '精准画像系统：0无权限，1有权限（默认无权限）',
  `compare` tinyint(0) NOT NULL DEFAULT 0 COMMENT '文件比对系统：0无权限，1有权限（默认无权限）',
  `model` tinyint(0) NOT NULL DEFAULT 0 COMMENT '规则模型系统：0无权限，1有权限（默认无权限）',
  `created_user` int(0) NOT NULL COMMENT '创建者id',
  `created_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `isDelete` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否删除：0未删除',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电话',
  `status` tinyint(0) NULL DEFAULT NULL COMMENT '0 启用;1 停用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (2, 1002, '$2a$10$Y5zGDS/Hz7VZy3QJpOWGzOB9PDIW4ZlHChLnFhiI8ZPCaPkXI2CFK', 'test2', 1, '测试', 0, 0, 0, 0, 0, '2024-06-05 11:19:57', 0, '13333333333', NULL);
INSERT INTO `user` VALUES (4, 1001, '123123', 'test', 1, '12', 1, 1, 1, 1, 1, '2024-06-03 17:51:47', 0, NULL, 0);

-- ----------------------------
-- View structure for jw_field
-- ----------------------------
DROP VIEW IF EXISTS `jw_field`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `jw_field` AS select `information_schema`.`a`.`COLUMN_NAME` AS `field_name`,`information_schema`.`a`.`COLUMN_COMMENT` AS `description`,`information_schema`.`a`.`COLUMN_TYPE` AS `data_type`,`b`.`table_id` AS `table_id` from (`information_schema`.`COLUMNS` `a` join `jw_table` `b` on((`information_schema`.`a`.`TABLE_NAME` = `b`.`table_name`))) where (`information_schema`.`a`.`TABLE_SCHEMA` = 'jw');

SET FOREIGN_KEY_CHECKS = 1;
