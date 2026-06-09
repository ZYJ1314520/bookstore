package com.bookstore.order.feign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import com.bookstore.entity.Review;
import com.bookstore.order.mapper.OrderItemMapper;
import com.bookstore.order.mapper.OrderMapper;
import com.bookstore.order.mapper.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * 内部Feign接口 - 供其他微服务调用
 */
@RestController
@RequestMapping("/internal/order")
public class OrderInternalController {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ReviewMapper reviewMapper;

    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUserId(@PathVariable Long userId, @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>().eq(Order::getUserId, userId);
        if (status != null) wrapper.eq(Order::getStatus, status);
        wrapper.orderByDesc(Order::getId);
        return orderMapper.selectList(wrapper);
    }

    @GetMapping("/shop/{shopId}")
    public List<Order> getOrdersByShopId(@PathVariable Long shopId) {
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getShopId, shopId));
        List<Long> orderIds = items.stream().map(OrderItem::getOrderId).distinct().toList();
        if (orderIds.isEmpty()) return List.of();
        return orderMapper.selectBatchIds(orderIds);
    }

    @GetMapping("/items/{orderId}")
    public List<OrderItem> getOrderItems(@PathVariable Long orderId) {
        return orderMapper.selectById(orderId) != null ?
                orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId)) :
                List.of();
    }

    @GetMapping("/reviews/book/{bookId}")
    public List<Review> getBookReviews(@PathVariable Long bookId,
                                       @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer size) {
        return reviewMapper.selectList(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getBookId, bookId)
                        .orderByDesc(Review::getCreateTime)
                        .last("LIMIT " + size + " OFFSET " + (page - 1) * size));
    }

    @GetMapping("/reviews/book/{bookId}/count")
    public long getBookReviewCount(@PathVariable Long bookId) {
        return reviewMapper.selectCount(
                new LambdaQueryWrapper<Review>().eq(Review::getBookId, bookId));
    }

    @GetMapping("/stats/sales-trend")
    public List<Map<String, Object>> getSalesTrend(@RequestParam(defaultValue = "7") Integer days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);
            List<Order> dayOrders = orderMapper.selectList(
                    new LambdaQueryWrapper<Order>()
                            .between(Order::getPayTime, start, end)
                            .in(Order::getStatus, 1, 2, 3).eq(Order::getDeleted, 0));
            double sales = dayOrders.stream().mapToDouble(o -> o.getTotalAmount().doubleValue()).sum();
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("sales", sales);
            item.put("orders", dayOrders.size());
            trend.add(item);
        }
        return trend;
    }

    @GetMapping("/stats/shop-rank")
    public List<Map<String, Object>> getShopRank(@RequestParam(defaultValue = "10") Integer limit) {
        List<Order> allOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .in(Order::getStatus, 1, 2, 3).eq(Order::getDeleted, 0));
        Map<Long, Double> shopSales = new HashMap<>();
        for (Order order : allOrders) {
            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
            for (OrderItem item : items) {
                shopSales.merge(item.getShopId(), order.getTotalAmount().doubleValue() / items.size(), Double::sum);
            }
        }
        return shopSales.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("shopId", e.getKey());
                    m.put("sales", e.getValue());
                    return m;
                }).toList();
    }

    @GetMapping("/stats/category-sales")
    public List<Map<String, Object>> getCategorySales() {
        List<Order> allOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .in(Order::getStatus, 1, 2, 3).eq(Order::getDeleted, 0));
        Map<Long, Double> categorySales = new HashMap<>();
        for (Order order : allOrders) {
            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
            for (OrderItem item : items) {
                categorySales.merge(item.getShopId(), order.getTotalAmount().doubleValue() / items.size(), Double::sum);
            }
        }
        return categorySales.entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("categoryId", e.getKey());
                    m.put("sales", e.getValue());
                    return m;
                }).toList();
    }

    @GetMapping("/stats/total")
    public Map<String, Object> getOrderStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalOrders = orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getDeleted, 0));
        long todayOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                        .ge(Order::getCreateTime, LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                        .eq(Order::getDeleted, 0));
        List<Order> todayOrderList = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .ge(Order::getPayTime, LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                        .in(Order::getStatus, 1, 2, 3).eq(Order::getDeleted, 0));
        double todaySales = todayOrderList.stream().mapToDouble(o -> o.getTotalAmount().doubleValue()).sum();
        stats.put("totalOrders", totalOrders);
        stats.put("todayOrders", todayOrders);
        stats.put("todaySales", todaySales);
        return stats;
    }
}
