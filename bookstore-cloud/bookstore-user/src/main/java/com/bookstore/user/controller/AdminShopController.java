package com.bookstore.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.entity.Shop;
import com.bookstore.user.mapper.ShopMapper;
import com.bookstore.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/shops")
public class AdminShopController {

    @Autowired
    private ShopMapper shopMapper;

    @GetMapping
    public Result<PageResult<Shop>> getList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(Shop::getStatus, status);
        wrapper.orderByDesc(Shop::getCreateTime);
        List<Shop> shops = shopMapper.selectList(wrapper);
        int total = shops.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<Shop> pageShops = start < total ? shops.subList(start, end) : List.of();
        return Result.success(new PageResult<>(pageShops, (long) total, page, size));
    }

    @PutMapping("/{id}/audit")
    public Result<Void> audit(@PathVariable Long id, @RequestParam Integer status, @RequestParam(required = false) String remark) {
        Shop shop = shopMapper.selectById(id);
        if (shop == null) return Result.error("商家不存在");
        shop.setStatus(status);
        shop.setAuditRemark(remark);
        shop.setAuditTime(LocalDateTime.now());
        shopMapper.updateById(shop);
        return Result.success();
    }
}
