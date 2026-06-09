package com.bookstore.admin.feign;

import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.entity.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "bookstore-order")
public interface OrderServiceClient {

    @GetMapping("/internal/order/stats/total")
    Map<String, Object> getOrderStats();

    @GetMapping("/internal/order/stats/sales-trend")
    List<Map<String, Object>> getSalesTrend(@RequestParam(defaultValue = "7") Integer days);

    @GetMapping("/internal/order/stats/shop-rank")
    List<Map<String, Object>> getShopRank(@RequestParam(defaultValue = "10") Integer limit);

    @GetMapping("/internal/order/stats/category-sales")
    List<Map<String, Object>> getCategorySales();
}
