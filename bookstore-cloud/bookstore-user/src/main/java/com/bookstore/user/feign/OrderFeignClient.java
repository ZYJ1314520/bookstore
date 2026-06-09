package com.bookstore.user.feign;

import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "bookstore-order")
public interface OrderFeignClient {

    @GetMapping("/internal/order/shop/{shopId}")
    List<Order> getOrdersByShopId(@PathVariable("shopId") Long shopId);

    @GetMapping("/internal/order/items/{orderId}")
    List<OrderItem> getOrderItems(@PathVariable("orderId") Long orderId);

    @GetMapping("/internal/order/stats/sales-trend")
    List<Map<String, Object>> getSalesTrend(@RequestParam("days") Integer days);
}
