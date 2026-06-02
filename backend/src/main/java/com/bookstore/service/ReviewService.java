package com.bookstore.service;

import com.bookstore.common.PageResult;
import com.bookstore.dto.ReviewDTO;
import com.bookstore.entity.Review;

import java.util.List;

/**
 * 评价服务接口
 */
public interface ReviewService {

    /**
     * 提交评价
     */
    void addReview(Long userId, ReviewDTO dto);

    /**
     * 获取图书评价列表
     */
    PageResult<Review> getBookReviews(Long bookId, Integer page, Integer size);

    /**
     * 获取用户待评价订单
     */
    PageResult<Review> getPendingReviews(Long userId, Integer page, Integer size);

    /**
     * 获取用户评价历史
     */
    PageResult<Review> getUserReviews(Long userId, Integer page, Integer size);

    /**
     * 商家端 - 获取店铺评价列表
     */
    PageResult<Review> getShopReviews(Long shopId, Integer rating, Integer page, Integer size);

    /**
     * 商家端 - 回复评价
     */
    void replyReview(Long shopId, Long reviewId, String reply);
}
