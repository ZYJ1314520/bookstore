package com.bookstore.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.common.BusinessException;
import com.bookstore.entity.Book;
import com.bookstore.entity.Cart;
import com.bookstore.user.feign.BookFeignClient;
import com.bookstore.user.mapper.CartMapper;
import com.bookstore.user.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private BookFeignClient bookFeignClient;

    @Override
    public List<Cart> getCartList(Long userId) {
        return cartMapper.selectList(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getUserId, userId)
                        .orderByDesc(Cart::getCreateTime));
    }

    @Override
    public void addToCart(Long userId, Long bookId, Integer quantity) {
        // 通过Feign检查图书
        Book book = bookFeignClient.getBookById(bookId);
        if (book == null || book.getStatus() != 1) throw new BusinessException("图书不存在或已下架");
        if (book.getStock() < quantity) throw new BusinessException("库存不足");

        Cart existingCart = cartMapper.selectOne(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getUserId, userId)
                        .eq(Cart::getBookId, bookId));
        if (existingCart != null) {
            existingCart.setQuantity(existingCart.getQuantity() + quantity);
            cartMapper.updateById(existingCart);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setBookId(bookId);
            cart.setQuantity(quantity);
            cart.setCreateTime(LocalDateTime.now());
            cart.setUpdateTime(LocalDateTime.now());
            cartMapper.insert(cart);
        }
    }

    @Override
    public void updateQuantity(Long userId, Long cartId, Integer quantity) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) throw new BusinessException("购物车项不存在");
        cart.setQuantity(quantity);
        cartMapper.updateById(cart);
    }

    @Override
    public void deleteCartItem(Long userId, Long cartId) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) throw new BusinessException("购物车项不存在");
        cartMapper.deleteById(cartId);
    }

    @Override
    public void clearCart(Long userId) {
        cartMapper.delete(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId));
    }
}
