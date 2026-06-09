package com.bookstore.admin.feign;

import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.entity.Shop;
import com.bookstore.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "bookstore-user")
public interface UserServiceClient {

    @GetMapping("/api/admin/shops")
    Result<PageResult<Shop>> getShopList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size);

    @GetMapping("/api/admin/users")
    Result<PageResult<User>> getUserList(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size);

    @GetMapping("/internal/user/{id}")
    User getUserById(@PathVariable("id") Long id);

    @GetMapping("/internal/shop/{id}")
    Shop getShopById(@PathVariable("id") Long id);
}
