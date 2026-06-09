package com.bookstore.order.feign;

import com.bookstore.entity.Book;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "bookstore-book")
public interface BookFeignClient {

    @GetMapping("/internal/book/{id}")
    Book getBookById(@PathVariable("id") Long id);

    @GetMapping("/internal/book/batch")
    List<Book> batchGetBooks(@RequestParam("ids") List<Long> ids);

    @PutMapping("/internal/book/{id}/stock/decrease")
    boolean decreaseStock(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity);

    @PutMapping("/internal/book/{id}/stock/restore")
    void restoreStock(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity);

    @PutMapping("/internal/book/{id}/sales/increase")
    void increaseSales(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity);
}
