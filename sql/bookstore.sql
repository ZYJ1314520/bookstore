/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : localhost:3306
 Source Schema         : bookstore

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 03/06/2026 11:18:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收件人姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '省',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '区',
  `detail_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `is_default` tinyint NULL DEFAULT 0 COMMENT '是否默认: 0否 1是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '收货地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of address
-- ----------------------------
INSERT INTO `address` VALUES (1, 1, '张三', '13800138001', '广东省', '深圳市', '南山区', '科技园路1号', 1, '2026-06-01 16:09:05', '2026-06-01 16:09:05');
INSERT INTO `address` VALUES (2, 2, '李四', '13800138002', '北京市', '北京市', '海淀区', '中关村大街1号', 1, '2026-06-01 16:09:05', '2026-06-01 16:09:05');

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码(BCrypt加密)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', '$2a$10$VYBEv4sfaT6c/uxi.w.VsuzllUc/1c/LSu24nQWz.Xwi3Fa.HE96S', '2026-06-01 16:09:05');

-- ----------------------------
-- Table structure for book
-- ----------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `shop_id` bigint NOT NULL COMMENT '商家ID',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '书名',
  `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作者',
  `isbn` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ISBN',
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片',
  `price` decimal(10, 2) NOT NULL COMMENT '售价',
  `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '原价',
  `stock` int NULL DEFAULT 0 COMMENT '库存',
  `sales` int NULL DEFAULT 0 COMMENT '销量',
  `publisher` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '出版社',
  `publish_date` date NULL DEFAULT NULL COMMENT '出版日期',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '书籍简介',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详情描述(富文本)',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0下架 1上架',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_shop_id`(`shop_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_title`(`title` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 135 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '图书表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book
-- ----------------------------
INSERT INTO `book` VALUES (1, 1, 1, '活着', '余华', '9787506365437', NULL, 29.00, 45.00, 99, 2346, '作家出版社', NULL, '地主少爷福贵嗜赌成性，终于赌光了家业一贫如洗。', NULL, 1, '2026-06-01 16:09:05', '2026-06-01 16:09:05', 0);
INSERT INTO `book` VALUES (2, 1, 1, '三体', '刘慈欣', '9787536692930', '/uploads/fe63ff3b4d2f4ca1b0a484f51d6e9622.png', 35.00, 59.00, 80, 1876, '重庆出版社', NULL, '文化大革命如火如荼进行的同时，军方探寻外星文明的绝秘计划\"红岸工程\"取得了突破性进展。', '', 1, '2026-06-01 16:09:05', '2026-06-01 16:09:05', 0);
INSERT INTO `book` VALUES (3, 1, 2, '高等数学(第七版)上册', '同济大学数学系', '9787040396638', NULL, 32.00, 39.80, 150, 5678, '高等教育出版社', NULL, '本书是同济大学数学系编的《高等数学》第七版上册。', NULL, 1, '2026-06-01 16:09:05', '2026-06-01 16:09:05', 0);
INSERT INTO `book` VALUES (4, 1, 3, 'JavaScript高级程序设计(第4版)', 'Matt Frisbie', '9787115545381', NULL, 89.00, 129.00, 60, 987, '人民邮电出版社', NULL, '本书是JavaScript超级畅销书的最新版。', NULL, 1, '2026-06-01 16:09:05', '2026-06-01 16:09:05', 0);
INSERT INTO `book` VALUES (5, 1, 4, '经济学原理(第7版)', '曼昆', '9787301256510', NULL, 75.00, 98.00, 45, 654, '北京大学出版社', NULL, '本书是世界上最流行的经济学教材！', NULL, 1, '2026-06-01 16:09:05', '2026-06-01 16:09:05', 0);
INSERT INTO `book` VALUES (6, 1, 1, '百年孤独', '加西亚·马尔克斯', '9787544253994', NULL, 39.50, 55.00, 60, 1543, '南海出版公司', NULL, '马孔多是何等的魔幻与孤独。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (7, 1, 1, '围城', '钱钟书', '9787020024759', NULL, 28.00, 39.00, 90, 1234, '人民文学出版社', NULL, '围在城里的人想逃出来，城外的人想冲进去。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (8, 1, 1, '平凡的世界', '路遥', '9787530212004', NULL, 68.00, 98.00, 70, 2345, '北京十月文艺出版社', NULL, '全景式地表现了中国当代城乡社会生活。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (9, 1, 1, '白鹿原', '陈忠实', '9787544254366', NULL, 39.50, 56.00, 65, 1543, '南海出版公司', NULL, '一部渭河平原的雄奇史诗。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (10, 1, 1, '尘埃落定', '阿来', '9787544221498', NULL, 28.00, 38.00, 80, 876, '南海出版公司', NULL, '一个藏族土司家族的兴衰史。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (11, 1, 1, '额尔古纳河右岸', '迟子建', '9787506365438', NULL, 29.00, 42.00, 75, 654, '作家出版社', NULL, '一位年届九旬的鄂温克族最后一位酋长女人的自述。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (12, 1, 1, '长恨歌', '王安忆', '9787532120857', NULL, 28.00, 38.00, 60, 543, '上海文艺出版社', NULL, '一个女人四十年的情与爱，一座城市的变迁。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (13, 1, 1, '秦腔', '贾平凹', '9787506365439', NULL, 36.00, 48.00, 55, 432, '作家出版社', NULL, '一部用中国最古老歌谣吟唱的当代史诗。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (14, 1, 1, '废都', '贾平凹', '9787506365440', NULL, 32.00, 45.00, 70, 654, '作家出版社', NULL, '一部关于知识分子的精神危机的小说。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (15, 1, 1, '黄金时代', '王小波', '9787506365441', NULL, 25.00, 35.00, 85, 876, '作家出版社', NULL, '知青王二与陈清扬的爱情故事。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (16, 1, 1, '沉默的大多数', '王小波', '9787506365442', NULL, 28.00, 38.00, 90, 987, '作家出版社', NULL, '对社会道德伦理问题的思考。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (17, 1, 1, '万历十五年', '黄仁宇', '9787506365443', NULL, 26.00, 36.00, 75, 1234, '作家出版社', NULL, '一个看似平淡的年份，却影响了中国历史的走向。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (18, 1, 1, '明朝那些事儿', '当年明月', '9787506365444', NULL, 29.00, 42.00, 100, 3456, '作家出版社', NULL, '一部好看又好玩的明朝历史。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (19, 1, 1, '论语', '孔子', '9787506365447', NULL, 18.00, 28.00, 120, 4567, '作家出版社', NULL, '儒家学派的经典著作。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (20, 1, 1, '道德经', '老子', '9787506365448', NULL, 15.00, 22.00, 150, 5678, '作家出版社', NULL, '道家哲学的奠基之作。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (21, 1, 1, '孙子兵法', '孙武', '9787506365449', NULL, 16.00, 25.00, 130, 3456, '作家出版社', NULL, '中国古典军事文化遗产中的璀璨瑰宝。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (22, 1, 1, '诗经', '佚名', '9787506365450', NULL, 20.00, 30.00, 100, 2345, '作家出版社', NULL, '中国第一部诗歌总集。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (23, 1, 1, '楚辞', '屈原', '9787506365451', NULL, 22.00, 32.00, 90, 1876, '作家出版社', NULL, '中国文学史上第一部浪漫主义诗歌总集。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (24, 1, 1, '唐诗三百首', '蘅塘退士', '9787506365452', NULL, 18.00, 26.00, 140, 4567, '作家出版社', NULL, '唐诗选本中流传最广的一种。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (25, 1, 1, '宋词三百首', '朱孝臧', '9787506365453', NULL, 19.00, 28.00, 120, 3456, '作家出版社', NULL, '宋词选本中的经典之作。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (26, 1, 1, '古文观止', '吴楚材', '9787506365455', NULL, 24.00, 35.00, 100, 1876, '作家出版社', NULL, '中国古代散文的精华选本。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (27, 1, 1, '资治通鉴', '司马光', '9787506365456', NULL, 68.00, 98.00, 50, 1234, '作家出版社', NULL, '中国第一部编年体通史。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (28, 1, 1, '史记', '司马迁', '9787506365457', NULL, 75.00, 108.00, 45, 1543, '作家出版社', NULL, '中国第一部纪传体通史。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (29, 1, 1, '三国演义', '罗贯中', '9787506365458', NULL, 35.00, 52.00, 80, 3456, '作家出版社', NULL, '中国第一部长篇章回体历史演义小说。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (30, 1, 1, '水浒传', '施耐庵', '9787506365459', NULL, 32.00, 48.00, 75, 2876, '作家出版社', NULL, '中国历史上第一部用白话文写成的章回小说。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (31, 2, 2, '高等数学(第七版)上册', '同济大学数学系', '9787040396638', NULL, 32.00, 39.80, 150, 5678, '高等教育出版社', NULL, '本书是同济大学数学系编的高等数学第七版上册。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (32, 2, 2, '线性代数', '同济大学数学系', '9787040396607', NULL, 22.00, 28.00, 120, 3456, '高等教育出版社', NULL, '本书是同济大学数学系编的线性代数第六版。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (33, 2, 2, '概率论与数理统计', '浙江大学', '9787040396645', NULL, 28.00, 35.00, 100, 2345, '高等教育出版社', NULL, '本书是浙江大学编的概率论与数理统计第四版。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (34, 2, 2, '新概念英语(1)', 'L.G.Alexander', '9787544612346', NULL, 29.00, 38.00, 200, 5678, '上海外语教育出版社', NULL, '经典英语学习教材。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (35, 2, 2, '新概念英语(2)', 'L.G.Alexander', '9787544612347', NULL, 32.00, 42.00, 180, 4567, '上海外语教育出版社', NULL, '经典英语学习教材进阶篇。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (36, 2, 2, '新概念英语(3)', 'L.G.Alexander', '9787544612348', NULL, 35.00, 45.00, 150, 3456, '上海外语教育出版社', NULL, '经典英语学习教材提高篇。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (37, 2, 2, '新概念英语(4)', 'L.G.Alexander', '9787544612349', NULL, 38.00, 48.00, 120, 2345, '上海外语教育出版社', NULL, '经典英语学习教材高级篇。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (38, 2, 2, '考研英语词汇', '朱泰祺', '9787544612350', NULL, 42.00, 56.00, 100, 1876, '上海外语教育出版社', NULL, '考研英语词汇速记手册。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (39, 2, 2, '考研政治大纲解析', '教育部考试中心', '9787544612351', NULL, 48.00, 62.00, 90, 1543, '高等教育出版社', NULL, '考研政治官方大纲解析。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (40, 2, 2, '考研数学复习指南', '陈文灯', '9787544612352', NULL, 55.00, 72.00, 80, 1234, '高等教育出版社', NULL, '考研数学经典复习指南。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (41, 2, 2, '公务员考试行政能力测验', '华图教育', '9787544612353', NULL, 45.00, 62.00, 110, 2345, '高等教育出版社', NULL, '公务员考试必备教材。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (42, 2, 2, '公务员考试申论', '华图教育', '9787544612354', NULL, 42.00, 58.00, 100, 1876, '高等教育出版社', NULL, '公务员考试申论必备教材。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (43, 2, 2, '司法考试教材', '众合教育', '9787544612355', NULL, 68.00, 88.00, 60, 876, '高等教育出版社', NULL, '司法考试官方教材。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (44, 2, 2, '注册会计师考试教材', '中国注册会计师协会', '9787544612356', NULL, 58.00, 75.00, 70, 654, '高等教育出版社', NULL, '注册会计师考试官方教材。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (45, 2, 2, '教师资格证考试教材', '中公教育', '9787544612357', NULL, 38.00, 52.00, 120, 3456, '高等教育出版社', NULL, '教师资格证考试必备教材。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (46, 2, 2, '计算机二级考试教材', '教育部考试中心', '9787544612358', NULL, 35.00, 48.00, 130, 2876, '高等教育出版社', NULL, '计算机二级考试官方教材。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (47, 2, 2, '英语四级词汇', '新东方', '9787544612359', NULL, 28.00, 38.00, 150, 4567, '上海外语教育出版社', NULL, '英语四级核心词汇速记。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (48, 2, 2, '英语六级词汇', '新东方', '9787544612360', NULL, 30.00, 40.00, 140, 3456, '上海外语教育出版社', NULL, '英语六级核心词汇速记。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (49, 2, 2, '雅思词汇', '新东方', '9787544612361', NULL, 35.00, 48.00, 100, 2345, '上海外语教育出版社', NULL, '雅思考试核心词汇速记。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (50, 2, 2, '托福词汇', '新东方', '9787544612362', NULL, 38.00, 52.00, 90, 1876, '上海外语教育出版社', NULL, '托福考试核心词汇速记。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (51, 2, 2, 'GRE词汇', '新东方', '9787544612363', NULL, 42.00, 58.00, 80, 1543, '上海外语教育出版社', NULL, 'GRE考试核心词汇速记。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (52, 2, 2, '日语能力考试N1词汇', '新东方', '9787544612364', NULL, 32.00, 45.00, 110, 2345, '上海外语教育出版社', NULL, '日语能力考试N1核心词汇。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (53, 2, 2, '日语能力考试N2词汇', '新东方', '9787544612365', NULL, 30.00, 42.00, 120, 1876, '上海外语教育出版社', NULL, '日语能力考试N2核心词汇。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (54, 2, 2, '法语入门', '马晓宏', '9787544612366', NULL, 28.00, 38.00, 100, 1234, '上海外语教育出版社', NULL, '法语零基础入门教材。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (55, 2, 2, '德语入门', '赵登荣', '9787544612367', NULL, 30.00, 42.00, 90, 876, '上海外语教育出版社', NULL, '德语零基础入门教材。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (56, 2, 2, '西班牙语入门', '董燕生', '9787544612368', NULL, 32.00, 45.00, 80, 654, '上海外语教育出版社', NULL, '西班牙语零基础入门教材。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (57, 2, 2, '韩语入门', '李先汉', '9787544612369', NULL, 28.00, 38.00, 110, 1543, '上海外语教育出版社', NULL, '韩语零基础入门教材。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (58, 2, 2, '意大利语入门', '王军', '9787544612370', NULL, 30.00, 42.00, 70, 876, '上海外语教育出版社', NULL, '意大利语零基础入门教材。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (59, 2, 2, '葡萄牙语入门', '肖宪', '9787544612371', NULL, 28.00, 38.00, 60, 543, '上海外语教育出版社', NULL, '葡萄牙语零基础入门教材。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (60, 3, 3, 'JavaScript高级程序设计(第4版)', 'Matt Frisbie', '9787115545381', NULL, 89.00, 129.00, 60, 987, '人民邮电出版社', NULL, '本书是JavaScript超级畅销书的最新版。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (61, 3, 3, 'Vue.js设计与实现', '霍春阳', '9787115583895', NULL, 79.00, 109.00, 45, 654, '人民邮电出版社', NULL, '本书深入讲解Vue.js的内部原理和设计思想。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (62, 3, 3, '深入理解Java虚拟机', '周志明', '9787111641247', NULL, 89.00, 129.00, 55, 1234, '机械工业出版社', NULL, '本书是关于Java虚拟机的权威著作。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (63, 3, 3, 'Spring实战(第5版)', 'Craig Walls', '9787115545382', NULL, 79.00, 109.00, 50, 876, '人民邮电出版社', NULL, 'Spring框架实战指南。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (64, 3, 3, 'MySQL必知必会', 'Ben Forta', '9787115545383', NULL, 49.00, 69.00, 70, 654, '人民邮电出版社', NULL, 'MySQL入门经典教材。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (65, 3, 3, 'Redis设计与实现', '黄健宏', '9787115545384', NULL, 69.00, 99.00, 40, 543, '人民邮电出版社', NULL, 'Redis内部实现原理详解。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (66, 3, 3, 'Docker技术入门与实战', '杨保华', '9787115545385', NULL, 59.00, 89.00, 55, 432, '人民邮电出版社', NULL, 'Docker容器化技术实战指南。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (67, 3, 3, 'Kubernetes权威指南', '龚正', '9787115545386', NULL, 89.00, 129.00, 35, 321, '人民邮电出版社', NULL, 'Kubernetes容器编排权威指南。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (68, 3, 3, 'Python编程：从入门到实践', 'Eric Matthes', '9787115545387', NULL, 69.00, 99.00, 80, 1543, '人民邮电出版社', NULL, 'Python入门经典教材。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (69, 3, 3, '机器学习', '周志华', '9787115545388', NULL, 88.00, 128.00, 45, 987, '人民邮电出版社', NULL, '机器学习领域经典教材。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (70, 3, 3, '深度学习', 'Ian Goodfellow', '9787115545389', NULL, 108.00, 158.00, 30, 765, '人民邮电出版社', NULL, '深度学习领域经典著作。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (71, 3, 3, '算法导论', 'Thomas H.Cormen', '9787115545390', NULL, 128.00, 188.00, 25, 543, '人民邮电出版社', NULL, '计算机算法领域经典教材。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (72, 3, 3, '计算机网络：自顶向下方法', 'James Kurose', '9787115545391', NULL, 79.00, 109.00, 50, 654, '人民邮电出版社', NULL, '计算机网络经典教材。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (73, 3, 3, '操作系统概念', 'Abraham Silberschatz', '9787115545392', NULL, 89.00, 129.00, 40, 543, '人民邮电出版社', NULL, '操作系统领域经典教材。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (74, 3, 3, '编译原理', 'Alfred V.Aho', '9787115545393', NULL, 79.00, 109.00, 35, 432, '人民邮电出版社', NULL, '编译原理领域经典教材。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (75, 3, 3, '设计模式', 'Erich Gamma', '9787115545394', NULL, 69.00, 99.00, 60, 765, '人民邮电出版社', NULL, '软件设计模式经典著作。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (76, 3, 3, '重构：改善既有代码的设计', 'Martin Fowler', '9787115545395', NULL, 79.00, 109.00, 50, 654, '人民邮电出版社', NULL, '代码重构经典指南。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (77, 3, 3, '代码整洁之道', 'Robert C.Martin', '9787115545396', NULL, 59.00, 89.00, 65, 543, '人民邮电出版社', NULL, '编写高质量代码的指南。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (78, 3, 3, '敏捷软件开发', 'Robert C.Martin', '9787115545397', NULL, 69.00, 99.00, 45, 432, '人民邮电出版社', NULL, '敏捷开发方法论经典著作。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (79, 3, 3, 'Head First设计模式', 'Eric Freeman', '9787115545398', NULL, 79.00, 109.00, 55, 543, '人民邮电出版社', NULL, '设计模式入门经典教材。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (80, 3, 3, '人月神话', 'Frederick P.Brooks', '9787115545399', NULL, 49.00, 69.00, 40, 321, '人民邮电出版社', NULL, '软件工程领域经典著作。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (81, 3, 3, '黑客与画家', 'Paul Graham', '9787115545400', NULL, 45.00, 65.00, 60, 432, '人民邮电出版社', NULL, '关于计算机技术与创业的思考。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (82, 3, 3, '乔布斯传', '沃尔特·艾萨克森', '9787115545401', NULL, 68.00, 98.00, 70, 1234, '人民邮电出版社', NULL, '苹果公司创始人乔布斯的官方传记。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (83, 3, 3, '浪潮之巅', '吴军', '9787115545402', NULL, 59.00, 89.00, 65, 987, '人民邮电出版社', NULL, '硅谷科技公司的兴衰史。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (84, 3, 3, '智能时代', '吴军', '9787115545403', NULL, 49.00, 69.00, 55, 765, '人民邮电出版社', NULL, '大数据与人工智能时代的思考。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (85, 3, 3, '数学之美', '吴军', '9787115545404', NULL, 45.00, 65.00, 75, 876, '人民邮电出版社', NULL, '数学在计算机科学中的应用。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (86, 3, 3, '硅谷来信', '吴军', '9787115545406', NULL, 49.00, 69.00, 60, 543, '人民邮电出版社', NULL, '关于科技、教育、人生的思考。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (87, 3, 3, '见识', '吴军', '9787115545407', NULL, 45.00, 65.00, 70, 654, '人民邮电出版社', NULL, '关于职业发展与人生的思考。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (88, 3, 3, '格局', '吴军', '9787115545408', NULL, 42.00, 62.00, 65, 543, '人民邮电出版社', NULL, '关于思维方式与格局的思考。', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 10:58:56', 0);
INSERT INTO `book` VALUES (89, 1, 4, '穷查理宝典', '查理·芒格', '9787508663326', NULL, 168.00, 198.00, 50, 2345, '中信出版社', NULL, '巴菲特的合伙人查理·芒格的智慧箴言录。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (90, 1, 5, '设计中的设计', '原研哉', '9787532725670', NULL, 48.00, 68.00, 40, 1543, '山东人民出版社', NULL, '日本设计大师原研哉的设计理念。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (91, 1, 6, '枪炮、病菌与钢铁', '贾雷德·戴蒙德', '9787108009821', NULL, 45.00, 65.00, 55, 1234, '上海译文出版社', NULL, '人类社会命运的思考。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (92, 1, 7, '从一到无穷大', '乔治·伽莫夫', '9787506366281', NULL, 29.00, 45.00, 60, 2345, '作家出版社', NULL, '科普经典，带你走进科学世界。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (93, 1, 8, '断舍离', '山下英子', '9787549550548', NULL, 32.00, 45.00, 70, 3456, '广西科学技术出版社', NULL, '整理人生的新生活方式。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (94, 1, 9, '窗边的小豆豆', '黑柳彻子', '9787538719932', NULL, 25.00, 35.00, 80, 5678, '南海出版公司', NULL, '关于成长的温暖故事。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (95, 1, 10, '读者', '读者杂志社', '9786269706013', NULL, 10.00, 10.00, 200, 8765, '读者杂志社', NULL, '最受欢迎的文摘杂志。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (96, 1, 4, '聪明的投资者', '本杰明·格雷厄姆', '9787508648026', NULL, 68.00, 98.00, 45, 1876, '中信出版社', NULL, '价值投资的圣经。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (97, 1, 5, '写给大家看的设计书', 'Robin Williams', '9787115440389', NULL, 69.00, 99.00, 35, 987, '人民邮电出版社', NULL, '设计入门必读经典。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (98, 1, 6, '乌合之众', '古斯塔夫·勒庞', '9787506365437', NULL, 29.00, 45.00, 65, 2345, '作家出版社', NULL, '群体心理学的经典之作。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (99, 1, 7, '时间简史', '史蒂芬·霍金', '9787535732309', NULL, 38.00, 55.00, 50, 1543, '湖南科学技术出版社', NULL, '探索时间和空间核心秘密。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (100, 1, 8, '极简主义', '弗朗西斯·吉', '9787549570034', NULL, 35.00, 48.00, 60, 1234, '广西师范大学出版社', NULL, '少即是多的生活哲学。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (101, 1, 9, '小王子', '圣埃克苏佩里', '9787020042494', '/uploads/3b8e12c94a1248bb8515a518c728649d.png', 22.00, 32.00, 100, 8765, '人民文学出版社', NULL, '永远长不大的小王子。', '', 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (102, 1, 10, '三联生活周刊', '三联生活周刊杂志社', '9787108009821', NULL, 15.00, 15.00, 150, 4321, '三联生活周刊杂志社', NULL, '中国最具影响力的生活周刊。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (103, 1, 4, '国富论', '亚当·斯密', '9787506365437', NULL, 58.00, 88.00, 40, 876, '作家出版社', NULL, '经济学的奠基之作。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (104, 2, 4, '影响力', '罗伯特·西奥迪尼', '9787508648026', NULL, 59.00, 89.00, 50, 2345, '中信出版社', NULL, '说服心理学的经典之作。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (105, 2, 5, '色彩的艺术', '约翰内斯·伊顿', '9787532725670', NULL, 58.00, 78.00, 35, 987, '上海人民美术出版社', NULL, '色彩理论的经典著作。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (106, 2, 6, '自私的基因', '理查德·道金斯', '9787108009821', NULL, 42.00, 62.00, 45, 1876, '上海译文出版社', NULL, '从基因角度解读进化。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (107, 2, 7, '万物简史', '比尔·布莱森', '9787506366281', NULL, 49.00, 69.00, 55, 1543, '接力出版社', NULL, '一部关于科学的通俗读物。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (108, 2, 8, '活着', '余华', '9787506365437', NULL, 29.00, 45.00, 80, 5678, '作家出版社', NULL, '讲述人生的苦难与坚韧。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (109, 2, 9, '夏洛的网', 'E.B.怀特', '9787532725670', NULL, 25.00, 35.00, 90, 3456, '上海译文出版社', NULL, '关于友谊的经典童话。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (110, 2, 10, '国家地理', '国家地理学会', '9787508648026', NULL, 20.00, 20.00, 180, 6543, '中信出版社', NULL, '探索世界的百科全书。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (111, 2, 4, '思考快与慢', '丹尼尔·卡尼曼', '9787508648026', NULL, 59.00, 89.00, 40, 1234, '中信出版社', NULL, '诺贝尔经济学奖得主的经典之作。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (112, 2, 5, '版式设计原理', '佐藤直树', '9787115440389', NULL, 49.00, 69.00, 30, 654, '人民邮电出版社', NULL, '日本设计大师的版式设计心得。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (113, 2, 6, '娱乐至死', '尼尔·波兹曼', '9787506365437', '', 36.00, 48.00, 55, 2345, '作家出版社', NULL, '对电视文化的深刻反思。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (114, 2, 7, '黑客与画家', '保罗·格雷厄姆', '9787115440389', NULL, 45.00, 65.00, 45, 987, '人民邮电出版社', NULL, '硅谷创业之父的智慧。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (115, 2, 8, '三体', '刘慈欣', '9787536692930', '/uploads/aa8c62b866174c35ade24472a0161c5f.png', 35.00, 59.00, 69, 8766, '重庆出版社', NULL, '中国科幻文学的里程碑。', '', 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (116, 2, 9, '哈利·波特与魔法石', 'J.K.罗琳', '9787020044764', NULL, 29.00, 39.00, 85, 5432, '人民文学出版社', NULL, '魔法世界的大门由此打开。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (117, 2, 10, '新周刊', '新周刊杂志社', '9787108009821', NULL, 12.00, 12.00, 160, 3210, '新周刊杂志社', NULL, '中国最新锐的生活方式周刊。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (118, 2, 4, '资本论', '卡尔·马克思', '9787506365437', NULL, 128.00, 168.00, 30, 543, '作家出版社', NULL, '马克思的经典经济学著作。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (119, 3, 4, '经济学原理', '曼昆', '9787301256510', NULL, 75.00, 98.00, 45, 654, '北京大学出版社', NULL, '世界上最流行的经济学教材。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (120, 3, 5, '艺术的故事', '贡布里希', '9787549587278', NULL, 128.00, 168.00, 30, 234, '广西美术出版社', NULL, '有关艺术的书籍中最著名的著作。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (121, 3, 6, '人类简史', '尤瓦尔·赫拉利', '9787508647357', NULL, 45.00, 68.00, 60, 1876, '中信出版社', NULL, '从认知革命到科学革命。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (122, 3, 7, '时间简史', '史蒂芬·霍金', '9787535732309', NULL, 38.00, 55.00, 55, 1543, '湖南科学技术出版社', NULL, '探索时间和空间核心秘密。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (123, 3, 8, '小王子', '圣埃克苏佩里', '9787020042494', NULL, 22.00, 32.00, 90, 5678, '人民文学出版社', NULL, '一个永远长不大的小王子。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (124, 3, 9, '窗边的小豆豆', '黑柳彻子', '9787538719932', NULL, 25.00, 35.00, 75, 3456, '南海出版公司', NULL, '关于成长的温暖故事。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (125, 3, 10, '读者', '读者杂志社', '9786269706013', '/uploads/75b802d9e69e4283903f47374a928a99.png', 10.00, 10.00, 200, 8765, '读者杂志社', NULL, '最受欢迎的文摘杂志。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (126, 3, 4, '穷查理宝典', '查理·芒格', '9787508663326', NULL, 168.00, 198.00, 35, 1234, '中信出版社', NULL, '巴菲特的合伙人查理·芒格的智慧。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (127, 3, 5, '设计中的设计', '原研哉', '9787532725670', NULL, 48.00, 68.00, 40, 987, '山东人民出版社', NULL, '日本设计大师原研哉的设计理念。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (128, 3, 6, '枪炮、病菌与钢铁', '贾雷德·戴蒙德', '9787108009821', NULL, 45.00, 65.00, 50, 1543, '上海译文出版社', NULL, '人类社会命运的思考。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (129, 3, 7, '从一到无穷大', '乔治·伽莫夫', '9787506366281', NULL, 29.00, 45.00, 65, 2345, '作家出版社', NULL, '科普经典，带你走进科学世界。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (130, 3, 8, '断舍离', '山下英子', '9787549550548', NULL, 32.00, 45.00, 70, 1876, '广西科学技术出版社', NULL, '整理人生的新生活方式。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (131, 3, 9, '夏洛的网', 'E.B.怀特', '9787532725670', NULL, 25.00, 35.00, 80, 2345, '上海译文出版社', NULL, '关于友谊的经典童话。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (132, 3, 10, '三联生活周刊', '三联生活周刊杂志社', '9787108009821', NULL, 15.00, 15.00, 150, 4321, '三联生活周刊杂志社', NULL, '中国最具影响力的生活周刊。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (133, 3, 4, '国富论', '亚当·斯密', '9787506365437', NULL, 58.00, 88.00, 45, 876, '作家出版社', NULL, '经济学的奠基之作。', NULL, 1, '2026-06-02 11:11:34', '2026-06-02 11:11:34', 0);
INSERT INTO `book` VALUES (134, 1, 1, '123', '1', '1', '', 2.00, 3.00, 1, 0, '', NULL, '', NULL, 1, NULL, '2026-06-03 11:17:39', 1);

-- ----------------------------
-- Table structure for book_image
-- ----------------------------
DROP TABLE IF EXISTS `book_image`;
CREATE TABLE `book_image`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `book_id` bigint NOT NULL COMMENT '图书ID',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_book_id`(`book_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '图书详情图片表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_image
-- ----------------------------

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `book_id` bigint NOT NULL COMMENT '图书ID',
  `quantity` int NULL DEFAULT 1 COMMENT '数量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_book`(`user_id` ASC, `book_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart
-- ----------------------------

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父级ID, 0为顶级分类',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '图书分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '文学小说', 0, 1, '2026-06-01 16:09:05', '2026-06-01 16:09:05');
INSERT INTO `category` VALUES (2, '教育考试', 0, 2, '2026-06-01 16:09:05', '2026-06-01 16:09:05');
INSERT INTO `category` VALUES (3, '计算机与互联网', 0, 3, '2026-06-01 16:09:05', '2026-06-01 16:09:05');
INSERT INTO `category` VALUES (4, '经济管理', 0, 4, '2026-06-01 16:09:05', '2026-06-01 16:09:05');
INSERT INTO `category` VALUES (5, '艺术设计', 0, 5, '2026-06-01 16:09:05', '2026-06-01 16:09:05');
INSERT INTO `category` VALUES (6, '人文社科', 0, 6, '2026-06-01 16:09:05', '2026-06-01 16:09:05');
INSERT INTO `category` VALUES (7, '科技工程', 0, 7, '2026-06-01 16:09:05', '2026-06-01 16:09:05');
INSERT INTO `category` VALUES (8, '生活休闲', 0, 8, '2026-06-01 16:09:05', '2026-06-01 16:09:05');
INSERT INTO `category` VALUES (9, '童书绘本', 0, 9, '2026-06-01 16:09:05', '2026-06-01 16:09:05');
INSERT INTO `category` VALUES (10, '杂志期刊', 0, 10, '2026-06-01 16:09:05', '2026-06-01 16:09:05');

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0待付款 1待发货 2已发货 3已完成 4已取消',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收件人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收件电话',
  `receiver_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收件地址',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `ship_time` datetime NULL DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime NULL DEFAULT NULL COMMENT '收货时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES (1, '2061636310815608832', 1, 29.00, 3, '张三', '13800138001', '广东省深圳市南山区科技园路1号', '2026-06-02 10:29:41', '2026-06-02 10:30:29', '2026-06-02 10:31:17', NULL, NULL, 0);
INSERT INTO `order` VALUES (2, '2062000739046567936', 1, 35.00, 4, '张三', '13800138001', '广东省深圳市南山区科技园路1号', NULL, NULL, NULL, NULL, NULL, 0);
INSERT INTO `order` VALUES (3, '2062000872182165504', 1, 35.00, 4, '张三', '13800138001', '广东省深圳市南山区科技园路1号', NULL, NULL, NULL, NULL, NULL, 0);
INSERT INTO `order` VALUES (4, '2062001412744048640', 1, 35.00, 4, '张三', '13800138001', '广东省深圳市南山区科技园路1号', NULL, NULL, NULL, NULL, NULL, 0);
INSERT INTO `order` VALUES (5, '2062001468511514624', 1, 35.00, 3, '张三', '13800138001', '广东省深圳市南山区科技园路1号', '2026-06-03 10:40:42', '2026-06-03 10:48:24', '2026-06-03 10:49:57', NULL, NULL, 0);

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `book_id` bigint NOT NULL COMMENT '图书ID',
  `book_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '书名(冗余)',
  `book_cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面(冗余)',
  `price` decimal(10, 2) NOT NULL COMMENT '单价',
  `quantity` int NOT NULL COMMENT '数量',
  `shop_id` bigint NOT NULL COMMENT '商家ID(冗余)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_shop_id`(`shop_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单详情表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 1, 1, '活着', NULL, 29.00, 1, 1, NULL);
INSERT INTO `order_item` VALUES (2, 2, 115, '三体', '/uploads/aa8c62b866174c35ade24472a0161c5f.png', 35.00, 1, 2, NULL);
INSERT INTO `order_item` VALUES (3, 3, 115, '三体', '/uploads/aa8c62b866174c35ade24472a0161c5f.png', 35.00, 1, 2, NULL);
INSERT INTO `order_item` VALUES (4, 4, 115, '三体', '/uploads/aa8c62b866174c35ade24472a0161c5f.png', 35.00, 1, 2, NULL);
INSERT INTO `order_item` VALUES (5, 5, 115, '三体', '/uploads/aa8c62b866174c35ade24472a0161c5f.png', 35.00, 1, 2, NULL);

-- ----------------------------
-- Table structure for review
-- ----------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `book_id` bigint NOT NULL COMMENT '图书ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `rating` tinyint NOT NULL COMMENT '评分 1-5',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评价内容',
  `images` json NULL COMMENT '评价图片(JSON数组)',
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商家回复',
  `reply_time` datetime NULL DEFAULT NULL COMMENT '回复时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_book_id`(`book_id` ASC) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of review
-- ----------------------------
INSERT INTO `review` VALUES (1, 1, 1, 1, 5, '书不错', NULL, NULL, NULL, NULL);
INSERT INTO `review` VALUES (2, 1, 115, 5, 5, '可以', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for shop
-- ----------------------------
DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `shop_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '店铺名',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '店铺Logo',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '店铺简介',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `contact_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系邮箱',
  `license_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '营业执照号',
  `license_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '营业执照图片',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0待审核 1正常 2禁用 3拒绝',
  `audit_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核意见',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商家表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shop
-- ----------------------------
INSERT INTO `shop` VALUES (1, 3, '悦读书屋', NULL, '专注精品图书，为您推荐好书', '13900139001', NULL, NULL, NULL, 1, NULL, NULL, '2026-06-01 16:09:05', '2026-06-01 16:09:05');
INSERT INTO `shop` VALUES (2, 4, '博雅书坊', NULL, '学术著作与经典文献专营', '13900139002', NULL, NULL, NULL, 1, NULL, NULL, '2026-06-02 10:58:56', '2026-06-02 10:58:56');
INSERT INTO `shop` VALUES (3, 5, '墨香阁', NULL, '原创文学与新书速递', '13900139003', NULL, NULL, NULL, 1, NULL, NULL, '2026-06-02 10:58:56', '2026-06-02 10:58:56');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码(BCrypt加密)',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0禁用 1正常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'user1', '$2a$10$bSJTGhqw.hJ3wtdvWi9u.eVqfwG8MrF6WwkeagmNKVp5NqNGDw2TW', '测试用户1', NULL, '13800138001', NULL, 1, '2026-06-01 16:09:05', '2026-06-01 16:09:05', 0);
INSERT INTO `user` VALUES (2, 'user2', '$2a$10$LD1QBabnloAJipIfSFuA3O/in5THyzH/b6q/nosnAox3J3AKwFIVW', '测试用户2', NULL, '13800138002', NULL, 1, '2026-06-01 16:09:05', '2026-06-01 16:09:05', 0);
INSERT INTO `user` VALUES (3, 'shop1', '$2a$10$rxRODErOz/FEXFnrrokGEekNQz4sx.E7xnGeJ5IItK3m3qhFE.cGi', '商家1', NULL, '13900139001', NULL, 1, '2026-06-01 16:09:05', '2026-06-01 16:09:05', 0);
INSERT INTO `user` VALUES (4, 'shop2', '$2a$10$XNV9hFUwqyGgOtHoyEPa1eKGUXKVC4irVT3J9qaLw0/U9al3FvyTK', '商家2', NULL, '13900139002', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 11:17:06', 0);
INSERT INTO `user` VALUES (5, 'shop3', '$2a$10$XNV9hFUwqyGgOtHoyEPa1eKGUXKVC4irVT3J9qaLw0/U9al3FvyTK', '商家3', NULL, '13900139003', NULL, 1, '2026-06-02 10:58:56', '2026-06-02 11:17:06', 0);

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `book_id` bigint NOT NULL COMMENT '图书ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_book`(`user_id` ASC, `book_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '收藏表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
