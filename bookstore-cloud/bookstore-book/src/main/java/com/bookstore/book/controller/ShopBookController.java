package com.bookstore.book.controller;

import com.bookstore.book.feign.ShopFeignClient;
import com.bookstore.book.service.BookService;
import com.bookstore.common.BusinessException;
import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.dto.BookDTO;
import com.bookstore.entity.Book;
import com.bookstore.entity.Shop;
import com.bookstore.utils.UserContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop/books")
public class ShopBookController {

    @Autowired
    private BookService bookService;
    @Autowired
    private ShopFeignClient shopFeignClient;

    private Shop getShop() {
        Shop shop = shopFeignClient.getShopByUserId(UserContext.getUserId());
        if (shop == null) throw new BusinessException("店铺不存在");
        return shop;
    }

    @GetMapping
    public Result<PageResult<Book>> getList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(bookService.getShopBookList(getShop().getId(), keyword, categoryId, status, page, size));
    }

    @GetMapping("/{id}")
    public Result<Book> getDetail(@PathVariable Long id) {
        Book book = bookService.getBookDetail(id);
        if (!book.getShopId().equals(getShop().getId())) {
            return Result.error("无权查看该图书");
        }
        return Result.success(book);
    }

    @PostMapping
    public Result<Void> add(@Valid @RequestBody BookDTO dto) {
        bookService.addBook(getShop().getId(), dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody BookDTO dto) {
        dto.setId(id);
        bookService.updateBook(getShop().getId(), dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bookService.deleteBook(getShop().getId(), id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        bookService.updateBookStatus(getShop().getId(), id, status);
        return Result.success();
    }
}
