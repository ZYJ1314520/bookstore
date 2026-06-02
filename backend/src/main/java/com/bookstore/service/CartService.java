package com.bookstore.service;

import com.bookstore.entity.Cart;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {

    /**
     * 获取购物车列表
     */
    List<Cart> getCartList(Long userId);

    /**
     * 添加购物车
     */
    void addToCart(Long userId, Long bookId, Integer quantity);

    /**
     * 更新购物车数量
     */
    void updateQuantity(Long userId, Long cartId, Integer quantity);

    /**
     * 删除购物车项
     */
    void deleteCartItem(Long userId, Long cartId);

    /**
     * 清空购物车
     */
    void clearCart(Long userId);
}
