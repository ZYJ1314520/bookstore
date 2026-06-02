package com.bookstore.controller.user;

import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.dto.OrderDTO;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import com.bookstore.service.OrderService;
import com.bookstore.mapper.OrderItemMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端订单接口
 */
@Tag(name = "用户端订单", description = "订单管理")
@RestController
@RequestMapping("/api/user/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<Order> createOrder(HttpServletRequest request,
                                     @Valid @RequestBody OrderDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(orderService.createOrder(userId, dto));
    }

    @Operation(summary = "我的订单列表")
    @GetMapping
    public Result<PageResult<Order>> getOrderList(
            HttpServletRequest request,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(orderService.getUserOrderList(userId, status, page, size));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public Result<Order> getOrderDetail(HttpServletRequest request,
                                        @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(orderService.getOrderDetail(userId, id));
    }

    @Operation(summary = "订单商品列表")
    @GetMapping("/{id}/items")
    public Result<List<OrderItem>> getOrderItems(@PathVariable Long id) {
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, id)
        );
        return Result.success(items);
    }

    @Operation(summary = "模拟支付")
    @PutMapping("/{id}/pay")
    public Result<?> payOrder(HttpServletRequest request,
                              @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.payOrder(userId, id);
        return Result.success("支付成功");
    }

    @Operation(summary = "确认收货")
    @PutMapping("/{id}/receive")
    public Result<?> receiveOrder(HttpServletRequest request,
                                  @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.receiveOrder(userId, id);
        return Result.success("已确认收货");
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{id}/cancel")
    public Result<?> cancelOrder(HttpServletRequest request,
                                 @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.cancelOrder(userId, id);
        return Result.success("订单已取消");
    }
}
