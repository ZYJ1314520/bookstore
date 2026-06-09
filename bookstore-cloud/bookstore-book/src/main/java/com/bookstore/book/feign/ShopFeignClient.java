package com.bookstore.book.feign;

import com.bookstore.entity.Shop;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "bookstore-user")
public interface ShopFeignClient {

    @GetMapping("/internal/shop/{id}")
    Shop getShopById(@PathVariable("id") Long id);

    @GetMapping("/internal/shop/user/{userId}")
    Shop getShopByUserId(@PathVariable("userId") Long userId);
}
