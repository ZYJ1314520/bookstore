package com.bookstore.book.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.book.feign.ReviewFeignClient;
import com.bookstore.book.feign.ShopFeignClient;
import com.bookstore.book.mapper.BookImageMapper;
import com.bookstore.book.service.BookService;
import com.bookstore.book.service.CategoryService;
import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.entity.Book;
import com.bookstore.entity.BookImage;
import com.bookstore.entity.Category;
import com.bookstore.entity.Review;
import com.bookstore.entity.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BookService bookService;
    @Autowired
    private ShopFeignClient shopFeignClient;
    @Autowired
    private ReviewFeignClient reviewFeignClient;
    @Autowired
    private BookImageMapper bookImageMapper;

    @GetMapping("/categories")
    public Result<List<Category>> getCategories() {
        return Result.success(categoryService.getCategoryTree());
    }

    @GetMapping("/books/hot")
    public Result<List<Book>> getHotBooks(@RequestParam(defaultValue = "8") Integer limit) {
        return Result.success(bookService.getHotBooks(limit));
    }

    @GetMapping("/books/new")
    public Result<List<Book>> getNewBooks(@RequestParam(defaultValue = "8") Integer limit) {
        return Result.success(bookService.getNewBooks(limit));
    }

    @GetMapping("/books/search")
    public Result<PageResult<Book>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer size,
            @RequestParam(required = false) String sort) {
        return Result.success(bookService.getBookList(keyword, categoryId, page, size, sort));
    }

    @GetMapping("/books")
    public Result<PageResult<Book>> bookList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer size,
            @RequestParam(defaultValue = "sales") String sort) {
        return Result.success(bookService.getBookList(keyword, categoryId, priceMin, priceMax, page, size, sort));
    }

    @GetMapping("/books/{id}")
    public Result<?> getBookDetail(@PathVariable Long id) {
        return Result.success(bookService.getBookDetailWithShopName(id));
    }

    @GetMapping("/books/{id}/reviews")
    public Result<?> getBookReviews(@PathVariable Long id,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer size) {
        try {
            List<Review> reviews = reviewFeignClient.getBookReviews(id, page, size);
            long total = reviewFeignClient.getBookReviewCount(id);
            Map<String, Object> data = new HashMap<>();
            data.put("records", reviews);
            data.put("total", total);
            return Result.success(data);
        } catch (Exception e) {
            return Result.success(Map.of("records", List.of(), "total", 0));
        }
    }

    @GetMapping("/books/{id}/images")
    public Result<?> getBookImages(@PathVariable Long id) {
        List<BookImage> images = bookImageMapper.selectList(
                new LambdaQueryWrapper<BookImage>()
                        .eq(BookImage::getBookId, id)
                        .orderByAsc(BookImage::getSort));
        return Result.success(images);
    }

    @GetMapping("/shops/{shopId}")
    public Result<Shop> getShopDetail(@PathVariable Long shopId) {
        return Result.success(shopFeignClient.getShopById(shopId));
    }

    @GetMapping("/shops/{shopId}/books")
    public Result<PageResult<Book>> getShopBooks(
            @PathVariable Long shopId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer size,
            @RequestParam(required = false) String sort) {
        return Result.success(bookService.getShopBookListPublic(shopId, page, size, sort));
    }
}
