package com.bookstore.order.service;

import com.bookstore.common.PageResult;
import com.bookstore.dto.OrderDTO;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;

import java.util.List;
import java.util.Map;

public interface OrderService {
    List<Order> createOrder(Long userId, OrderDTO dto);
    PageResult<Order> getUserOrderList(Long userId, Integer status, Integer page, Integer size);
    Order getOrderDetail(Long userId, Long orderId);
    List<Map<String, Object>> getOrderItems(Long userId, Long orderId);
    void payOrder(Long userId, Long orderId);
    void receiveOrder(Long userId, Long orderId);
    void cancelOrder(Long userId, Long orderId);
    PageResult<Order> getShopOrderList(Long shopId, Integer status, Integer page, Integer size);
    void shipOrder(Long shopId, Long orderId);
    PageResult<Order> getAllOrderList(Long userId, Integer status, Integer page, Integer size);
}
