package com.bookstore.controller.shop;

import com.bookstore.common.Result;
import com.bookstore.dto.PasswordDTO;
import com.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商家端密码修改接口
 */
@Tag(name = "商家端密码修改", description = "修改商家账号密码")
@RestController
@RequestMapping("/api/shop")
public class ShopPasswordController {

    @Autowired
    private UserService userService;

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<?> updatePassword(HttpServletRequest request,
                                    @Valid @RequestBody PasswordDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updatePassword(userId, dto);
        return Result.success("密码修改成功");
    }
}
