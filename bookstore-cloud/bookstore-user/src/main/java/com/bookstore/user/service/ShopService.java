package com.bookstore.user.service;

import com.bookstore.entity.Shop;

public interface ShopService {
    Shop getShopInfo(Long userId);
    void updateShopInfo(Long userId, Shop shop);
    void registerShop(Long userId, Shop shop);
}
