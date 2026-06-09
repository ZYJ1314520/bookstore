package com.bookstore.book.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookstore.common.BusinessException;
import com.bookstore.common.PageResult;
import com.bookstore.dto.BookDTO;
import com.bookstore.entity.Book;
import com.bookstore.entity.Shop;
import com.bookstore.book.feign.ShopFeignClient;
import com.bookstore.book.mapper.BookMapper;
import com.bookstore.book.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private ShopFeignClient shopFeignClient;

    @Override
    public PageResult<Book> getBookList(String keyword, Long categoryId, Integer page, Integer size, String sort) {
        return getBookList(keyword, categoryId, null, null, page, size, sort);
    }

    public PageResult<Book> getBookList(String keyword, Long categoryId, BigDecimal priceMin, BigDecimal priceMax, Integer page, Integer size, String sort) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<Book>()
                .eq(Book::getStatus, 1);
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Book::getTitle, keyword).or().like(Book::getAuthor, keyword).or().like(Book::getIsbn, keyword));
        }
        if (categoryId != null) wrapper.eq(Book::getCategoryId, categoryId);
        if (priceMin != null) wrapper.ge(Book::getPrice, priceMin);
        if (priceMax != null) wrapper.le(Book::getPrice, priceMax);
        if ("sales".equals(sort)) wrapper.orderByDesc(Book::getSales);
        else if ("price_asc".equals(sort)) wrapper.orderByAsc(Book::getPrice);
        else if ("price_desc".equals(sort)) wrapper.orderByDesc(Book::getPrice);
        else if ("new".equals(sort)) wrapper.orderByDesc(Book::getId);
        else wrapper.orderByDesc(Book::getSales);

        Page<Book> pageResult = bookMapper.selectPage(new Page<>(page, size), wrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal(), page, size);
    }

    @Override
    public Book getBookDetail(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null || book.getDeleted() == 1) throw new BusinessException("图书不存在");
        return book;
    }

    @Override
    public Map<String, Object> getBookDetailWithShopName(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null || book.getDeleted() == 1) throw new BusinessException("图书不存在");
        Map<String, Object> result = new HashMap<>();
        result.put("book", book);
        Shop shop = shopFeignClient.getShopById(book.getShopId());
        if (shop != null) result.put("shopName", shop.getShopName());
        return result;
    }

    @Override
    @Cacheable(value = "bookCache", key = "'hot:' + #limit")
    public List<Book> getHotBooks(Integer limit) {
        return bookMapper.selectList(
                new LambdaQueryWrapper<Book>()
                        .eq(Book::getStatus, 1)
                        .orderByDesc(Book::getSales).last("LIMIT " + limit));
    }

    @Override
    @Cacheable(value = "bookCache", key = "'new:' + #limit")
    public List<Book> getNewBooks(Integer limit) {
        return bookMapper.selectList(
                new LambdaQueryWrapper<Book>()
                        .eq(Book::getStatus, 1)
                        .orderByDesc(Book::getCreateTime).last("LIMIT " + limit));
    }

    @Override
    public PageResult<Book> getShopBookList(Long shopId, String keyword, Long categoryId, Integer status, Integer page, Integer size) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<Book>().eq(Book::getShopId, shopId);
        if (StrUtil.isNotBlank(keyword)) wrapper.like(Book::getTitle, keyword);
        if (categoryId != null) wrapper.eq(Book::getCategoryId, categoryId);
        if (status != null) wrapper.eq(Book::getStatus, status);
        wrapper.orderByDesc(Book::getId);
        Page<Book> pageResult = bookMapper.selectPage(new Page<>(page, size), wrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal(), page, size);
    }

    @Override
    @CacheEvict(value = "bookCache", allEntries = true)
    public void addBook(Long shopId, BookDTO dto) {
        Book book = new Book();
        book.setShopId(shopId);
        book.setCategoryId(dto.getCategoryId());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setCover(dto.getCover());
        book.setPrice(dto.getPrice());
        book.setOriginalPrice(dto.getOriginalPrice());
        book.setStock(dto.getStock() != null ? dto.getStock() : 0);
        book.setSales(0);
        book.setPublisher(dto.getPublisher());
        if (dto.getPublishDate() != null && !dto.getPublishDate().isEmpty()) {
            book.setPublishDate(LocalDate.parse(dto.getPublishDate()));
        }
        book.setDescription(dto.getDescription());
        book.setDetail(dto.getDetail());
        book.setStatus(1);
        book.setCreateTime(LocalDateTime.now());
        book.setUpdateTime(LocalDateTime.now());
        bookMapper.insert(book);
    }

    @Override
    @CacheEvict(value = "bookCache", allEntries = true)
    public void updateBook(Long shopId, BookDTO dto) {
        Book book = bookMapper.selectById(dto.getId());
        if (book == null || !book.getShopId().equals(shopId)) throw new BusinessException("图书不存在");
        book.setCategoryId(dto.getCategoryId());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setCover(dto.getCover());
        book.setPrice(dto.getPrice());
        book.setOriginalPrice(dto.getOriginalPrice());
        book.setStock(dto.getStock());
        book.setPublisher(dto.getPublisher());
        if (dto.getPublishDate() != null && !dto.getPublishDate().isEmpty()) {
            try { book.setPublishDate(LocalDate.parse(dto.getPublishDate())); } catch (Exception e) { log.warn("日期格式解析失败: {}", dto.getPublishDate()); }
        }
        book.setDescription(dto.getDescription());
        book.setDetail(dto.getDetail());
        bookMapper.updateById(book);
    }

    @Override
    @CacheEvict(value = "bookCache", allEntries = true)
    public void deleteBook(Long shopId, Long bookId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null || !book.getShopId().equals(shopId)) throw new BusinessException("图书不存在");
        bookMapper.deleteById(bookId);
    }

    @Override
    @CacheEvict(value = "bookCache", allEntries = true)
    public void updateBookStatus(Long shopId, Long bookId, Integer status) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) throw new BusinessException("图书不存在");
        if (shopId != null && !book.getShopId().equals(shopId)) throw new BusinessException("无权操作该图书");
        book.setStatus(status);
        bookMapper.updateById(book);
    }

    @Override
    public PageResult<Book> getAllBookList(String keyword, Long shopId, Integer page, Integer size) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<Book>();
        if (StrUtil.isNotBlank(keyword)) wrapper.like(Book::getTitle, keyword);
        if (shopId != null) wrapper.eq(Book::getShopId, shopId);
        wrapper.orderByDesc(Book::getId);
        Page<Book> pageResult = bookMapper.selectPage(new Page<>(page, size), wrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal(), page, size);
    }

    @Override
    public PageResult<Book> getShopBookListPublic(Long shopId, Integer page, Integer size, String sort) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<Book>()
                .eq(Book::getShopId, shopId).eq(Book::getStatus, 1);
        if ("sales".equals(sort)) wrapper.orderByDesc(Book::getSales);
        else if ("price_asc".equals(sort)) wrapper.orderByAsc(Book::getPrice);
        else if ("price_desc".equals(sort)) wrapper.orderByDesc(Book::getPrice);
        else if ("new".equals(sort)) wrapper.orderByDesc(Book::getId);
        else wrapper.orderByDesc(Book::getSales);
        Page<Book> pageResult = bookMapper.selectPage(new Page<>(page, size), wrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal(), page, size);
    }
}
