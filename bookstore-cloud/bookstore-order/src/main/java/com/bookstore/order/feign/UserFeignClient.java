package com.bookstore.order.feign;

import com.bookstore.entity.Address;
import com.bookstore.entity.Shop;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "bookstore-user")
public interface UserFeignClient {

    @GetMapping("/internal/address/{id}")
    Address getAddressById(@PathVariable("id") Long id, @RequestParam("userId") Long userId);

    @DeleteMapping("/internal/cart/{userId}/items")
    void deleteCartItems(@PathVariable("userId") Long userId, @RequestBody List<Long> bookIds);

    @GetMapping("/internal/shop/user/{userId}")
    Shop getShopByUserId(@PathVariable("userId") Long userId);

    @GetMapping("/internal/shop/{id}")
    Shop getShopById(@PathVariable("id") Long id);
}
