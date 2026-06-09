package com.bookstore.admin.controller;

import com.bookstore.admin.feign.BookServiceClient;
import com.bookstore.admin.feign.OrderServiceClient;
import com.bookstore.admin.feign.UserServiceClient;
import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.entity.Book;
import com.bookstore.entity.Shop;
import com.bookstore.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private UserServiceClient userServiceClient;
    @Autowired
    private BookServiceClient bookServiceClient;
    @Autowired
    private OrderServiceClient orderServiceClient;

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 用户统计（通过Feign调用user-service）
        PageResult<User> userPage = userServiceClient.getUserList(null, 1, Integer.MAX_VALUE).getData();
        long totalUsers = userPage != null ? userPage.getTotal() : 0;

        // 商家统计
        PageResult<Shop> shopPage = userServiceClient.getShopList(null, 1, Integer.MAX_VALUE).getData();
        long totalShops = shopPage != null ? shopPage.getTotal() : 0;
        PageResult<Shop> pendingPage = userServiceClient.getShopList(0, 1, Integer.MAX_VALUE).getData();
        long pendingShops = pendingPage != null ? pendingPage.getTotal() : 0;

        // 图书统计
        PageResult<Book> bookPage = bookServiceClient.getBookList(null, null, 1, Integer.MAX_VALUE).getData();
        long totalBooks = bookPage != null ? bookPage.getTotal() : 0;

        // 订单统计（通过Feign调用order-service）
        Map<String, Object> orderStats = orderServiceClient.getOrderStats();

        stats.put("totalUsers", totalUsers);
        stats.put("totalShops", totalShops);
        stats.put("pendingShops", pendingShops);
        stats.put("totalBooks", totalBooks);
        stats.putAll(orderStats);

        return Result.success(stats);
    }

    @GetMapping("/sales-trend")
    public Result<List<Map<String, Object>>> getSalesTrend(@RequestParam(defaultValue = "7") Integer days) {
        return Result.success(orderServiceClient.getSalesTrend(days));
    }

    @GetMapping("/hot-books")
    public Result<List<Book>> getHotBooks(@RequestParam(defaultValue = "10") Integer limit) {
        List<Book> books = bookServiceClient.getHotBooks(limit).getData();
        return Result.success(books != null ? books : List.of());
    }

    @GetMapping("/shop-rank")
    public Result<List<Map<String, Object>>> getShopRank(@RequestParam(defaultValue = "10") Integer limit) {
        List<Map<String, Object>> rank = orderServiceClient.getShopRank(limit);
        for (Map<String, Object> item : rank) {
            Long shopId = Long.valueOf(item.get("shopId").toString());
            try {
                Shop shop = userServiceClient.getShopById(shopId);
                if (shop != null) item.put("shopName", shop.getShopName());
            } catch (Exception e) {
                // ignore
            }
        }
        return Result.success(rank);
    }

    @GetMapping("/category-sales")
    public Result<List<Map<String, Object>>> getCategorySales() {
        return Result.success(orderServiceClient.getCategorySales());
    }
}
