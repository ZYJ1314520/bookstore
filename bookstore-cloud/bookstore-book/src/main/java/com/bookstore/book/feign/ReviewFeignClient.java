package com.bookstore.book.feign;

import com.bookstore.entity.Review;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "bookstore-order")
public interface ReviewFeignClient {

    @GetMapping("/internal/order/reviews/book/{bookId}")
    List<Review> getBookReviews(@PathVariable("bookId") Long bookId,
                                @RequestParam("page") Integer page,
                                @RequestParam("size") Integer size);

    @GetMapping("/internal/order/reviews/book/{bookId}/count")
    long getBookReviewCount(@PathVariable("bookId") Long bookId);
}
