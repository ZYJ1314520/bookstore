package com.bookstore.order.controller;

import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.dto.OrderDTO;
import com.bookstore.entity.Order;
import com.bookstore.order.service.OrderService;
import com.bookstore.utils.UserContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Result<List<Order>> create(@Valid @RequestBody OrderDTO dto) {
        return Result.success(orderService.createOrder(UserContext.getUserId(), dto));
    }

    @GetMapping
    public Result<PageResult<Order>> getList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(orderService.getUserOrderList(UserContext.getUserId(), status, page, size));
    }

    @GetMapping("/{id}")
    public Result<Order> getDetail(@PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(UserContext.getUserId(), id));
    }

    @PutMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id) {
        orderService.payOrder(UserContext.getUserId(), id);
        return Result.success();
    }

    @PutMapping("/{id}/receive")
    public Result<Void> receive(@PathVariable Long id) {
        orderService.receiveOrder(UserContext.getUserId(), id);
        return Result.success();
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        orderService.cancelOrder(UserContext.getUserId(), id);
        return Result.success();
    }

    @GetMapping("/{id}/items")
    public Result<?> getItems(@PathVariable Long id) {
        return Result.success(orderService.getOrderItems(UserContext.getUserId(), id));
    }
}
