package com.bookstore.book.feign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.book.mapper.BookMapper;
import com.bookstore.entity.Book;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 内部Feign接口 - 供其他微服务调用
 */
@RestController
@RequestMapping("/internal/book")
public class BookInternalController {

    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private RedissonClient redissonClient;

    private static final String LOCK_PREFIX = "lock:book:stock:";

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookMapper.selectById(id);
    }

    @GetMapping("/popular")
    public List<Book> getPopularBooks() {
        return bookMapper.selectList(
                new LambdaQueryWrapper<Book>()
                        .eq(Book::getStatus, 1)
                        .eq(Book::getDeleted, 0)
                        .orderByDesc(Book::getSales)
                        .last("LIMIT 20"));
    }

    @GetMapping("/all")
    public List<Book> getAllBooks() {
        return bookMapper.selectList(
                new LambdaQueryWrapper<Book>()
                        .eq(Book::getStatus, 1)
                        .eq(Book::getDeleted, 0)
                        .orderByDesc(Book::getSales));
    }

    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam String keyword) {
        return bookMapper.selectList(
                new LambdaQueryWrapper<Book>()
                        .eq(Book::getStatus, 1)
                        .eq(Book::getDeleted, 0)
                        .and(w -> w
                                .like(Book::getTitle, keyword)
                                .or().like(Book::getAuthor, keyword)
                                .or().like(Book::getDescription, keyword))
                        .last("LIMIT 10"));
    }

    @GetMapping("/shop/{shopId}/count")
    public long countByShopId(@PathVariable Long shopId) {
        return bookMapper.selectCount(
                new LambdaQueryWrapper<Book>()
                        .eq(Book::getShopId, shopId)
                        .eq(Book::getDeleted, 0));
    }

    @GetMapping("/batch")
    public List<Book> batchGetBooks(@RequestParam List<Long> ids) {
        return bookMapper.selectBatchIds(ids);
    }

    @PutMapping("/{id}/stock/decrease")
    public boolean decreaseStock(@PathVariable Long id, @RequestParam Integer quantity) {
        RLock lock = redissonClient.getLock(LOCK_PREFIX + id);
        try {
            if (!lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                return false;
            }
            Book book = bookMapper.selectById(id);
            if (book == null || book.getStock() < quantity) return false;
            book.setStock(book.getStock() - quantity);
            bookMapper.updateById(book);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @PutMapping("/{id}/stock/restore")
    public void restoreStock(@PathVariable Long id, @RequestParam Integer quantity) {
        RLock lock = redissonClient.getLock(LOCK_PREFIX + id);
        try {
            if (!lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                throw new RuntimeException("获取锁失败");
            }
            Book book = bookMapper.selectById(id);
            if (book != null) {
                book.setStock(book.getStock() + quantity);
                bookMapper.updateById(book);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取锁被中断", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @PutMapping("/{id}/sales/increase")
    @CacheEvict(value = "bookCache", allEntries = true)
    public void increaseSales(@PathVariable Long id, @RequestParam Integer quantity) {
        RLock lock = redissonClient.getLock(LOCK_PREFIX + id);
        try {
            if (!lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                throw new RuntimeException("获取锁失败");
            }
            Book book = bookMapper.selectById(id);
            if (book != null) {
                book.setSales(book.getSales() + quantity);
                bookMapper.updateById(book);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取锁被中断", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
