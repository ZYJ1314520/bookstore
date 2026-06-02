package com.bookstore.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.common.BusinessException;
import com.bookstore.common.PageResult;
import com.bookstore.dto.OrderDTO;
import com.bookstore.entity.*;
import com.bookstore.mapper.*;
import com.bookstore.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单服务实现
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private CartMapper cartMapper;

    @Override
    @Transactional
    public Order createOrder(Long userId, OrderDTO dto) {
        // 获取收货地址
        Address address = addressMapper.selectById(dto.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("收货地址不存在");
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(IdUtil.getSnowflakeNextIdStr());
        order.setUserId(userId);
        order.setStatus(0); // 待付款
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress());

        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;

        // 添加订单详情
        for (OrderDTO.OrderItemDTO itemDto : dto.getItems()) {
            Book book = bookMapper.selectById(itemDto.getBookId());
            if (book == null || book.getStatus() != 1) {
                throw new BusinessException("图书不存在或已下架: " + itemDto.getBookId());
            }
            if (book.getStock() < itemDto.getQuantity()) {
                throw new BusinessException("库存不足: " + book.getTitle());
            }

            OrderItem item = new OrderItem();
            item.setBookId(book.getId());
            item.setBookName(book.getTitle());
            item.setBookCover(book.getCover());
            item.setPrice(book.getPrice());
            item.setQuantity(itemDto.getQuantity());
            item.setShopId(book.getShopId());

            totalAmount = totalAmount.add(book.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));

            // 扣减库存
            book.setStock(book.getStock() - itemDto.getQuantity());
            bookMapper.updateById(book);
        }

        order.setTotalAmount(totalAmount);
        orderMapper.insert(order);

        // 插入订单详情
        for (OrderDTO.OrderItemDTO itemDto : dto.getItems()) {
            Book book = bookMapper.selectById(itemDto.getBookId());
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setBookId(book.getId());
            item.setBookName(book.getTitle());
            item.setBookCover(book.getCover());
            item.setPrice(book.getPrice());
            item.setQuantity(itemDto.getQuantity());
            item.setShopId(book.getShopId());
            orderItemMapper.insert(item);
        }

        return order;
    }

    @Override
    public PageResult<Order> getUserOrderList(Long userId, Integer status, Integer page, Integer size) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId);

        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        wrapper.orderByDesc(Order::getCreateTime);

        List<Order> orders = orderMapper.selectList(wrapper);
        int total = orders.size();

        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<Order> pageOrders = start < total ? orders.subList(start, end) : List.of();

        return new PageResult<>(pageOrders, (long) total, page, size);
    }

    @Override
    public Order getOrderDetail(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        // userId不为null时校验归属（用户操作），null时跳过（管理员/商家操作）
        if (userId != null && !order.getUserId().equals(userId)) {
            throw new BusinessException("无权查看该订单");
        }
        return order;
    }

    @Override
    @Transactional
    public void payOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态错误");
        }

        order.setStatus(1); // 待发货
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 增加销量
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId)
        );
        for (OrderItem item : items) {
            Book book = bookMapper.selectById(item.getBookId());
            if (book != null) {
                book.setSales(book.getSales() + item.getQuantity());
                bookMapper.updateById(book);
            }
        }
    }

    @Override
    public void receiveOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException("订单状态错误");
        }

        order.setStatus(3); // 已完成
        order.setReceiveTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态错误，无法取消");
        }

        order.setStatus(4); // 已取消
        orderMapper.updateById(order);

        // 恢复库存
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId)
        );
        for (OrderItem item : items) {
            Book book = bookMapper.selectById(item.getBookId());
            if (book != null) {
                book.setStock(book.getStock() + item.getQuantity());
                bookMapper.updateById(book);
            }
        }
    }

    @Override
    public PageResult<Order> getShopOrderList(Long shopId, Integer status, Integer page, Integer size) {
        // 先查询该店铺的所有订单ID
        List<OrderItem> shopItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getShopId, shopId)
        );

        List<Long> orderIds = shopItems.stream()
                .map(OrderItem::getOrderId)
                .distinct()
                .toList();

        if (orderIds.isEmpty()) {
            return new PageResult<>(List.of(), 0L, page, size);
        }

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .in(Order::getId, orderIds);

        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        wrapper.orderByDesc(Order::getCreateTime);

        List<Order> orders = orderMapper.selectList(wrapper);
        int total = orders.size();

        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<Order> pageOrders = start < total ? orders.subList(start, end) : List.of();

        return new PageResult<>(pageOrders, (long) total, page, size);
    }

    @Override
    public void shipOrder(Long shopId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException("订单状态错误");
        }

        order.setStatus(2); // 已发货
        order.setShipTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public PageResult<Order> getAllOrderList(Long userId, Integer status, Integer page, Integer size) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (userId != null) {
            wrapper.eq(Order::getUserId, userId);
        }
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        wrapper.orderByDesc(Order::getCreateTime);

        List<Order> orders = orderMapper.selectList(wrapper);
        int total = orders.size();

        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<Order> pageOrders = start < total ? orders.subList(start, end) : List.of();

        return new PageResult<>(pageOrders, (long) total, page, size);
    }
}
