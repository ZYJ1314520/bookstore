package com.bookstore.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookstore.common.BusinessException;
import com.bookstore.common.PageResult;
import com.bookstore.dto.ReviewDTO;
import com.bookstore.entity.*;
import com.bookstore.order.feign.BookFeignClient;
import com.bookstore.order.feign.UserFeignClient;
import com.bookstore.order.mapper.OrderItemMapper;
import com.bookstore.order.mapper.OrderMapper;
import com.bookstore.order.mapper.ReviewMapper;
import com.bookstore.order.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private BookFeignClient bookFeignClient;

    @Override
    public void addReview(Long userId, ReviewDTO dto) {
        Order order = orderMapper.selectById(dto.getOrderId());
        if (order == null || !order.getUserId().equals(userId)) throw new BusinessException("订单不存在");
        if (order.getStatus() != 3) throw new BusinessException("订单未完成，无法评价");

        Long bookId = dto.getBookId();
        if (bookId == null) {
            OrderItem orderItem = orderItemMapper.selectOne(
                    new LambdaQueryWrapper<OrderItem>()
                            .eq(OrderItem::getOrderId, dto.getOrderId())
                            .last("LIMIT 1"));
            if (orderItem == null) throw new BusinessException("订单明细不存在");
            bookId = orderItem.getBookId();
        }

        Long count = reviewMapper.selectCount(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getUserId, userId)
                        .eq(Review::getOrderId, dto.getOrderId())
                        .eq(Review::getBookId, bookId));
        if (count > 0) throw new BusinessException("您已评价过该图书");

        Review review = new Review();
        review.setUserId(userId);
        review.setBookId(bookId);
        review.setOrderId(dto.getOrderId());
        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        review.setImages(dto.getImages());
        review.setCreateTime(LocalDateTime.now());
        reviewMapper.insert(review);
    }

    @Override
    public PageResult<Review> getBookReviews(Long bookId, Integer page, Integer size) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<Review>()
                .eq(Review::getBookId, bookId).orderByDesc(Review::getCreateTime);
        Page<Review> pageResult = reviewMapper.selectPage(new Page<>(page, size), wrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal(), page, size);
    }

    @Override
    public PageResult<Map<String, Object>> getPendingReviews(Long userId, Integer page, Integer size) {
        List<Order> completedOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, userId).eq(Order::getStatus, 3)
                        .orderByDesc(Order::getReceiveTime));
        List<Long> completedOrderIds = completedOrders.stream().map(Order::getId).toList();
        if (completedOrderIds.isEmpty()) return new PageResult<>(List.of(), 0L, page, size);

        List<Review> existingReviews = reviewMapper.selectList(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getUserId, userId).in(Review::getOrderId, completedOrderIds));
        Set<String> reviewedPairs = existingReviews.stream()
                .map(r -> r.getOrderId() + "_" + r.getBookId())
                .collect(Collectors.toSet());

        List<OrderItem> allItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().in(OrderItem::getOrderId, completedOrderIds));
        List<OrderItem> pendingItems = allItems.stream()
                .filter(item -> !reviewedPairs.contains(item.getOrderId() + "_" + item.getBookId()))
                .toList();
        if (pendingItems.isEmpty()) return new PageResult<>(List.of(), 0L, page, size);

        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderItem item : pendingItems) {
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", item.getOrderId());
            map.put("bookId", item.getBookId());
            map.put("bookName", item.getBookName());
            map.put("bookCover", item.getBookCover());
            list.add(map);
        }
        int total = list.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<Map<String, Object>> pageList = start < total ? list.subList(start, end) : List.of();
        return new PageResult<>(pageList, (long) total, page, size);
    }

    @Override
    public PageResult<Map<String, Object>> getUserReviews(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<Review>()
                .eq(Review::getUserId, userId).orderByDesc(Review::getCreateTime);
        Page<Review> pageResult = reviewMapper.selectPage(new Page<>(page, size), wrapper);

        List<Map<String, Object>> list = new ArrayList<>();
        for (Review review : pageResult.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", review.getId());
            map.put("bookId", review.getBookId());
            map.put("orderId", review.getOrderId());
            map.put("rating", review.getRating());
            map.put("content", review.getContent());
            map.put("reply", review.getReply());
            map.put("replyTime", review.getReplyTime());
            map.put("createTime", review.getCreateTime());
            Book book = bookFeignClient.getBookById(review.getBookId());
            if (book != null) {
                map.put("bookName", book.getTitle());
                map.put("bookCover", book.getCover());
            }
            list.add(map);
        }
        return new PageResult<>(list, pageResult.getTotal(), page, size);
    }

    @Override
    public PageResult<Review> getShopReviews(Long shopId, Integer rating, Integer page, Integer size) {
        List<OrderItem> shopItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getShopId, shopId));
        List<Long> bookIds = shopItems.stream().map(OrderItem::getBookId).distinct().toList();
        if (bookIds.isEmpty()) return new PageResult<>(List.of(), 0L, page, size);

        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<Review>().in(Review::getBookId, bookIds);
        if (rating != null) wrapper.eq(Review::getRating, rating);
        wrapper.orderByDesc(Review::getCreateTime);
        Page<Review> pageResult = reviewMapper.selectPage(new Page<>(page, size), wrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal(), page, size);
    }

    @Override
    public void replyReview(Long shopId, Long reviewId, String reply) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) throw new BusinessException("评价不存在");
        if (review.getReply() != null) throw new BusinessException("已回复过该评价");
        review.setReply(reply);
        review.setReplyTime(LocalDateTime.now());
        reviewMapper.updateById(review);
    }
}
