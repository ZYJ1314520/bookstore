package com.bookstore.order.controller;

import com.bookstore.common.BusinessException;
import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.entity.Review;
import com.bookstore.entity.Shop;
import com.bookstore.order.feign.UserFeignClient;
import com.bookstore.order.service.ReviewService;
import com.bookstore.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop/reviews")
public class ShopReviewController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserFeignClient userFeignClient;

    private Shop getShop() {
        Shop shop = userFeignClient.getShopByUserId(UserContext.getUserId());
        if (shop == null) throw new BusinessException("店铺不存在");
        return shop;
    }

    @GetMapping
    public Result<PageResult<Review>> getList(
            @RequestParam(required = false) Integer rating,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(reviewService.getShopReviews(getShop().getId(), rating, page, size));
    }

    @PostMapping("/{id}/reply")
    public Result<Void> reply(@PathVariable Long id, @RequestParam String reply) {
        reviewService.replyReview(getShop().getId(), id, reply);
        return Result.success();
    }
}
