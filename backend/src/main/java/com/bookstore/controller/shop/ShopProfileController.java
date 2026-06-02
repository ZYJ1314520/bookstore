package com.bookstore.controller.shop;

import com.bookstore.common.Result;
import com.bookstore.entity.Shop;
import com.bookstore.service.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商家店铺设置接口
 */
@Tag(name = "商家店铺设置", description = "店铺信息管理")
@RestController
@RequestMapping("/api/shop/profile")
public class ShopProfileController {

    @Autowired
    private ShopService shopService;

    @Operation(summary = "获取店铺信息")
    @GetMapping
    public Result<Shop> getShopInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(shopService.getShopInfo(userId));
    }

    @Operation(summary = "更新店铺信息")
    @PutMapping
    public Result<?> updateShopInfo(HttpServletRequest request,
                                    @RequestBody Shop shop) {
        Long userId = (Long) request.getAttribute("userId");
        shopService.updateShopInfo(userId, shop);
        return Result.success("更新成功");
    }
}
