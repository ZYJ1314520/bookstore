package com.bookstore.service;

import com.bookstore.common.PageResult;
import com.bookstore.entity.Shop;
import com.bookstore.entity.User;

/**
 * 管理员服务接口
 */
public interface AdminService {

    /**
     * 获取商家列表
     */
    PageResult<Shop> getShopList(Integer status, Integer page, Integer size);

    /**
     * 审核商家
     */
    void auditShop(Long shopId, Integer status, String remark);

    /**
     * 获取用户列表
     */
    PageResult<User> getUserList(String keyword, Integer page, Integer size);

    /**
     * 更新用户状态
     */
    void updateUserStatus(Long userId, Integer status);

    /**
     * 获取统计数据
     */
    Object getDashboardStats();

    /**
     * 获取销售趋势
     */
    Object getSalesTrend(Integer days);

    /**
     * 热销图书排行
     */
    Object getHotBooks(Integer limit);

    /**
     * 商家销售排行
     */
    Object getShopRank(Integer limit);

    /**
     * 分类销售占比
     */
    Object getCategorySales();
}
