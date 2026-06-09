package com.bookstore.user.controller;

import com.bookstore.common.BusinessException;
import com.bookstore.common.Result;
import com.bookstore.entity.Cart;
import com.bookstore.user.service.CartService;
import com.bookstore.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    private Long requireUserId() {
        Long userId = UserContext.getUserId();
        log.info("CartController - userId from header: {}", userId);
        if (userId == null) throw new BusinessException("请先登录");
        return userId;
    }

    @GetMapping
    public Result<List<Cart>> getList() {
        return Result.success(cartService.getCartList(requireUserId()));
    }

    @PostMapping
    public Result<Void> add(@RequestParam Long bookId,
                            @RequestParam(defaultValue = "1") Integer quantity) {
        cartService.addToCart(requireUserId(), bookId, quantity);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateQuantity(@PathVariable Long id,
                                       @RequestParam Integer quantity) {
        cartService.updateQuantity(requireUserId(), id, quantity);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        cartService.deleteCartItem(requireUserId(), id);
        return Result.success();
    }
}
