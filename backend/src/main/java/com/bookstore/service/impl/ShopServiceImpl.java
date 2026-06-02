package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.common.BusinessException;
import com.bookstore.entity.Shop;
import com.bookstore.mapper.ShopMapper;
import com.bookstore.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 店铺服务实现
 */
@Slf4j
@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopMapper shopMapper;

    @Override
    public Shop getShopInfo(Long userId) {
        return shopMapper.selectOne(
                new LambdaQueryWrapper<Shop>().eq(Shop::getUserId, userId)
        );
    }

    @Override
    public void updateShopInfo(Long userId, Shop shop) {
        Shop existing = getShopInfo(userId);
        if (existing == null) {
            throw new BusinessException("店铺不存在");
        }

        shop.setId(existing.getId());
        shop.setUserId(userId);
        shopMapper.updateById(shop);
    }

    @Override
    public void registerShop(Long userId, Shop shop) {
        // 检查是否已注册
        Shop existing = getShopInfo(userId);
        if (existing != null) {
            throw new BusinessException("您已注册过店铺");
        }

        shop.setUserId(userId);
        shop.setStatus(0); // 待审核
        shopMapper.insert(shop);
    }
}
