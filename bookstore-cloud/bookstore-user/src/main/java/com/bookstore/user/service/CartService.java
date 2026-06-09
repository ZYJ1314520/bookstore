package com.bookstore.user.service;

import com.bookstore.entity.Cart;
import java.util.List;

public interface CartService {
    List<Cart> getCartList(Long userId);
    void addToCart(Long userId, Long bookId, Integer quantity);
    void updateQuantity(Long userId, Long cartId, Integer quantity);
    void deleteCartItem(Long userId, Long cartId);
    void clearCart(Long userId);
}
