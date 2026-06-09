package com.bookstore.admin.feign;

import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.entity.Book;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "bookstore-book")
public interface BookServiceClient {

    @GetMapping("/internal/book/{id}")
    Book getBookById(@PathVariable("id") Long id);

    @GetMapping("/api/admin/books")
    Result<PageResult<Book>> getBookList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long shopId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size);

    @GetMapping("/api/public/books/hot")
    Result<List<Book>> getHotBooks(@RequestParam(defaultValue = "10") Integer limit);
}
