package com.bookstore.config;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.entity.*;
import com.bookstore.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据初始化 - 启动时自动创建测试账号
 */
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private BookMapper bookMapper;

    @Override
    public void run(String... args) {
        // 初始化管理员
        initAdmin();
        // 初始化测试用户
        initTestUsers();
        // 初始化测试商家
        initTestShop();
        // 初始化测试图书
        initTestBooks();
        log.info("===== 数据初始化完成 =====");
    }

    private void initAdmin() {
        Long count = adminMapper.selectCount(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, "admin")
        );
        if (count == 0) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(BCrypt.hashpw("admin123"));
            adminMapper.insert(admin);
            log.info("已创建管理员账号: admin / admin123");
        } else {
            // 更新密码确保正确
            Admin admin = adminMapper.selectOne(
                    new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, "admin")
            );
            if (!BCrypt.checkpw("admin123", admin.getPassword())) {
                admin.setPassword(BCrypt.hashpw("admin123"));
                adminMapper.updateById(admin);
                log.info("已更新管理员密码");
            }
        }
    }

    private void initTestUsers() {
        String[][] users = {
                {"user1", "123456", "测试用户1", "13800138001"},
                {"user2", "123456", "测试用户2", "13800138002"}
        };

        for (String[] u : users) {
            Long count = userMapper.selectCount(
                    new LambdaQueryWrapper<User>().eq(User::getUsername, u[0])
            );
            if (count == 0) {
                User user = new User();
                user.setUsername(u[0]);
                user.setPassword(BCrypt.hashpw(u[1]));
                user.setNickname(u[2]);
                user.setPhone(u[3]);
                user.setStatus(1);
                userMapper.insert(user);
                log.info("已创建用户账号: {} / {}", u[0], u[1]);
            } else {
                // 更新密码确保正确
                User user = userMapper.selectOne(
                        new LambdaQueryWrapper<User>().eq(User::getUsername, u[0])
                );
                if (!BCrypt.checkpw(u[1], user.getPassword())) {
                    user.setPassword(BCrypt.hashpw(u[1]));
                    userMapper.updateById(user);
                }
            }
        }
    }

    private void initTestShop() {
        String shopUsername = "shop1";
        String shopPassword = "123456";

        Long userCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, shopUsername)
        );

        Long userId;
        if (userCount == 0) {
            User shopUser = new User();
            shopUser.setUsername(shopUsername);
            shopUser.setPassword(BCrypt.hashpw(shopPassword));
            shopUser.setNickname("商家1");
            shopUser.setPhone("13900139001");
            shopUser.setStatus(1);
            userMapper.insert(shopUser);
            userId = shopUser.getId();
            log.info("已创建商家账号: {} / {}", shopUsername, shopPassword);
        } else {
            User user = userMapper.selectOne(
                    new LambdaQueryWrapper<User>().eq(User::getUsername, shopUsername)
            );
            if (!BCrypt.checkpw(shopPassword, user.getPassword())) {
                user.setPassword(BCrypt.hashpw(shopPassword));
                userMapper.updateById(user);
            }
            userId = user.getId();
        }

        // 确保店铺存在
        Long shopCount = shopMapper.selectCount(
                new LambdaQueryWrapper<Shop>().eq(Shop::getUserId, userId)
        );
        if (shopCount == 0) {
            Shop shop = new Shop();
            shop.setUserId(userId);
            shop.setShopName("悦读书屋");
            shop.setDescription("专注精品图书，为您推荐好书");
            shop.setContactPhone("13900139001");
            shop.setStatus(1); // 直接审核通过
            shopMapper.insert(shop);
            log.info("已创建测试店铺: 悦读书屋");
        }
    }

    private void initTestBooks() {
        Shop shop = shopMapper.selectOne(
                new LambdaQueryWrapper<Shop>().eq(Shop::getShopName, "悦读书屋")
        );
        if (shop == null) return;

        // 检查是否已有图书
        Long bookCount = bookMapper.selectCount(
                new LambdaQueryWrapper<Book>().eq(Book::getShopId, shop.getId())
        );
        if (bookCount > 0) return;

        // 悦读书屋的图书 (15本)
        Object[][] books = {
                {1L, "活着", "余华", "9787506365437", 29.00, 45.00, 100, 2345, "作家出版社", "地主少爷福贵嗜赌成性，终于赌光了家业一贫如洗。"},
                {1L, "三体", "刘慈欣", "9787536692930", 35.00, 59.00, 80, 1876, "重庆出版社", "文化大革命如火如荼进行的同时，军方探寻外星文明的绝秘计划红岸工程取得了突破性进展。"},
                {1L, "百年孤独", "加西亚·马尔克斯", "9787544253994", 39.50, 55.00, 60, 1543, "南海出版公司", "马孔多是何等的魔幻与孤独。"},
                {1L, "围城", "钱钟书", "9787020024759", 28.00, 39.00, 90, 1234, "人民文学出版社", "围在城里的人想逃出来，城外的人想冲进去。"},
                {1L, "平凡的世界", "路遥", "9787530212004", 68.00, 98.00, 70, 2345, "北京十月文艺出版社", "全景式地表现了中国当代城乡社会生活。"},
                {1L, "白鹿原", "陈忠实", "9787544254366", 39.50, 56.00, 65, 1543, "南海出版公司", "一部渭河平原的雄奇史诗。"},
                {1L, "尘埃落定", "阿来", "9787544221498", 28.00, 38.00, 80, 876, "南海出版公司", "一个藏族土司家族的兴衰史。"},
                {1L, "额尔古纳河右岸", "迟子建", "9787506365438", 29.00, 42.00, 75, 654, "作家出版社", "一位年届九旬的鄂温克族最后一位酋长女人的自述。"},
                {1L, "长恨歌", "王安忆", "9787532120857", 28.00, 38.00, 60, 543, "上海文艺出版社", "一个女人四十年的情与爱，一座城市的变迁。"},
                {1L, "秦腔", "贾平凹", "9787506365439", 36.00, 48.00, 55, 432, "作家出版社", "一部用中国最古老歌谣吟唱的当代史诗。"},
                {1L, "废都", "贾平凹", "9787506365440", 32.00, 45.00, 70, 654, "作家出版社", "一部关于知识分子的精神危机的小说。"},
                {1L, "黄金时代", "王小波", "9787506365441", 25.00, 35.00, 85, 876, "作家出版社", "知青王二与陈清扬的爱情故事。"},
                {1L, "沉默的大多数", "王小波", "9787506365442", 28.00, 38.00, 90, 987, "作家出版社", "对社会道德伦理问题的思考。"},
                {1L, "万历十五年", "黄仁宇", "9787506365443", 26.00, 36.00, 75, 1234, "作家出版社", "一个看似平淡的年份，却影响了中国历史的走向。"},
                {1L, "明朝那些事儿", "当年明月", "9787506365444", 29.00, 42.00, 100, 3456, "作家出版社", "一部好看又好玩的明朝历史。"}
        };

        for (Object[] b : books) {
            Book book = new Book();
            book.setShopId(shop.getId());
            book.setCategoryId((Long) b[0]);
            book.setTitle((String) b[1]);
            book.setAuthor((String) b[2]);
            book.setIsbn((String) b[3]);
            book.setPrice(new java.math.BigDecimal((Double) b[4]));
            book.setOriginalPrice(new java.math.BigDecimal((Double) b[5]));
            book.setStock((Integer) b[6]);
            book.setSales((Integer) b[7]);
            book.setPublisher((String) b[8]);
            book.setDescription((String) b[9]);
            book.setStatus(1);
            bookMapper.insert(book);
        }
        log.info("已为悦读书屋创建 15 本测试图书");
    }
}
