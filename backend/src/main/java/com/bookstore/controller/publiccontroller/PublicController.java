package com.bookstore.controller.publiccontroller;

import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import com.bookstore.entity.Shop;
import com.bookstore.mapper.ShopMapper;
import com.bookstore.service.BookService;
import com.bookstore.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公共接口（无需登录）
 */
@Tag(name = "公共接口", description = "无需登录的接口")
@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BookService bookService;

    @Autowired
    private ShopMapper shopMapper;

    @Operation(summary = "获取分类树")
    @GetMapping("/categories")
    public Result<List<Category>> getCategoryTree() {
        return Result.success(categoryService.getCategoryTree());
    }

    @Operation(summary = "获取热销图书")
    @GetMapping("/books/hot")
    public Result<List<Book>> getHotBooks(
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(bookService.getHotBooks(limit));
    }

    @Operation(summary = "获取新书")
    @GetMapping("/books/new")
    public Result<List<Book>> getNewBooks(
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(bookService.getNewBooks(limit));
    }

    @Operation(summary = "图书搜索")
    @GetMapping("/books")
    public Result<?> searchBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "sales") String sort) {
        return Result.success(bookService.getBookList(keyword, categoryId, page, size, sort));
    }

    @Operation(summary = "获取店铺详情")
    @GetMapping("/shops/{id}")
    public Result<Shop> getShopDetail(@PathVariable Long id) {
        Shop shop = shopMapper.selectById(id);
        if (shop == null || shop.getStatus() != 1) {
            return Result.error("店铺不存在");
        }
        // 不返回敏感信息
        shop.setLicenseNo(null);
        shop.setLicenseImage(null);
        return Result.success(shop);
    }

    @Operation(summary = "获取店铺商品列表")
    @GetMapping("/shops/{id}/books")
    public Result<PageResult<Book>> getShopBooks(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "sales") String sort) {
        Shop shop = shopMapper.selectById(id);
        if (shop == null || shop.getStatus() != 1) {
            return Result.error("店铺不存在");
        }
        return Result.success(bookService.getShopBookListPublic(id, page, size, sort));
    }
}
