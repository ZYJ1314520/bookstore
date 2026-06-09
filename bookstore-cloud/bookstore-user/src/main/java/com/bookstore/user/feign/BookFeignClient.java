package com.bookstore.user.feign;

import com.bookstore.entity.Book;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "bookstore-book")
public interface BookFeignClient {

    @GetMapping("/internal/book/{id}")
    Book getBookById(@PathVariable("id") Long id);

    @GetMapping("/internal/book/shop/{shopId}/count")
    long countByShopId(@PathVariable("shopId") Long shopId);
}
