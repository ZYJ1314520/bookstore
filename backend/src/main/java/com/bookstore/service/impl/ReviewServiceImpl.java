package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.common.BusinessException;
import com.bookstore.common.PageResult;
import com.bookstore.dto.ReviewDTO;
import com.bookstore.entity.*;
import com.bookstore.mapper.*;
import com.bookstore.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价服务实现
 */
@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public void addReview(Long userId, ReviewDTO dto) {
        // 检查订单
        Order order = orderMapper.selectById(dto.getOrderId());
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 3) {
            throw new BusinessException("订单未完成，无法评价");
        }

        // 如果前端没有传bookId，从订单明细中获取
        Long bookId = dto.getBookId();
        if (bookId == null) {
            // 查询该订单的第一个订单明细（单个商品订单）
            OrderItem orderItem = orderItemMapper.selectOne(
                    new LambdaQueryWrapper<OrderItem>()
                            .eq(OrderItem::getOrderId, dto.getOrderId())
                            .last("LIMIT 1")
            );
            if (orderItem == null) {
                throw new BusinessException("订单明细不存在");
            }
            bookId = orderItem.getBookId();
        }

        // 检查是否已评价
        Long count = reviewMapper.selectCount(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getUserId, userId)
                        .eq(Review::getOrderId, dto.getOrderId())
                        .eq(Review::getBookId, bookId)
        );
        if (count > 0) {
            throw new BusinessException("您已评价过该图书");
        }

        // 创建评价
        Review review = new Review();
        review.setUserId(userId);
        review.setBookId(bookId);
        review.setOrderId(dto.getOrderId());
        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        review.setImages(dto.getImages());

        reviewMapper.insert(review);
    }

    @Override
    public PageResult<Review> getBookReviews(Long bookId, Integer page, Integer size) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<Review>()
                .eq(Review::getBookId, bookId)
                .orderByDesc(Review::getCreateTime);

        List<Review> reviews = reviewMapper.selectList(wrapper);
        int total = reviews.size();

        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<Review> pageReviews = start < total ? reviews.subList(start, end) : List.of();

        return new PageResult<>(pageReviews, (long) total, page, size);
    }

    @Override
    public PageResult<Review> getPendingReviews(Long userId, Integer page, Integer size) {
        // 查询已完成但未评价的订单
        List<Order> completedOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, userId)
                        .eq(Order::getStatus, 3)
                        .orderByDesc(Order::getReceiveTime)
        );

        // 获取已完成订单的ID列表
        List<Long> completedOrderIds = completedOrders.stream()
                .map(Order::getId)
                .toList();

        if (completedOrderIds.isEmpty()) {
            return new PageResult<>(List.of(), 0L, page, size);
        }

        // 查询这些订单中已评价的订单ID
        List<Review> existingReviews = reviewMapper.selectList(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getUserId, userId)
                        .in(Review::getOrderId, completedOrderIds)
        );
        List<Long> reviewedOrderIds = existingReviews.stream()
                .map(Review::getOrderId)
                .distinct()
                .toList();

        // 过滤出未评价的订单ID
        List<Long> pendingOrderIds = completedOrderIds.stream()
                .filter(id -> !reviewedOrderIds.contains(id))
                .toList();

        if (pendingOrderIds.isEmpty()) {
            return new PageResult<>(List.of(), 0L, page, size);
        }

        // 查询这些订单的订单明细（每个明细对应一个可评价的商品）
        List<OrderItem> pendingItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .in(OrderItem::getOrderId, pendingOrderIds)
                        .orderByDesc(OrderItem::getCreateTime)
        );

        // 转换为Review对象返回（包含orderId, bookId, bookName, bookCover, shopId等信息）
        List<Review> reviews = pendingItems.stream()
                .map(item -> {
                    Review review = new Review();
                    review.setOrderId(item.getOrderId());
                    review.setBookId(item.getBookId());
                    // 用content字段临时存储bookName（前端显示用）
                    review.setContent(item.getBookName());
                    // 用images字段临时存储bookCover（前端显示用）
                    review.setImages(item.getBookCover());
                    return review;
                })
                .toList();

        int total = reviews.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<Review> pageReviews = start < total ? reviews.subList(start, end) : List.of();

        return new PageResult<>(pageReviews, (long) total, page, size);
    }

    @Override
    public PageResult<Review> getUserReviews(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<Review>()
                .eq(Review::getUserId, userId)
                .orderByDesc(Review::getCreateTime);

        List<Review> reviews = reviewMapper.selectList(wrapper);
        int total = reviews.size();

        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<Review> pageReviews = start < total ? reviews.subList(start, end) : List.of();

        return new PageResult<>(pageReviews, (long) total, page, size);
    }

    @Override
    public PageResult<Review> getShopReviews(Long shopId, Integer rating, Integer page, Integer size) {
        // 先查询该店铺的所有图书ID
        List<OrderItem> shopItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getShopId, shopId)
        );

        List<Long> bookIds = shopItems.stream()
                .map(OrderItem::getBookId)
                .distinct()
                .toList();

        if (bookIds.isEmpty()) {
            return new PageResult<>(List.of(), 0L, page, size);
        }

        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<Review>()
                .in(Review::getBookId, bookIds);

        if (rating != null) {
            wrapper.eq(Review::getRating, rating);
        }

        wrapper.orderByDesc(Review::getCreateTime);

        List<Review> reviews = reviewMapper.selectList(wrapper);
        int total = reviews.size();

        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<Review> pageReviews = start < total ? reviews.subList(start, end) : List.of();

        return new PageResult<>(pageReviews, (long) total, page, size);
    }

    @Override
    public void replyReview(Long shopId, Long reviewId, String reply) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }

        if (review.getReply() != null) {
            throw new BusinessException("已回复过该评价");
        }

        review.setReply(reply);
        review.setReplyTime(LocalDateTime.now());
        reviewMapper.updateById(review);
    }
}
