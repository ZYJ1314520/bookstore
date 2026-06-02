package com.bookstore.controller.shop;

import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.entity.Order;
import com.bookstore.entity.Shop;
import com.bookstore.service.OrderService;
import com.bookstore.service.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商家端订单接口
 */
@Tag(name = "商家端订单", description = "订单管理")
@RestController
@RequestMapping("/api/shop/orders")
public class ShopOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ShopService shopService;

    @Operation(summary = "订单列表")
    @GetMapping
    public Result<PageResult<Order>> getOrderList(
            HttpServletRequest request,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) request.getAttribute("userId");
        Shop shop = shopService.getShopInfo(userId);
        return Result.success(orderService.getShopOrderList(shop.getId(), status, page, size));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public Result<Order> getOrderDetail(@PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(null, id));
    }

    @Operation(summary = "订单发货")
    @PutMapping("/{id}/ship")
    public Result<?> shipOrder(HttpServletRequest request,
                               @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        Shop shop = shopService.getShopInfo(userId);
        orderService.shipOrder(shop.getId(), id);
        return Result.success("发货成功");
    }
}
