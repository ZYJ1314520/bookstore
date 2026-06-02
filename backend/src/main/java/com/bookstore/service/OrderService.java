package com.bookstore.service;

import com.bookstore.common.PageResult;
import com.bookstore.dto.OrderDTO;
import com.bookstore.entity.Order;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 创建订单
     */
    Order createOrder(Long userId, OrderDTO dto);

    /**
     * 获取用户订单列表
     */
    PageResult<Order> getUserOrderList(Long userId, Integer status, Integer page, Integer size);

    /**
     * 获取订单详情
     */
    Order getOrderDetail(Long userId, Long orderId);

    /**
     * 模拟支付
     */
    void payOrder(Long userId, Long orderId);

    /**
     * 确认收货
     */
    void receiveOrder(Long userId, Long orderId);

    /**
     * 取消订单
     */
    void cancelOrder(Long userId, Long orderId);

    /**
     * 商家端 - 获取店铺订单列表
     */
    PageResult<Order> getShopOrderList(Long shopId, Integer status, Integer page, Integer size);

    /**
     * 商家端 - 订单发货
     */
    void shipOrder(Long shopId, Long orderId);

    /**
     * 管理员端 - 获取所有订单
     */
    PageResult<Order> getAllOrderList(Long userId, Integer status, Integer page, Integer size);
}
