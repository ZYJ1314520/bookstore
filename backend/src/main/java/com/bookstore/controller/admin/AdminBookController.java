package com.bookstore.controller.admin;

import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.entity.Book;
import com.bookstore.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员端图书管理接口
 */
@Tag(name = "管理员端图书管理", description = "图书管理")
@RestController
@RequestMapping("/api/admin/books")
public class AdminBookController {

    @Autowired
    private BookService bookService;

    @Operation(summary = "图书列表")
    @GetMapping
    public Result<PageResult<Book>> getBookList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long shopId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(bookService.getAllBookList(keyword, shopId, page, size));
    }

    @Operation(summary = "下架图书")
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        // 管理员可以操作所有图书
        bookService.updateBookStatus(null, id, status);
        return Result.success("状态已更新");
    }
}
