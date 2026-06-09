package com.bookstore.book.service;

import com.bookstore.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> getCategoryTree();
    void addCategory(Category category);
    void updateCategory(Category category);
    void deleteCategory(Long id);
}
