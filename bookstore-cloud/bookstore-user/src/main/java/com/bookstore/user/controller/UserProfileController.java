package com.bookstore.user.controller;

import com.bookstore.common.Result;
import com.bookstore.dto.PasswordDTO;
import com.bookstore.dto.ProfileDTO;
import com.bookstore.entity.User;
import com.bookstore.user.service.UserService;
import com.bookstore.utils.UserContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public Result<User> getProfile() {
        return Result.success(userService.getUserInfo(UserContext.getUserId()));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@Valid @RequestBody ProfileDTO dto) {
        userService.updateProfile(UserContext.getUserId(), dto);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody PasswordDTO dto) {
        userService.updatePassword(UserContext.getUserId(), dto);
        return Result.success();
    }
}
