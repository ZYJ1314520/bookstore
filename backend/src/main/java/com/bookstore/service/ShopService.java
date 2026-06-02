package com.bookstore.service;

import com.bookstore.entity.Shop;

/**
 * 店铺服务接口
 */
public interface ShopService {

    /**
     * 获取店铺信息
     */
    Shop getShopInfo(Long userId);

    /**
     * 更新店铺信息
     */
    void updateShopInfo(Long userId, Shop shop);

    /**
     * 商家注册
     */
    void registerShop(Long userId, Shop shop);
}
