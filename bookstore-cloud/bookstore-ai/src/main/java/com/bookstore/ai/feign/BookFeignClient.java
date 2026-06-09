package com.bookstore.ai.feign;

import com.bookstore.entity.Book;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "bookstore-book", path = "/internal/book")
public interface BookFeignClient {

    @GetMapping("/popular")
    List<Book> getPopularBooks();

    @GetMapping("/all")
    List<Book> getAllBooks();

    @GetMapping("/search")
    List<Book> searchBooks(@RequestParam("keyword") String keyword);
}
