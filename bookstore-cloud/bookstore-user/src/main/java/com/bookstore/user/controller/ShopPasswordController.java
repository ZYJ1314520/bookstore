package com.bookstore.user.controller;

import com.bookstore.common.Result;
import com.bookstore.dto.PasswordDTO;
import com.bookstore.user.service.UserService;
import com.bookstore.utils.UserContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop/password")
public class ShopPasswordController {

    @Autowired
    private UserService userService;

    @PutMapping
    public Result<Void> updatePassword(@Valid @RequestBody PasswordDTO dto) {
        userService.updatePassword(UserContext.getUserId(), dto);
        return Result.success();
    }
}
