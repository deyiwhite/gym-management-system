SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `admin_account` int NOT NULL COMMENT '管理员账号',
  `admin_password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '管理员密码',
  PRIMARY KEY (`admin_account`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1001, '123456');
INSERT INTO `admin` VALUES (1002, '123456');
INSERT INTO `admin` VALUES (1003, '123456');

-- ----------------------------
-- Table structure for class_table
-- ----------------------------
DROP TABLE IF EXISTS `class_table`;
CREATE TABLE `class_table`  (
  `class_id` int NOT NULL COMMENT '课程id',
  `class_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '课程名称',
  `class_begin` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '开课时间',
  `class_duration` int NULL DEFAULT NULL COMMENT '课程时长(分钟)',
  `class_level` int NULL DEFAULT 1 COMMENT '课程等级 1入门 2进阶 3专业',
  `coach_id` int NULL DEFAULT NULL COMMENT '教练ID FK->employee',
  `max_capacity` int NULL DEFAULT NULL COMMENT '最大容量',
  `class_description` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '课程描述',
  `price` double NULL DEFAULT NULL COMMENT '价格',
  PRIMARY KEY (`class_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci;

-- ----------------------------
-- Records of class_table
-- ----------------------------
INSERT INTO `class_table` VALUES (1, '增肌', '2026年1月1日 15:00', 60, 1, 101038721, 20, '增肌训练课程', 200);
INSERT INTO `class_table` VALUES (2, '瑜伽', '2026年1月2日 10:20', 90, 2, 101068283, 15, '瑜伽课程', 150);
INSERT INTO `class_table` VALUES (3, '减脂', '2026年3月6日 18:00', 90, 2, 101053687, 25, '减脂训练课程', 180);
INSERT INTO `class_table` VALUES (4, '运动康复', '2026年2月2日 10:00', 45, 3, 101045354, 10, '运动康复课程', 300);
INSERT INTO `class_table` VALUES (5, '综合格斗', '2026年2月3日 15:00', 60, 2, 101038721, 20, '综合格斗训练', 220);
INSERT INTO `class_table` VALUES (6, '塑形', '2026年2月3日 15:00', 60, 1, 101068283, 18, '塑形训练课程', 160);
INSERT INTO `class_table` VALUES (7, '普拉提', '2026年3月1日 17:30', 60, 2, 101053687, 15, '普拉提课程', 200);
INSERT INTO `class_table` VALUES (8, '爵士舞', '2026年2月22日 09:00', 90, 1, 101045354, 25, '爵士舞课程', 130);
INSERT INTO `class_table` VALUES (9, '杠铃操', '2026年2月4日 15:00', 60, 2, 101038721, 20, '杠铃操课程', 170);
INSERT INTO `class_table` VALUES (10, '动感单车', '2026年3月8日 15:00', 45, 1, 101068283, 30, '动感单车课程', 120);
INSERT INTO `class_table` VALUES (11, '健美操', '2026年2月22日 18:00', 60, 1, 101053687, 22, '健美操课程', 140);


-- ----------------------------
-- Table structure for class_record (报名记录)
-- ----------------------------
DROP TABLE IF EXISTS `class_record`;
CREATE TABLE `class_record`  (
  `record_id` int NOT NULL AUTO_INCREMENT COMMENT '报名记录id',
  `member_id` int NOT NULL COMMENT '会员ID',
  `class_id` int NOT NULL COMMENT '课程ID',
  `join_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  `status` int NULL DEFAULT 0 COMMENT '0已报名 1已完成 2已取消',
  `total_sessions` int NULL DEFAULT NULL COMMENT '总课时',
  `completed_sessions` int NULL DEFAULT NULL COMMENT '已完成课时',
  `rating` double NULL DEFAULT NULL COMMENT '评分',
  PRIMARY KEY (`record_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8 COLLATE = utf8_general_ci;

-- ----------------------------
-- Records of class_record
-- ----------------------------
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202100788, 2, '2026-01-02 10:20:00', 0, 8.5);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202132539, 2, '2026-01-02 10:20:00', 0, 9.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202156754, 4, '2026-02-02 10:00:00', 0, 7.5);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202156754, 1, '2026-01-01 15:00:00', 0, 6.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202183406, 1, '2026-01-01 15:00:00', 0, 5.5);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202183406, 2, '2026-01-02 10:20:00', 0, 8.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202100788, 1, '2026-01-01 15:00:00', 0, 9.5);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202186416, 1, '2026-01-01 15:00:00', 0, 4.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202186416, 3, '2026-03-06 18:00:00', 0, NULL);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202123664, 3, '2026-03-06 18:00:00', 0, 7.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202153468, 3, '2026-03-06 18:00:00', 0, 8.5);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202100788, 5, '2026-02-03 15:00:00', 1, 8.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202132539, 5, '2026-02-03 15:00:00', 1, 7.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202106725, 6, '2026-02-03 15:00:00', 0, 6.5);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202176587, 7, '2026-03-01 17:30:00', 0, NULL);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202121345, 7, '2026-03-01 17:30:00', 0, 9.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202009867, 8, '2026-02-22 09:00:00', 1, 7.5);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202153468, 8, '2026-02-22 09:00:00', 0, 8.5);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202189776, 9, '2026-02-04 15:00:00', 0, 6.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202123664, 10, '2026-03-08 15:00:00', 0, 7.5);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202106725, 10, '2026-03-08 15:00:00', 0, 5.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202121345, 11, '2026-02-22 18:00:00', 0, 8.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202176587, 11, '2026-02-22 18:00:00', 0, NULL);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202156754, 3, '2026-03-06 18:00:00', 0, 9.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (202009867, 2, '2026-01-02 10:20:00', 1, 7.0);


-- ----------------------------
-- Table structure for class_equipment (课程关联器材)
-- ----------------------------
DROP TABLE IF EXISTS `class_equipment`;
CREATE TABLE `class_equipment`  (
  `class_id` int NOT NULL COMMENT '课程ID',
  `equipment_id` int NOT NULL COMMENT '器材ID',
  PRIMARY KEY (`class_id`, `equipment_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci;



-- ----------------------------
-- Table structure for member
-- ----------------------------
DROP TABLE IF EXISTS `member`;
CREATE TABLE `member`  (
  `member_id` int NOT NULL COMMENT '会员账号',
  `member_password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '123456' COMMENT '会员密码',
  `member_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员姓名',
  `gender` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '性别',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `phone` varchar(20) NULL DEFAULT NULL COMMENT '电话',
  `register_date` date NULL DEFAULT NULL COMMENT '办卡时间',
  `total_class_count` int NULL DEFAULT NULL COMMENT '购买课时',
  `remain_class_count` int NULL DEFAULT NULL COMMENT '剩余课时',
  PRIMARY KEY (`member_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci;

-- ----------------------------
-- Records of member
-- ----------------------------
INSERT INTO `member` VALUES (202009867, '123456', '张三', '女', 24, '13515548482', '2025-06-05', 40, 40);
INSERT INTO `member` VALUES (202100788, '123456', '李四', '男', 31, '13131554873', '2026-01-01', 50, 50);
INSERT INTO `member` VALUES (202132539, '123456', '王五', '男', 31, '13154875489', '2026-01-01', 40, 40);
INSERT INTO `member` VALUES (202186416, '123456', '马六', '女', 23, '13124576857', '2026-01-16', 30, 30);
INSERT INTO `member` VALUES (202106725, '123456', 'Tom', '男', 24, '13758784959', '2026-02-26', 30, 30);
INSERT INTO `member` VALUES (202183406, '123456', 'Tylor', '女', 19, '13786457488', '2026-02-27', 30, 30);
INSERT INTO `member` VALUES (202176587, '123456', 'Jack', '男', 33, '13767546666', '2026-02-27', 30, 30);
INSERT INTO `member` VALUES (202156754, '123456', 'Mike', '男', 36, '13786532448', '2026-02-28', 30, 30);
INSERT INTO `member` VALUES (202153468, '123456', 'Emma', '女', 25, '13786457124', '2026-03-01', 50, 50);
INSERT INTO `member` VALUES (202121345, '123456', 'Ava', '女', 28, '13754457488', '2026-03-02', 30, 30);
INSERT INTO `member` VALUES (202189776, '123456', 'Chloe', '女', 27, '13986337489', '2026-03-23', 30, 30);
INSERT INTO `member` VALUES (202123664, '123456', 'Lily', '女', 25, '15986457423', '2026-03-27', 30, 30);

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee`  (
  `employee_id` int NOT NULL COMMENT '员工账号',
  `employee_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '员工姓名',
  `gender` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `entry_date` date NULL DEFAULT NULL COMMENT '入职时间',
  `position` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '职务',
  PRIMARY KEY (`employee_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci;

-- ----------------------------
-- Records of employee
-- ----------------------------
INSERT INTO `employee` VALUES (101038721, '教练1', '女', 26, '2026-06-29', '健身教练');
INSERT INTO `employee` VALUES (101068283, '教练2', '男', 34, '2025-01-06', '健身教练');
INSERT INTO `employee` VALUES (101053687, '教练3', '男', 30, '2025-06-06', '健身教练');
INSERT INTO `employee` VALUES (101045354, '教练4', '男', 24, '2026-01-07', '健身教练');
INSERT INTO `employee` VALUES (101058973, '保洁1', '女', 48, '2024-08-24', '保洁员');
INSERT INTO `employee` VALUES (101034208, '保洁2', '女', 48, '2025-08-01', '保洁员');

-- ----------------------------
-- Table structure for equipment
-- ----------------------------
DROP TABLE IF EXISTS `equipment`;
CREATE TABLE `equipment`  (
  `equipment_id` int NOT NULL AUTO_INCREMENT COMMENT '器材id',
  `equipment_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '器材名称',
  `equipment_location` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '器材位置',
  `equipment_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '器材状态',
  `equipment_message` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '器材备注信息',
  PRIMARY KEY (`equipment_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of equipment
-- ----------------------------
INSERT INTO `equipment` VALUES (1, '哑铃1', '1号房间', '正常', '');
INSERT INTO `equipment` VALUES (2, '杠铃1', '2号房间', '损坏', '待维修');
INSERT INTO `equipment` VALUES (3, '跑步机1', '2号房间', '维修中', '联系厂家维修');
INSERT INTO `equipment` VALUES (4, '跑步机2', '2号房间', '正常', '');
INSERT INTO `equipment` VALUES (5, '跑步机3', '2号房间', '正常', '');
INSERT INTO `equipment` VALUES (6, '杠铃1', '1号房间', '正常', '');
INSERT INTO `equipment` VALUES (7, '杠铃2', '1号房间', '正常', '');
-- ----------------------------
-- Indexes for query and analysis performance
-- ----------------------------
ALTER TABLE `class_record` ADD INDEX `idx_cr_member_id` (`member_id`);
ALTER TABLE `class_record` ADD INDEX `idx_cr_class_id` (`class_id`);
ALTER TABLE `class_record` ADD INDEX `idx_cr_rating` (`rating`);
ALTER TABLE `class_record` ADD INDEX `idx_cr_class_rating` (`class_id`, `rating`);
ALTER TABLE `class_table` ADD INDEX `idx_class_coach_id` (`coach_id`);
ALTER TABLE `class_equipment` ADD INDEX `idx_ce_equipment_id` (`equipment_id`);
ALTER TABLE `member` ADD INDEX `idx_member_name` (`member_name`);
ALTER TABLE `employee` ADD INDEX `idx_employee_position` (`position`);
ALTER TABLE `equipment` ADD INDEX `idx_equipment_name` (`equipment_name`);

-- ----------------------------
-- Foreign key constraints for table relationships
-- ----------------------------
ALTER TABLE `class_record`
  ADD CONSTRAINT `fk_class_record_member`
  FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
  ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `class_record`
  ADD CONSTRAINT `fk_class_record_class`
  FOREIGN KEY (`class_id`) REFERENCES `class_table` (`class_id`)
  ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `class_table`
  ADD CONSTRAINT `fk_class_table_coach`
  FOREIGN KEY (`coach_id`) REFERENCES `employee` (`employee_id`)
  ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `class_equipment`
  ADD CONSTRAINT `fk_class_equipment_class`
  FOREIGN KEY (`class_id`) REFERENCES `class_table` (`class_id`)
  ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `class_equipment`
  ADD CONSTRAINT `fk_class_equipment_equipment`
  FOREIGN KEY (`equipment_id`) REFERENCES `equipment` (`equipment_id`)
  ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- View for experiment 4: member course rating
-- ----------------------------
CREATE OR REPLACE VIEW `v_member_course_rating` AS
SELECT m.member_id,
       m.member_name,
       m.gender,
       ct.class_id,
       ct.class_name,
       cr.record_id,
       cr.join_time,
       cr.status,
       cr.rating
FROM `member` m
JOIN `class_record` cr ON m.member_id = cr.member_id
JOIN `class_table` ct ON cr.class_id = ct.class_id
WHERE cr.rating IS NOT NULL;

SET FOREIGN_KEY_CHECKS = 1;
