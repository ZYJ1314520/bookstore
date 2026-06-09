package com.bookstore.user.config;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.entity.Admin;
import com.bookstore.entity.Shop;
import com.bookstore.entity.User;
import com.bookstore.user.mapper.AdminMapper;
import com.bookstore.user.mapper.ShopMapper;
import com.bookstore.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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

    @Override
    public void run(String... args) {
        initAdmin();
        initTestUsers();
        initTestShop();
        log.info("===== 用户服务数据初始化完成 =====");
    }

    private void initAdmin() {
        Long count = adminMapper.selectCount(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, "admin"));
        if (count == 0) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(BCrypt.hashpw("admin123"));
            admin.setCreateTime(LocalDateTime.now());
            adminMapper.insert(admin);
            log.info("已创建管理员账号: admin / admin123");
        }
    }

    private void initTestUsers() {
        String[][] users = {
                {"user1", "123456", "测试用户1", "13800138001"},
                {"user2", "123456", "测试用户2", "13800138002"}
        };
        for (String[] u : users) {
            Long count = userMapper.selectCount(
                    new LambdaQueryWrapper<User>().eq(User::getUsername, u[0]));
            if (count == 0) {
                User user = new User();
                user.setUsername(u[0]);
                user.setPassword(BCrypt.hashpw(u[1]));
                user.setNickname(u[2]);
                user.setPhone(u[3]);
                user.setStatus(1);
                user.setCreateTime(LocalDateTime.now());
                user.setUpdateTime(LocalDateTime.now());
                userMapper.insert(user);
                log.info("已创建用户账号: {} / {}", u[0], u[1]);
            }
        }
    }

    private void initTestShop() {
        String shopUsername = "shop1";
        String shopPassword = "123456";

        Long userCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, shopUsername));
        Long userId;
        if (userCount == 0) {
            User shopUser = new User();
            shopUser.setUsername(shopUsername);
            shopUser.setPassword(BCrypt.hashpw(shopPassword));
            shopUser.setNickname("商家1");
            shopUser.setPhone("13900139001");
            shopUser.setStatus(1);
            shopUser.setCreateTime(LocalDateTime.now());
            shopUser.setUpdateTime(LocalDateTime.now());
            userMapper.insert(shopUser);
            userId = shopUser.getId();
            log.info("已创建商家账号: {} / {}", shopUsername, shopPassword);
        } else {
            userId = userMapper.selectOne(
                    new LambdaQueryWrapper<User>().eq(User::getUsername, shopUsername)).getId();
        }

        Long shopCount = shopMapper.selectCount(
                new LambdaQueryWrapper<Shop>().eq(Shop::getUserId, userId));
        if (shopCount == 0) {
            Shop shop = new Shop();
            shop.setUserId(userId);
            shop.setShopName("悦读书屋");
            shop.setDescription("专注精品图书，为您推荐好书");
            shop.setContactPhone("13900139001");
            shop.setStatus(1);
            shop.setCreateTime(LocalDateTime.now());
            shop.setUpdateTime(LocalDateTime.now());
            shopMapper.insert(shop);
            log.info("已创建测试店铺: 悦读书屋");
        }
    }
}
