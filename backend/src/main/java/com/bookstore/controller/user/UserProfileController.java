package com.bookstore.controller.user;

import com.bookstore.common.Result;
import com.bookstore.dto.PasswordDTO;
import com.bookstore.dto.ProfileDTO;
import com.bookstore.entity.User;
import com.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端个人中心接口
 */
@Tag(name = "用户端个人中心", description = "修改资料、修改密码")
@RestController
@RequestMapping("/api/user")
public class UserProfileController {

    @Autowired
    private UserService userService;

    @Operation(summary = "获取个人信息")
    @GetMapping("/profile")
    public Result<User> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userService.getUserInfo(userId));
    }

    @Operation(summary = "修改个人信息")
    @PutMapping("/profile")
    public Result<?> updateProfile(HttpServletRequest request,
                                   @RequestBody ProfileDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updateProfile(userId, dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<?> updatePassword(HttpServletRequest request,
                                    @Valid @RequestBody PasswordDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updatePassword(userId, dto);
        return Result.success("密码修改成功");
    }
}
