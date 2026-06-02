package com.bookstore.controller.admin;

import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.entity.Order;
import com.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员端订单管理接口
 */
@Tag(name = "管理员端订单管理", description = "订单管理")
@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "订单列表")
    @GetMapping
    public Result<PageResult<Order>> getOrderList(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(orderService.getAllOrderList(userId, status, page, size));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public Result<Order> getOrderDetail(@PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(null, id));
    }
}
