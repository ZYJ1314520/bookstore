package com.bookstore.order.controller;

import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.entity.Order;
import com.bookstore.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public Result<PageResult<Order>> getList(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(orderService.getAllOrderList(userId, status, page, size));
    }

    @GetMapping("/{id}")
    public Result<Order> getDetail(@PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(null, id));
    }
}
