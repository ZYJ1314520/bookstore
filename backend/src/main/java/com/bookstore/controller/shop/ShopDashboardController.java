package com.bookstore.controller.shop;

import com.bookstore.common.Result;
import com.bookstore.entity.Shop;
import com.bookstore.service.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商家端工作台接口
 */
@Tag(name = "商家端工作台", description = "统计数据")
@RestController
@RequestMapping("/api/shop/dashboard")
public class ShopDashboardController {

    @Autowired
    private ShopService shopService;

    // 注入必要的 Mapper
    @Autowired
    private com.bookstore.mapper.OrderMapper orderMapper;

    @Autowired
    private com.bookstore.mapper.OrderItemMapper orderItemMapper;

    @Autowired
    private com.bookstore.mapper.BookMapper bookMapper;

    @Operation(summary = "获取统计数据")
    @GetMapping
    public Result<?> getDashboard(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Shop shop = shopService.getShopInfo(userId);
        if (shop == null) {
            return Result.error("店铺不存在");
        }

        Map<String, Object> stats = new HashMap<>();

        // 查询该店铺的所有订单ID
        List<com.bookstore.entity.OrderItem> shopItems = orderItemMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.bookstore.entity.OrderItem>()
                        .eq(com.bookstore.entity.OrderItem::getShopId, shop.getId())
        );
        List<Long> orderIds = shopItems.stream()
                .map(com.bookstore.entity.OrderItem::getOrderId)
                .distinct()
                .collect(Collectors.toList());

        if (orderIds.isEmpty()) {
            stats.put("todaySales", 0);
            stats.put("todayOrders", 0);
            stats.put("pendingOrders", 0);
            stats.put("totalBooks", bookMapper.selectCount(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.bookstore.entity.Book>()
                            .eq(com.bookstore.entity.Book::getShopId, shop.getId())
                            .eq(com.bookstore.entity.Book::getDeleted, 0)
            ));
            return Result.success(stats);
        }

        // 今日订单
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        List<com.bookstore.entity.Order> todayOrders = orderMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.bookstore.entity.Order>()
                        .in(com.bookstore.entity.Order::getId, orderIds)
                        .ge(com.bookstore.entity.Order::getCreateTime, todayStart)
                        .eq(com.bookstore.entity.Order::getDeleted, 0)
        );

        // 今日销售额（已付款的订单）
        List<Long> todayPaidOrderIds = todayOrders.stream()
                .filter(o -> o.getStatus() >= 1 && o.getStatus() <= 3)
                .map(com.bookstore.entity.Order::getId)
                .collect(Collectors.toList());
        double todaySales = 0;
        if (!todayPaidOrderIds.isEmpty()) {
            todaySales = todayOrders.stream()
                    .filter(o -> o.getStatus() >= 1 && o.getStatus() <= 3)
                    .mapToDouble(o -> o.getTotalAmount().doubleValue())
                    .sum();
        }

        // 待发货订单数
        long pendingOrders = orderMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.bookstore.entity.Order>()
                        .in(com.bookstore.entity.Order::getId, orderIds)
                        .eq(com.bookstore.entity.Order::getStatus, 1)
                        .eq(com.bookstore.entity.Order::getDeleted, 0)
        );

        // 图书总数
        long totalBooks = bookMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.bookstore.entity.Book>()
                        .eq(com.bookstore.entity.Book::getShopId, shop.getId())
                        .eq(com.bookstore.entity.Book::getDeleted, 0)
        );

        stats.put("todaySales", todaySales);
        stats.put("todayOrders", todayOrders.size());
        stats.put("pendingOrders", pendingOrders);
        stats.put("totalBooks", totalBooks);

        return Result.success(stats);
    }

    @Operation(summary = "获取销售趋势")
    @GetMapping("/chart")
    public Result<?> getSalesTrend(HttpServletRequest request,
                                   @RequestParam(defaultValue = "7") Integer days) {
        Long userId = (Long) request.getAttribute("userId");
        Shop shop = shopService.getShopInfo(userId);
        if (shop == null) {
            return Result.error("店铺不存在");
        }

        // 查询该店铺的所有订单ID
        List<com.bookstore.entity.OrderItem> shopItems = orderItemMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.bookstore.entity.OrderItem>()
                        .eq(com.bookstore.entity.OrderItem::getShopId, shop.getId())
        );
        List<Long> orderIds = shopItems.stream()
                .map(com.bookstore.entity.OrderItem::getOrderId)
                .distinct()
                .collect(Collectors.toList());

        List<Map<String, Object>> trend = new ArrayList<>();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);

            double sales = 0;
            int orderCount = 0;

            if (!orderIds.isEmpty()) {
                List<com.bookstore.entity.Order> dayOrders = orderMapper.selectList(
                        new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.bookstore.entity.Order>()
                                .in(com.bookstore.entity.Order::getId, orderIds)
                                .between(com.bookstore.entity.Order::getPayTime, start, end)
                                .in(com.bookstore.entity.Order::getStatus, 1, 2, 3)
                                .eq(com.bookstore.entity.Order::getDeleted, 0)
                );
                sales = dayOrders.stream()
                        .mapToDouble(o -> o.getTotalAmount().doubleValue())
                        .sum();
                orderCount = dayOrders.size();
            }

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("sales", sales);
            item.put("orders", orderCount);
            trend.add(item);
        }

        return Result.success(trend);
    }
}
