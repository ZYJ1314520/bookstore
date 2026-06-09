package com.bookstore.book.service;

import com.bookstore.common.PageResult;
import com.bookstore.dto.BookDTO;
import com.bookstore.entity.Book;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface BookService {
    PageResult<Book> getBookList(String keyword, Long categoryId, Integer page, Integer size, String sort);
    PageResult<Book> getBookList(String keyword, Long categoryId, BigDecimal priceMin, BigDecimal priceMax, Integer page, Integer size, String sort);
    Book getBookDetail(Long id);
    Map<String, Object> getBookDetailWithShopName(Long id);
    List<Book> getHotBooks(Integer limit);
    List<Book> getNewBooks(Integer limit);
    PageResult<Book> getShopBookList(Long shopId, String keyword, Long categoryId, Integer status, Integer page, Integer size);
    void addBook(Long shopId, BookDTO dto);
    void updateBook(Long shopId, BookDTO dto);
    void deleteBook(Long shopId, Long bookId);
    void updateBookStatus(Long shopId, Long bookId, Integer status);
    PageResult<Book> getAllBookList(String keyword, Long shopId, Integer page, Integer size);
    PageResult<Book> getShopBookListPublic(Long shopId, Integer page, Integer size, String sort);
}
