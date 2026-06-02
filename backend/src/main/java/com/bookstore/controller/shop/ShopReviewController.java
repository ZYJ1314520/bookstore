package com.bookstore.controller.shop;

import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.entity.Review;
import com.bookstore.entity.Shop;
import com.bookstore.service.ReviewService;
import com.bookstore.service.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商家端评价接口
 */
@Tag(name = "商家端评价", description = "评价管理")
@RestController
@RequestMapping("/api/shop/reviews")
public class ShopReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ShopService shopService;

    @Operation(summary = "评价列表")
    @GetMapping
    public Result<PageResult<Review>> getReviewList(
            HttpServletRequest request,
            @RequestParam(required = false) Integer rating,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) request.getAttribute("userId");
        Shop shop = shopService.getShopInfo(userId);
        return Result.success(reviewService.getShopReviews(shop.getId(), rating, page, size));
    }

    @Operation(summary = "回复评价")
    @PostMapping("/{id}/reply")
    public Result<?> replyReview(HttpServletRequest request,
                                 @PathVariable Long id,
                                 @RequestParam String reply) {
        Long userId = (Long) request.getAttribute("userId");
        Shop shop = shopService.getShopInfo(userId);
        reviewService.replyReview(shop.getId(), id, reply);
        return Result.success("回复成功");
    }
}
