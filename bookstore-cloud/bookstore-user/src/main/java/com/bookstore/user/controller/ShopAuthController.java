package com.bookstore.user.controller;

import com.bookstore.common.Result;
import com.bookstore.dto.LoginDTO;
import com.bookstore.dto.ShopRegisterDTO;
import com.bookstore.user.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shop/auth")
public class ShopAuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.shopLogin(dto));
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody ShopRegisterDTO dto) {
        authService.shopRegister(dto);
        return Result.success();
    }
}
