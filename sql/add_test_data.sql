-- ================================================
-- 添加测试数据脚本
-- 添加2个新商店，每个商店30本图书
-- 原有shop1也补充到30本图书
-- ================================================

USE bookstore;

-- ================================================
-- 1. 添加新商家用户账号
-- ================================================
-- 密码都是 123456 (BCrypt加密)

-- shop2 用户
INSERT IGNORE INTO `user` (`username`, `password`, `nickname`, `phone`, `status`)
VALUES ('shop2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '商家2', '13900139002', 1);

-- shop3 用户
INSERT IGNORE INTO `user` (`username`, `password`, `nickname`, `phone`, `status`)
VALUES ('shop3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '商家3', '13900139003', 1);

-- ================================================
-- 2. 添加新店铺
-- ================================================

-- 获取 shop2 的 user_id
SET @shop2_user_id = (SELECT id FROM user WHERE username = 'shop2');
SET @shop3_user_id = (SELECT id FROM user WHERE username = 'shop3');

-- 检查店铺是否已存在，不存在则插入
INSERT IGNORE INTO `shop` (`user_id`, `shop_name`, `description`, `contact_phone`, `status`)
SELECT @shop2_user_id, '博雅书坊', '学术著作与经典文献专营', '13900139002', 1
WHERE NOT EXISTS (SELECT 1 FROM shop WHERE user_id = @shop2_user_id);

INSERT IGNORE INTO `shop` (`user_id`, `shop_name`, `description`, `contact_phone`, `status`)
SELECT @shop3_user_id, '墨香阁', '原创文学与新书速递', '13900139003', 1
WHERE NOT EXISTS (SELECT 1 FROM shop WHERE user_id = @shop3_user_id);

-- ================================================
-- 3. 获取店铺ID
-- ================================================
SET @shop1_id = (SELECT id FROM shop WHERE shop_name = '悦读书屋');
SET @shop2_id = (SELECT id FROM shop WHERE shop_name = '博雅书坊');
SET @shop3_id = (SELECT id FROM shop WHERE shop_name = '墨香阁');

-- ================================================
-- 4. 检查shop1现有图书数量
-- ================================================
SET @shop1_book_count = (SELECT COUNT(*) FROM book WHERE shop_id = @shop1_id);

-- ================================================
-- 5. 为shop1添加图书（补齐到30本）
-- ================================================
-- 如果shop1已有图书，则不重复添加

-- shop1 图书 (悦读书屋 - 文学小说类)
INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '活着', '余华', '9787506365437', 29.00, 45.00, 100, 2345, '作家出版社', '地主少爷福贵嗜赌成性，终于赌光了家业一贫如洗。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '活着');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '三体', '刘慈欣', '9787536692930', 35.00, 59.00, 80, 1876, '重庆出版社', '文化大革命如火如荼进行的同时，军方探寻外星文明的绝秘计划红岸工程取得了突破性进展。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '三体');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '百年孤独', '加西亚·马尔克斯', '9787544253994', 39.50, 55.00, 60, 1543, '南海出版公司', '马孔多是何等的魔幻与孤独。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '百年孤独');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '围城', '钱钟书', '9787020024759', 28.00, 39.00, 90, 1234, '人民文学出版社', '围在城里的人想逃出来，城外的人想冲进去。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '围城');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '平凡的世界', '路遥', '9787530212004', 68.00, 98.00, 70, 2345, '北京十月文艺出版社', '全景式地表现了中国当代城乡社会生活。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '平凡的世界');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '白鹿原', '陈忠实', '9787544254366', 39.50, 56.00, 65, 1543, '南海出版公司', '一部渭河平原的雄奇史诗。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '白鹿原');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '尘埃落定', '阿来', '9787544221498', 28.00, 38.00, 80, 876, '南海出版公司', '一个藏族土司家族的兴衰史。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '尘埃落定');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '额尔古纳河右岸', '迟子建', '9787506365438', 29.00, 42.00, 75, 654, '作家出版社', '一位年届九旬的鄂温克族最后一位酋长女人的自述。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '额尔古纳河右岸');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '长恨歌', '王安忆', '9787532120857', 28.00, 38.00, 60, 543, '上海文艺出版社', '一个女人四十年的情与爱，一座城市的变迁。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '长恨歌');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '秦腔', '贾平凹', '9787506365439', 36.00, 48.00, 55, 432, '作家出版社', '一部用中国最古老歌谣吟唱的当代史诗。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '秦腔');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '废都', '贾平凹', '9787506365440', 32.00, 45.00, 70, 654, '作家出版社', '一部关于知识分子的精神危机的小说。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '废都');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '黄金时代', '王小波', '9787506365441', 25.00, 35.00, 85, 876, '作家出版社', '知青王二与陈清扬的爱情故事。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '黄金时代');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '沉默的大多数', '王小波', '9787506365442', 28.00, 38.00, 90, 987, '作家出版社', '对社会道德伦理问题的思考。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '沉默的大多数');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '万历十五年', '黄仁宇', '9787506365443', 26.00, 36.00, 75, 1234, '作家出版社', '一个看似平淡的年份，却影响了中国历史的走向。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '万历十五年');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '明朝那些事儿', '当年明月', '9787506365444', 29.00, 42.00, 100, 3456, '作家出版社', '一部好看又好玩的明朝历史。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '明朝那些事儿');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '论语', '孔子', '9787506365447', 18.00, 28.00, 120, 4567, '作家出版社', '儒家学派的经典著作。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '论语');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '道德经', '老子', '9787506365448', 15.00, 22.00, 150, 5678, '作家出版社', '道家哲学的奠基之作。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '道德经');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '孙子兵法', '孙武', '9787506365449', 16.00, 25.00, 130, 3456, '作家出版社', '中国古典军事文化遗产中的璀璨瑰宝。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '孙子兵法');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '诗经', '佚名', '9787506365450', 20.00, 30.00, 100, 2345, '作家出版社', '中国第一部诗歌总集。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '诗经');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '楚辞', '屈原', '9787506365451', 22.00, 32.00, 90, 1876, '作家出版社', '中国文学史上第一部浪漫主义诗歌总集。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '楚辞');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '唐诗三百首', '蘅塘退士', '9787506365452', 18.00, 26.00, 140, 4567, '作家出版社', '唐诗选本中流传最广的一种。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '唐诗三百首');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '宋词三百首', '朱孝臧', '9787506365453', 19.00, 28.00, 120, 3456, '作家出版社', '宋词选本中的经典之作。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '宋词三百首');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '古文观止', '吴楚材', '9787506365455', 24.00, 35.00, 100, 1876, '作家出版社', '中国古代散文的精华选本。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '古文观止');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '资治通鉴', '司马光', '9787506365456', 68.00, 98.00, 50, 1234, '作家出版社', '中国第一部编年体通史。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '资治通鉴');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '史记', '司马迁', '9787506365457', 75.00, 108.00, 45, 1543, '作家出版社', '中国第一部纪传体通史。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '史记');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '三国演义', '罗贯中', '9787506365458', 35.00, 52.00, 80, 3456, '作家出版社', '中国第一部长篇章回体历史演义小说。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '三国演义');

