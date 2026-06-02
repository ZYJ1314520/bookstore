package com.bookstore.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.common.BusinessException;
import com.bookstore.common.PageResult;
import com.bookstore.entity.*;
import com.bookstore.mapper.*;
import com.bookstore.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员服务实现
 */
@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public PageResult<Shop> getShopList(Integer status, Integer page, Integer size) {
        LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Shop::getStatus, status);
        }

        wrapper.orderByDesc(Shop::getCreateTime);

        List<Shop> shops = shopMapper.selectList(wrapper);
        int total = shops.size();

        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<Shop> pageShops = start < total ? shops.subList(start, end) : List.of();

        return new PageResult<>(pageShops, (long) total, page, size);
    }

    @Override
    public void auditShop(Long shopId, Integer status, String remark) {
        Shop shop = shopMapper.selectById(shopId);
        if (shop == null) {
            throw new BusinessException("商家不存在");
        }

        shop.setStatus(status);
        shop.setAuditRemark(remark);
        shop.setAuditTime(LocalDateTime.now());
        shopMapper.updateById(shop);
    }

    @Override
    public PageResult<User> getUserList(String keyword, Integer page, Integer size) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword);
        }

        wrapper.orderByDesc(User::getCreateTime);

        List<User> users = userMapper.selectList(wrapper);
        int total = users.size();

        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<User> pageUsers = start < total ? users.subList(start, end) : List.of();

        return new PageResult<>(pageUsers, (long) total, page, size);
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public Object getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // 用户统计
        long totalUsers = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getDeleted, 0));
        long todayUsers = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .ge(User::getCreateTime, LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                        .eq(User::getDeleted, 0)
        );

        // 商家统计
        long totalShops = shopMapper.selectCount(new LambdaQueryWrapper<>());
        long pendingShops = shopMapper.selectCount(
                new LambdaQueryWrapper<Shop>().eq(Shop::getStatus, 0)
        );

        // 图书统计
        long totalBooks = bookMapper.selectCount(
                new LambdaQueryWrapper<Book>().eq(Book::getDeleted, 0)
        );

        // 订单统计
        long totalOrders = orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getDeleted, 0));
        long todayOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                        .ge(Order::getCreateTime, LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                        .eq(Order::getDeleted, 0)
        );

        // 今日销售额
        List<Order> todayOrderList = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .ge(Order::getPayTime, LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                        .in(Order::getStatus, 1, 2, 3)
                        .eq(Order::getDeleted, 0)
        );
        double todaySales = todayOrderList.stream()
                .mapToDouble(order -> order.getTotalAmount().doubleValue())
                .sum();

        stats.put("totalUsers", totalUsers);
        stats.put("todayUsers", todayUsers);
        stats.put("totalShops", totalShops);
        stats.put("pendingShops", pendingShops);
        stats.put("totalBooks", totalBooks);
        stats.put("totalOrders", totalOrders);
        stats.put("todayOrders", todayOrders);
        stats.put("todaySales", todaySales);

        return stats;
    }

    @Override
    public Object getSalesTrend(Integer days) {
        List<Map<String, Object>> trend = new java.util.ArrayList<>();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);

            List<Order> dayOrders = orderMapper.selectList(
                    new LambdaQueryWrapper<Order>()
                            .between(Order::getPayTime, start, end)
                            .in(Order::getStatus, 1, 2, 3)
                            .eq(Order::getDeleted, 0)
            );

            double sales = dayOrders.stream()
                    .mapToDouble(order -> order.getTotalAmount().doubleValue())
                    .sum();

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("sales", sales);
            item.put("orders", dayOrders.size());
            trend.add(item);
        }

        return trend;
    }

    @Override
    public Object getHotBooks(Integer limit) {
        List<Book> books = bookMapper.selectList(
                new LambdaQueryWrapper<Book>()
                        .eq(Book::getDeleted, 0)
                        .orderByDesc(Book::getSales)
                        .last("LIMIT " + limit)
        );
        return books;
    }

    @Override
    public Object getShopRank(Integer limit) {
        // 查询所有订单
        List<Order> allOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .in(Order::getStatus, 1, 2, 3)
                        .eq(Order::getDeleted, 0)
        );

        // 按商家ID统计销售额
        Map<Long, Double> shopSales = new java.util.HashMap<>();
        Map<Long, String> shopNames = new java.util.HashMap<>();

        for (Order order : allOrders) {
            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>()
                            .eq(OrderItem::getOrderId, order.getId())
            );
            for (OrderItem item : items) {
                shopSales.merge(item.getShopId(),
                        order.getTotalAmount().doubleValue() / items.size(),
                        Double::sum);
            }
        }

        // 获取店铺名称
        for (Long shopId : shopSales.keySet()) {
            Shop shop = shopMapper.selectById(shopId);
            if (shop != null) {
                shopNames.put(shopId, shop.getShopName());
            }
        }

        // 排序并截取
        return shopSales.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("shopId", e.getKey());
                    item.put("shopName", shopNames.getOrDefault(e.getKey(), "未知"));
                    item.put("sales", e.getValue());
                    return item;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Object getCategorySales() {
        // 查询所有已完成订单的商品
        List<Order> allOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .in(Order::getStatus, 1, 2, 3)
                        .eq(Order::getDeleted, 0)
        );

        Map<Long, Double> categorySales = new java.util.HashMap<>();
        Map<Long, String> categoryNames = new java.util.HashMap<>();

        for (Order order : allOrders) {
            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>()
                            .eq(OrderItem::getOrderId, order.getId())
            );
            for (OrderItem item : items) {
                Book book = bookMapper.selectById(item.getBookId());
                if (book != null && book.getCategoryId() != null) {
                    double amount = order.getTotalAmount().doubleValue() / items.size();
                    categorySales.merge(book.getCategoryId(), amount, Double::sum);
                }
            }
        }

        // 获取分类名称
        for (Long catId : categorySales.keySet()) {
            Category cat = categoryMapper.selectById(catId);
            if (cat != null) {
                categoryNames.put(catId, cat.getName());
            }
        }

        return categorySales.entrySet().stream()
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("categoryId", e.getKey());
                    item.put("categoryName", categoryNames.getOrDefault(e.getKey(), "未分类"));
                    item.put("sales", e.getValue());
                    return item;
                })
                .collect(java.util.stream.Collectors.toList());
    }
}
