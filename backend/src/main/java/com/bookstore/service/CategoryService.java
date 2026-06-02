package com.bookstore.service;

import com.bookstore.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {

    /**
     * 获取分类树
     */
    List<Category> getCategoryTree();

    /**
     * 新增分类
     */
    void addCategory(Category category);

    /**
     * 编辑分类
     */
    void updateCategory(Category category);

    /**
     * 删除分类
     */
    void deleteCategory(Long id);
}