INSERT IGNORE INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop1_id, 1, '水浒传', '施耐庵', '9787506365459', 32.00, 48.00, 75, 2876, '作家出版社', '中国历史上第一部用白话文写成的章回小说。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop1_id AND title = '水浒传');

-- ================================================
-- 6. 为shop2添加图书（博雅书坊 - 教材教辅类）
-- ================================================

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '高等数学(第七版)上册', '同济大学数学系', '9787040396638', 32.00, 39.80, 150, 5678, '高等教育出版社', '本书是同济大学数学系编的高等数学第七版上册。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '高等数学(第七版)上册');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '线性代数', '同济大学数学系', '9787040396607', 22.00, 28.00, 120, 3456, '高等教育出版社', '本书是同济大学数学系编的线性代数第六版。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '线性代数');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '概率论与数理统计', '浙江大学', '9787040396645', 28.00, 35.00, 100, 2345, '高等教育出版社', '本书是浙江大学编的概率论与数理统计第四版。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '概率论与数理统计');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '新概念英语(1)', 'L.G.Alexander', '9787544612346', 29.00, 38.00, 200, 5678, '上海外语教育出版社', '经典英语学习教材。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '新概念英语(1)');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '新概念英语(2)', 'L.G.Alexander', '9787544612347', 32.00, 42.00, 180, 4567, '上海外语教育出版社', '经典英语学习教材进阶篇。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '新概念英语(2)');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '新概念英语(3)', 'L.G.Alexander', '9787544612348', 35.00, 45.00, 150, 3456, '上海外语教育出版社', '经典英语学习教材提高篇。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '新概念英语(3)');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '新概念英语(4)', 'L.G.Alexander', '9787544612349', 38.00, 48.00, 120, 2345, '上海外语教育出版社', '经典英语学习教材高级篇。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '新概念英语(4)');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '考研英语词汇', '朱泰祺', '9787544612350', 42.00, 56.00, 100, 1876, '上海外语教育出版社', '考研英语词汇速记手册。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '考研英语词汇');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '考研政治大纲解析', '教育部考试中心', '9787544612351', 48.00, 62.00, 90, 1543, '高等教育出版社', '考研政治官方大纲解析。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '考研政治大纲解析');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '考研数学复习指南', '陈文灯', '9787544612352', 55.00, 72.00, 80, 1234, '高等教育出版社', '考研数学经典复习指南。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '考研数学复习指南');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '公务员考试行政能力测验', '华图教育', '9787544612353', 45.00, 62.00, 110, 2345, '高等教育出版社', '公务员考试必备教材。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '公务员考试行政能力测验');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '公务员考试申论', '华图教育', '9787544612354', 42.00, 58.00, 100, 1876, '高等教育出版社', '公务员考试申论必备教材。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '公务员考试申论');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '司法考试教材', '众合教育', '9787544612355', 68.00, 88.00, 60, 876, '高等教育出版社', '司法考试官方教材。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '司法考试教材');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '注册会计师考试教材', '中国注册会计师协会', '9787544612356', 58.00, 75.00, 70, 654, '高等教育出版社', '注册会计师考试官方教材。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '注册会计师考试教材');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '教师资格证考试教材', '中公教育', '9787544612357', 38.00, 52.00, 120, 3456, '高等教育出版社', '教师资格证考试必备教材。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '教师资格证考试教材');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '计算机二级考试教材', '教育部考试中心', '9787544612358', 35.00, 48.00, 130, 2876, '高等教育出版社', '计算机二级考试官方教材。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '计算机二级考试教材');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '英语四级词汇', '新东方', '9787544612359', 28.00, 38.00, 150, 4567, '上海外语教育出版社', '英语四级核心词汇速记。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '英语四级词汇');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '英语六级词汇', '新东方', '9787544612360', 30.00, 40.00, 140, 3456, '上海外语教育出版社', '英语六级核心词汇速记。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '英语六级词汇');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '雅思词汇', '新东方', '9787544612361', 35.00, 48.00, 100, 2345, '上海外语教育出版社', '雅思考试核心词汇速记。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '雅思词汇');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '托福词汇', '新东方', '9787544612362', 38.00, 52.00, 90, 1876, '上海外语教育出版社', '托福考试核心词汇速记。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '托福词汇');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, 'GRE词汇', '新东方', '9787544612363', 42.00, 58.00, 80, 1543, '上海外语教育出版社', 'GRE考试核心词汇速记。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = 'GRE词汇');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '日语能力考试N1词汇', '新东方', '9787544612364', 32.00, 45.00, 110, 2345, '上海外语教育出版社', '日语能力考试N1核心词汇。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '日语能力考试N1词汇');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '日语能力考试N2词汇', '新东方', '9787544612365', 30.00, 42.00, 120, 1876, '上海外语教育出版社', '日语能力考试N2核心词汇。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '日语能力考试N2词汇');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '法语入门', '马晓宏', '9787544612366', 28.00, 38.00, 100, 1234, '上海外语教育出版社', '法语零基础入门教材。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '法语入门');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '德语入门', '赵登荣', '9787544612367', 30.00, 42.00, 90, 876, '上海外语教育出版社', '德语零基础入门教材。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '德语入门');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '西班牙语入门', '董燕生', '9787544612368', 32.00, 45.00, 80, 654, '上海外语教育出版社', '西班牙语零基础入门教材。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '西班牙语入门');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '韩语入门', '李先汉', '9787544612369', 28.00, 38.00, 110, 1543, '上海外语教育出版社', '韩语零基础入门教材。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '韩语入门');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '意大利语入门', '王军', '9787544612370', 30.00, 42.00, 70, 876, '上海外语教育出版社', '意大利语零基础入门教材。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '意大利语入门');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop2_id, 2, '葡萄牙语入门', '肖宪', '9787544612371', 28.00, 38.00, 60, 543, '上海外语教育出版社', '葡萄牙语零基础入门教材。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop2_id AND title = '葡萄牙语入门');

