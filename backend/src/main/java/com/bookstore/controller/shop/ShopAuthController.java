package com.bookstore.controller.shop;

import com.bookstore.common.Result;
import com.bookstore.dto.LoginDTO;
import com.bookstore.dto.ShopRegisterDTO;
import com.bookstore.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商家认证接口
 */
@Tag(name = "商家认证", description = "商家登录注册")
@RestController
@RequestMapping("/api/shop/auth")
public class ShopAuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "商家登录")
    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.shopLogin(dto));
    }

    @Operation(summary = "商家注册")
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody ShopRegisterDTO dto) {
        authService.shopRegister(dto);
        return Result.success("注册成功，等待审核");
    }
}
