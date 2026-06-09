package com.bookstore.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.common.BusinessException;
import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import com.bookstore.book.mapper.BookMapper;
import com.bookstore.book.mapper.CategoryMapper;
import com.bookstore.book.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BookMapper bookMapper;

    @Override
    @Cacheable(value = "categoryCache", key = "'tree'")
    public List<Category> getCategoryTree() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>().orderByAsc(Category::getId));
    }

    @Override
    @CacheEvict(value = "categoryCache", allEntries = true)
    public void addCategory(Category category) {
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.insert(category);
    }

    @Override
    @CacheEvict(value = "categoryCache", allEntries = true)
    public void updateCategory(Category category) {
        categoryMapper.updateById(category);
    }

    @Override
    @CacheEvict(value = "categoryCache", allEntries = true)
    public void deleteCategory(Long id) {
        Long childCount = categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>().eq(Category::getParentId, id));
        if (childCount > 0) throw new BusinessException("该分类下有子分类，无法删除");
        Long bookCount = bookMapper.selectCount(
                new LambdaQueryWrapper<Book>().eq(Book::getCategoryId, id));
        if (bookCount > 0) throw new BusinessException("该分类下有" + bookCount + "本图书，无法删除");
        categoryMapper.deleteById(id);
    }
}
