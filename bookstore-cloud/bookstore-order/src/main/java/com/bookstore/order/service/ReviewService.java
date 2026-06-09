package com.bookstore.order.service;

import com.bookstore.common.PageResult;
import com.bookstore.dto.ReviewDTO;
import com.bookstore.entity.Review;

import java.util.List;
import java.util.Map;

public interface ReviewService {
    void addReview(Long userId, ReviewDTO dto);
    PageResult<Review> getBookReviews(Long bookId, Integer page, Integer size);
    PageResult<Map<String, Object>> getPendingReviews(Long userId, Integer page, Integer size);
    PageResult<Map<String, Object>> getUserReviews(Long userId, Integer page, Integer size);
    PageResult<Review> getShopReviews(Long shopId, Integer rating, Integer page, Integer size);
    void replyReview(Long shopId, Long reviewId, String reply);
}
