package com.bookstore.controller.user;

import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.entity.Book;
import com.bookstore.entity.BookImage;
import com.bookstore.entity.Review;
import com.bookstore.entity.Shop;
import com.bookstore.mapper.BookImageMapper;
import com.bookstore.mapper.ShopMapper;
import com.bookstore.service.BookService;
import com.bookstore.service.ReviewService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户端图书接口
 */
@Tag(name = "用户端图书", description = "图书浏览、搜索")
@RestController
@RequestMapping("/api/user/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private BookImageMapper bookImageMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Operation(summary = "图书列表")
    @GetMapping
    public Result<PageResult<Book>> getBookList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) java.math.BigDecimal priceMin,
            @RequestParam(required = false) java.math.BigDecimal priceMax,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "sales") String sort) {
        return Result.success(bookService.getBookList(keyword, categoryId, priceMin, priceMax, page, size, sort));
    }

    @Operation(summary = "图书详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getBookDetail(@PathVariable Long id) {
        Book book = bookService.getBookDetail(id);
        Map<String, Object> result = new HashMap<>();
        result.put("book", book);

        // 查询店铺名称
        Shop shop = shopMapper.selectById(book.getShopId());
        if (shop != null) {
            result.put("shopName", shop.getShopName());
        }

        return Result.success(result);
    }

    @Operation(summary = "热销图书")
    @GetMapping("/hot")
    public Result<List<Book>> getHotBooks(
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(bookService.getHotBooks(limit));
    }

    @Operation(summary = "新书上架")
    @GetMapping("/new")
    public Result<List<Book>> getNewBooks(
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(bookService.getNewBooks(limit));
    }

    @Operation(summary = "图书评价列表")
    @GetMapping("/{id}/reviews")
    public Result<PageResult<Review>> getBookReviews(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(reviewService.getBookReviews(id, page, size));
    }

    @Operation(summary = "图书详情图片")
    @GetMapping("/{id}/images")
    public Result<List<BookImage>> getBookImages(@PathVariable Long id) {
        List<BookImage> images = bookImageMapper.selectList(
                new LambdaQueryWrapper<BookImage>()
                        .eq(BookImage::getBookId, id)
                        .orderByAsc(BookImage::getSort)
        );
        return Result.success(images);
    }
}
