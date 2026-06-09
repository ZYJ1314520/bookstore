package com.bookstore.book.controller;

import com.bookstore.book.service.CategoryService;
import com.bookstore.common.Result;
import com.bookstore.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public Result<List<Category>> getList() {
        return Result.success(categoryService.getCategoryTree());
    }

    @PostMapping
    public Result<Void> add(@RequestBody Category category) {
        categoryService.addCategory(category);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Category category) {
        categoryService.updateCategory(category);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}
