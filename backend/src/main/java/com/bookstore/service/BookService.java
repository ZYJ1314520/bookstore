package com.bookstore.service;

import com.bookstore.common.PageResult;
import com.bookstore.dto.BookDTO;
import com.bookstore.entity.Book;

import java.math.BigDecimal;
import java.util.List;

/**
 * 图书服务接口
 */
public interface BookService {

    /**
     * 分页查询图书（用户端）
     */
    PageResult<Book> getBookList(String keyword, Long categoryId, Integer page, Integer size, String sort);

    /**
     * 分页查询图书（含价格筛选）
     */
    PageResult<Book> getBookList(String keyword, Long categoryId, BigDecimal priceMin, BigDecimal priceMax, Integer page, Integer size, String sort);

    /**
     * 获取图书详情
     */
    Book getBookDetail(Long id);

    /**
     * 获取热销图书
     */
    List<Book> getHotBooks(Integer limit);

    /**
     * 获取新书
     */
    List<Book> getNewBooks(Integer limit);

    /**
     * 商家端 - 获取店铺图书列表
     */
    PageResult<Book> getShopBookList(Long shopId, String keyword, Long categoryId, Integer status, Integer page, Integer size);

    /**
     * 商家端 - 新增图书
     */
    void addBook(Long shopId, BookDTO dto);

    /**
     * 商家端 - 编辑图书
     */
    void updateBook(Long shopId, BookDTO dto);

    /**
     * 商家端 - 删除图书
     */
    void deleteBook(Long shopId, Long bookId);

    /**
     * 商家端 - 上下架图书
     */
    void updateBookStatus(Long shopId, Long bookId, Integer status);

    /**
     * 管理员端 - 获取所有图书
     */
    PageResult<Book> getAllBookList(String keyword, Long shopId, Integer page, Integer size);

    /**
     * 公共接口 - 获取店铺图书列表（公开，只返回上架商品）
     */
    PageResult<Book> getShopBookListPublic(Long shopId, Integer page, Integer size, String sort);
}
