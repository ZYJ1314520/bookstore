package com.bookstore.user.controller;

import com.bookstore.common.Result;
import com.bookstore.entity.Shop;
import com.bookstore.user.service.ShopService;
import com.bookstore.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop/profile")
public class ShopProfileController {

    @Autowired
    private ShopService shopService;

    @GetMapping
    public Result<Shop> getShopInfo() {
        return Result.success(shopService.getShopInfo(UserContext.getUserId()));
    }

    @PutMapping
    public Result<Void> updateShopInfo(@RequestBody Shop shop) {
        shopService.updateShopInfo(UserContext.getUserId(), shop);
        return Result.success();
    }
}
