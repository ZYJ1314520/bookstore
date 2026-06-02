package com.bookstore.controller.user;

import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.dto.ReviewDTO;
import com.bookstore.entity.Review;
import com.bookstore.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端评价接口
 */
@Tag(name = "用户端评价", description = "评价管理")
@RestController
@RequestMapping("/api/user/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Operation(summary = "提交评价")
    @PostMapping
    public Result<?> addReview(HttpServletRequest request,
                               @Valid @RequestBody ReviewDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        reviewService.addReview(userId, dto);
        return Result.success("评价成功");
    }

    @Operation(summary = "待评价列表")
    @GetMapping("/pending")
    public Result<PageResult<Review>> getPendingReviews(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(reviewService.getPendingReviews(userId, page, size));
    }

    @Operation(summary = "评价历史")
    @GetMapping("/history")
    public Result<PageResult<Review>> getReviewHistory(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(reviewService.getUserReviews(userId, page, size));
    }
}
