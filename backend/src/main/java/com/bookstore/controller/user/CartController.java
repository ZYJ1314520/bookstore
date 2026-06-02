package com.bookstore.controller.user;

import com.bookstore.common.Result;
import com.bookstore.entity.Cart;
import com.bookstore.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端购物车接口
 */
@RestController
@RequestMapping("/api/user/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public Result<List<Cart>> getCartList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(cartService.getCartList(userId));
    }

    @PostMapping
    public Result<?> addToCart(HttpServletRequest request,
                               @RequestParam Long bookId,
                               @RequestParam(defaultValue = "1") Integer quantity) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.addToCart(userId, bookId, quantity);
        return Result.success("添加成功");
    }

    @PutMapping("/{id}")
    public Result<?> updateQuantity(HttpServletRequest request,
                                    @PathVariable Long id,
                                    @RequestParam Integer quantity) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.updateQuantity(userId, id, quantity);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteCartItem(HttpServletRequest request,
                                    @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.deleteCartItem(userId, id);
        return Result.success();
    }
}