-- ================================================
-- 7. 为shop3添加图书（墨香阁 - 计算机技术类）
-- ================================================

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, 'JavaScript高级程序设计(第4版)', 'Matt Frisbie', '9787115545381', 89.00, 129.00, 60, 987, '人民邮电出版社', '本书是JavaScript超级畅销书的最新版。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = 'JavaScript高级程序设计(第4版)');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, 'Vue.js设计与实现', '霍春阳', '9787115583895', 79.00, 109.00, 45, 654, '人民邮电出版社', '本书深入讲解Vue.js的内部原理和设计思想。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = 'Vue.js设计与实现');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, '深入理解Java虚拟机', '周志明', '9787111641247', 89.00, 129.00, 55, 1234, '机械工业出版社', '本书是关于Java虚拟机的权威著作。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = '深入理解Java虚拟机');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, 'Spring实战(第5版)', 'Craig Walls', '9787115545382', 79.00, 109.00, 50, 876, '人民邮电出版社', 'Spring框架实战指南。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = 'Spring实战(第5版)');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, 'MySQL必知必会', 'Ben Forta', '9787115545383', 49.00, 69.00, 70, 654, '人民邮电出版社', 'MySQL入门经典教材。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = 'MySQL必知必会');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, 'Redis设计与实现', '黄健宏', '9787115545384', 69.00, 99.00, 40, 543, '人民邮电出版社', 'Redis内部实现原理详解。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = 'Redis设计与实现');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, 'Docker技术入门与实战', '杨保华', '9787115545385', 59.00, 89.00, 55, 432, '人民邮电出版社', 'Docker容器化技术实战指南。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = 'Docker技术入门与实战');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, 'Kubernetes权威指南', '龚正', '9787115545386', 89.00, 129.00, 35, 321, '人民邮电出版社', 'Kubernetes容器编排权威指南。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = 'Kubernetes权威指南');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, 'Python编程：从入门到实践', 'Eric Matthes', '9787115545387', 69.00, 99.00, 80, 1543, '人民邮电出版社', 'Python入门经典教材。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = 'Python编程：从入门到实践');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, '机器学习', '周志华', '9787115545388', 88.00, 128.00, 45, 987, '人民邮电出版社', '机器学习领域经典教材。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = '机器学习');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, '深度学习', 'Ian Goodfellow', '9787115545389', 108.00, 158.00, 30, 765, '人民邮电出版社', '深度学习领域经典著作。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = '深度学习');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, '算法导论', 'Thomas H.Cormen', '9787115545390', 128.00, 188.00, 25, 543, '人民邮电出版社', '计算机算法领域经典教材。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = '算法导论');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, '计算机网络：自顶向下方法', 'James Kurose', '9787115545391', 79.00, 109.00, 50, 654, '人民邮电出版社', '计算机网络经典教材。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = '计算机网络：自顶向下方法');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, '操作系统概念', 'Abraham Silberschatz', '9787115545392', 89.00, 129.00, 40, 543, '人民邮电出版社', '操作系统领域经典教材。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = '操作系统概念');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, '编译原理', 'Alfred V.Aho', '9787115545393', 79.00, 109.00, 35, 432, '人民邮电出版社', '编译原理领域经典教材。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = '编译原理');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, '设计模式', 'Erich Gamma', '9787115545394', 69.00, 99.00, 60, 765, '人民邮电出版社', '软件设计模式经典著作。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = '设计模式');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, '重构：改善既有代码的设计', 'Martin Fowler', '9787115545395', 79.00, 109.00, 50, 654, '人民邮电出版社', '代码重构经典指南。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = '重构：改善既有代码的设计');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, '代码整洁之道', 'Robert C.Martin', '9787115545396', 59.00, 89.00, 65, 543, '人民邮电出版社', '编写高质量代码的指南。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = '代码整洁之道');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, '敏捷软件开发', 'Robert C.Martin', '9787115545397', 69.00, 99.00, 45, 432, '人民邮电出版社', '敏捷开发方法论经典著作。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = '敏捷软件开发');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, 'Head First设计模式', 'Eric Freeman', '9787115545398', 79.00, 109.00, 55, 543, '人民邮电出版社', '设计模式入门经典教材。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = 'Head First设计模式');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, '人月神话', 'Frederick P.Brooks', '9787115545399', 49.00, 69.00, 40, 321, '人民邮电出版社', '软件工程领域经典著作。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = '人月神话');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, '黑客与画家', 'Paul Graham', '9787115545400', 45.00, 65.00, 60, 432, '人民邮电出版社', '关于计算机技术与创业的思考。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = '黑客与画家');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, '乔布斯传', '沃尔特·艾萨克森', '9787115545401', 68.00, 98.00, 70, 1234, '人民邮电出版社', '苹果公司创始人乔布斯的官方传记。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = '乔布斯传');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, '浪潮之巅', '吴军', '9787115545402', 59.00, 89.00, 65, 987, '人民邮电出版社', '硅谷科技公司的兴衰史。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = '浪潮之巅');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, '智能时代', '吴军', '9787115545403', 49.00, 69.00, 55, 765, '人民邮电出版社', '大数据与人工智能时代的思考。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = '智能时代');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, '数学之美', '吴军', '9787115545404', 45.00, 65.00, 75, 876, '人民邮电出版社', '数学在计算机科学中的应用。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = '数学之美');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, '硅谷来信', '吴军', '9787115545406', 49.00, 69.00, 60, 543, '人民邮电出版社', '关于科技、教育、人生的思考。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = '硅谷来信');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, '见识', '吴军', '9787115545407', 45.00, 65.00, 70, 654, '人民邮电出版社', '关于职业发展与人生的思考。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = '见识');

INSERT INTO `book` (`shop_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `stock`, `sales`, `publisher`, `description`, `status`)
SELECT @shop3_id, 3, '格局', '吴军', '9787115545408', 42.00, 62.00, 65, 543, '人民邮电出版社', '关于思维方式与格局的思考。', 1
WHERE NOT EXISTS (SELECT 1 FROM book WHERE shop_id = @shop3_id AND title = '格局');

-- ================================================
-- 完成提示
-- ================================================
SELECT '测试数据添加完成！' AS '结果';
SELECT shop_name, (SELECT COUNT(*) FROM book WHERE book.shop_id = shop.id) AS book_count FROM shop;
