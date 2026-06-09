package com.bookstore.order.controller;

import com.bookstore.common.BusinessException;
import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.entity.Order;
import com.bookstore.entity.Shop;
import com.bookstore.order.feign.UserFeignClient;
import com.bookstore.order.service.OrderService;
import com.bookstore.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop/orders")
public class ShopOrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserFeignClient userFeignClient;

    private Shop getShop() {
        Shop shop = userFeignClient.getShopByUserId(UserContext.getUserId());
        if (shop == null) throw new BusinessException("店铺不存在");
        return shop;
    }

    @GetMapping
    public Result<PageResult<Order>> getList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(orderService.getShopOrderList(getShop().getId(), status, page, size));
    }

    @GetMapping("/{id}")
    public Result<Order> getDetail(@PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(null, id));
    }

    @PutMapping("/{id}/ship")
    public Result<Void> ship(@PathVariable Long id) {
        orderService.shipOrder(getShop().getId(), id);
        return Result.success();
    }
}
