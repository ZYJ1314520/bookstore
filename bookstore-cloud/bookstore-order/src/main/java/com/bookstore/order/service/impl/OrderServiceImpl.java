package com.bookstore.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookstore.common.BusinessException;
import com.bookstore.common.PageResult;
import com.bookstore.dto.OrderDTO;
import com.bookstore.entity.*;
import com.bookstore.order.feign.BookFeignClient;
import com.bookstore.order.feign.UserFeignClient;
import com.bookstore.order.mapper.OrderItemMapper;
import com.bookstore.order.mapper.OrderMapper;
import com.bookstore.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private BookFeignClient bookFeignClient;
    @Autowired
    private UserFeignClient userFeignClient;

    @Override
    @Transactional
    public List<Order> createOrder(Long userId, OrderDTO dto) {
        // 获取收货地址
        Address address = userFeignClient.getAddressById(dto.getAddressId(), userId);
        if (address == null) throw new BusinessException("收货地址不存在");

        String receiverAddress = address.getProvince() + address.getCity()
                + address.getDistrict() + address.getDetailAddress();

        // 查询图书并按shopId分组
        Map<Long, List<BookItemPair>> shopGroups = new LinkedHashMap<>();
        for (OrderDTO.OrderItemDTO itemDto : dto.getItems()) {
            Book book = bookFeignClient.getBookById(itemDto.getBookId());
            if (book == null || book.getStatus() != 1) throw new BusinessException("图书不存在或已下架: " + itemDto.getBookId());
            shopGroups.computeIfAbsent(book.getShopId(), k -> new ArrayList<>())
                    .add(new BookItemPair(book, itemDto));
        }

        // 为每个商家创建独立订单
        List<Order> orders = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (Map.Entry<Long, List<BookItemPair>> entry : shopGroups.entrySet()) {
            List<BookItemPair> pairs = entry.getValue();

            Order order = new Order();
            order.setOrderNo(IdUtil.getSnowflakeNextIdStr());
            order.setUserId(userId);
            order.setStatus(0);
            order.setReceiverName(address.getReceiverName());
            order.setReceiverPhone(address.getPhone());
            order.setReceiverAddress(receiverAddress);
            order.setCreateTime(now);
            order.setUpdateTime(now);

            BigDecimal totalAmount = BigDecimal.ZERO;
            for (BookItemPair pair : pairs) {
                totalAmount = totalAmount.add(
                        pair.book.getPrice().multiply(BigDecimal.valueOf(pair.itemDto.getQuantity())));
            }
            order.setTotalAmount(totalAmount);
            orderMapper.insert(order);

            for (BookItemPair pair : pairs) {
                OrderItem item = new OrderItem();
                item.setOrderId(order.getId());
                item.setBookId(pair.book.getId());
                item.setBookName(pair.book.getTitle());
                item.setBookCover(pair.book.getCover());
                item.setPrice(pair.book.getPrice());
                item.setQuantity(pair.itemDto.getQuantity());
                item.setShopId(pair.book.getShopId());
                item.setCreateTime(now);
                orderItemMapper.insert(item);

                // 通过Feign扣减库存
                boolean decreased = bookFeignClient.decreaseStock(pair.book.getId(), pair.itemDto.getQuantity());
                if (!decreased) {
                    throw new BusinessException("库存扣减失败，库存不足: " + pair.book.getTitle());
                }
            }
            orders.add(order);
        }

        // 清空购物车中已下单的商品
        List<Long> bookIds = dto.getItems().stream()
                .map(OrderDTO.OrderItemDTO::getBookId).toList();
        userFeignClient.deleteCartItems(userId, bookIds);

        return orders;
    }

    private static class BookItemPair {
        Book book;
        OrderDTO.OrderItemDTO itemDto;
        BookItemPair(Book book, OrderDTO.OrderItemDTO itemDto) {
            this.book = book;
            this.itemDto = itemDto;
        }
    }

    @Override
    public PageResult<Order> getUserOrderList(Long userId, Integer status, Integer page, Integer size) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>().eq(Order::getUserId, userId);
        if (status != null) wrapper.eq(Order::getStatus, status);
        wrapper.orderByDesc(Order::getId);
        Page<Order> pageResult = orderMapper.selectPage(new Page<>(page, size), wrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal(), page, size);
    }

    @Override
    public Order getOrderDetail(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (userId != null && !order.getUserId().equals(userId)) throw new BusinessException("无权查看该订单");
        return order;
    }

    @Override
    public List<Map<String, Object>> getOrderItems(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (userId != null && !order.getUserId().equals(userId)) throw new BusinessException("无权查看该订单");
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        List<Map<String, Object>> result = new ArrayList<>();
        for (OrderItem item : items) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", item.getId());
            map.put("orderId", item.getOrderId());
            map.put("bookId", item.getBookId());
            map.put("bookName", item.getBookName());
            map.put("bookCover", item.getBookCover());
            map.put("price", item.getPrice());
            map.put("quantity", item.getQuantity());
            map.put("shopId", item.getShopId());
            // 查询店铺名称
            try {
                Shop shop = userFeignClient.getShopById(item.getShopId());
                if (shop != null) map.put("shopName", shop.getShopName());
            } catch (Exception e) {
                log.warn("获取店铺信息失败: shopId={}", item.getShopId(), e);
            }
            result.add(map);
        }
        return result;
    }

    @Override
    @Transactional
    public void payOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) throw new BusinessException("订单不存在");
        if (order.getStatus() != 0) throw new BusinessException("订单状态错误");
        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 增加销量
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            bookFeignClient.increaseSales(item.getBookId(), item.getQuantity());
        }
    }

    @Override
    public void receiveOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) throw new BusinessException("订单不存在");
        if (order.getStatus() != 2) throw new BusinessException("订单状态错误");
        order.setStatus(3);
        order.setReceiveTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) throw new BusinessException("订单不存在");
        if (order.getStatus() != 0) throw new BusinessException("订单状态错误，无法取消");
        order.setStatus(4);
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 恢复库存
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            bookFeignClient.restoreStock(item.getBookId(), item.getQuantity());
        }

    }

    @Override
    public PageResult<Order> getShopOrderList(Long shopId, Integer status, Integer page, Integer size) {
        List<OrderItem> shopItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getShopId, shopId));
        List<Long> orderIds = shopItems.stream().map(OrderItem::getOrderId).distinct().toList();
        if (orderIds.isEmpty()) return new PageResult<>(List.of(), 0L, page, size);

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>().in(Order::getId, orderIds);
        if (status != null) wrapper.eq(Order::getStatus, status);
        wrapper.orderByDesc(Order::getId);
        Page<Order> pageResult = orderMapper.selectPage(new Page<>(page, size), wrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal(), page, size);
    }

    @Override
    public void shipOrder(Long shopId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getStatus() != 1) throw new BusinessException("订单状态错误");
        order.setStatus(2);
        order.setShipTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public PageResult<Order> getAllOrderList(Long userId, Integer status, Integer page, Integer size) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) wrapper.eq(Order::getUserId, userId);
        if (status != null) wrapper.eq(Order::getStatus, status);
        wrapper.orderByDesc(Order::getId);
        Page<Order> pageResult = orderMapper.selectPage(new Page<>(page, size), wrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal(), page, size);
    }
}
