package com.bookstore.book.controller;

import com.bookstore.book.service.BookService;
import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/books")
public class AdminBookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public Result<PageResult<Book>> getList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long shopId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(bookService.getAllBookList(keyword, shopId, page, size));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        bookService.updateBookStatus(null, id, status);
        return Result.success();
    }
}
