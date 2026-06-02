package com.bookstore.controller.shop;

import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.dto.BookDTO;
import com.bookstore.entity.Book;
import com.bookstore.entity.Shop;
import com.bookstore.service.BookService;
import com.bookstore.service.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商家端图书接口
 */
@Tag(name = "商家端图书", description = "图书管理")
@RestController
@RequestMapping("/api/shop/books")
public class ShopBookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ShopService shopService;

    @Operation(summary = "图书列表")
    @GetMapping
    public Result<PageResult<Book>> getBookList(
            HttpServletRequest request,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) request.getAttribute("userId");
        Shop shop = shopService.getShopInfo(userId);
        return Result.success(bookService.getShopBookList(shop.getId(), keyword, categoryId, status, page, size));
    }

    @Operation(summary = "新增图书")
    @PostMapping
    public Result<?> addBook(HttpServletRequest request,
                             @Valid @RequestBody BookDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        Shop shop = shopService.getShopInfo(userId);
        bookService.addBook(shop.getId(), dto);
        return Result.success("添加成功");
    }

    @Operation(summary = "编辑图书")
    @PutMapping("/{id}")
    public Result<?> updateBook(HttpServletRequest request,
                                @PathVariable Long id,
                                @Valid @RequestBody BookDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        Shop shop = shopService.getShopInfo(userId);
        dto.setId(id);
        bookService.updateBook(shop.getId(), dto);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除图书")
    @DeleteMapping("/{id}")
    public Result<?> deleteBook(HttpServletRequest request,
                                @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        Shop shop = shopService.getShopInfo(userId);
        bookService.deleteBook(shop.getId(), id);
        return Result.success("删除成功");
    }

    @Operation(summary = "上下架图书")
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(HttpServletRequest request,
                                  @PathVariable Long id,
                                  @RequestParam Integer status) {
        Long userId = (Long) request.getAttribute("userId");
        Shop shop = shopService.getShopInfo(userId);
        bookService.updateBookStatus(shop.getId(), id, status);
        return Result.success("状态已更新");
    }
}
