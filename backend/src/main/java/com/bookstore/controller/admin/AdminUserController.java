package com.bookstore.controller.admin;

import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.entity.User;
import com.bookstore.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员端用户管理接口
 */
@Tag(name = "管理员端用户管理", description = "用户管理")
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private AdminService adminService;

    @Operation(summary = "用户列表")
    @GetMapping
    public Result<PageResult<User>> getUserList(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(adminService.getUserList(keyword, page, size));
    }

    @Operation(summary = "更新用户状态")
    @PutMapping("/{id}/status")
    public Result<?> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        adminService.updateUserStatus(id, status);
        return Result.success("状态已更新");
    }
}
