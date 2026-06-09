package com.bookstore.order.controller;

import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.dto.ReviewDTO;
import com.bookstore.entity.Review;
import com.bookstore.order.service.ReviewService;
import com.bookstore.utils.UserContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public Result<Void> add(@Valid @RequestBody ReviewDTO dto) {
        reviewService.addReview(UserContext.getUserId(), dto);
        return Result.success();
    }

    @GetMapping("/pending")
    public Result<PageResult<Map<String, Object>>> getPending(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(reviewService.getPendingReviews(UserContext.getUserId(), page, size));
    }

    @GetMapping("/history")
    public Result<PageResult<Map<String, Object>>> getMyReviews(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(reviewService.getUserReviews(UserContext.getUserId(), page, size));
    }

    @GetMapping("/book/{bookId}")
    public Result<PageResult<Review>> getBookReviews(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(reviewService.getBookReviews(bookId, page, size));
    }
}
