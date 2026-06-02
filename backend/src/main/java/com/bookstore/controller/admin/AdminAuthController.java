package com.bookstore.controller.admin;

import com.bookstore.common.Result;
import com.bookstore.dto.LoginDTO;
import com.bookstore.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员认证接口
 */
@Tag(name = "管理员认证", description = "管理员登录")
@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.adminLogin(dto));
    }
}
