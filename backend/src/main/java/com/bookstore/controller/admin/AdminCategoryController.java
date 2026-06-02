package com.bookstore.controller.admin;

import com.bookstore.common.Result;
import com.bookstore.entity.Category;
import com.bookstore.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员端分类管理接口
 */
@Tag(name = "管理员端分类管理", description = "分类管理")
@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "分类列表")
    @GetMapping
    public Result<List<Category>> getCategoryList() {
        return Result.success(categoryService.getCategoryTree());
    }

    @Operation(summary = "新增分类")
    @PostMapping
    public Result<?> addCategory(@RequestBody Category category) {
        categoryService.addCategory(category);
        return Result.success("添加成功");
    }

    @Operation(summary = "编辑分类")
    @PutMapping("/{id}")
    public Result<?> updateCategory(@PathVariable Long id,
                                    @RequestBody Category category) {
        category.setId(id);
        categoryService.updateCategory(category);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("删除成功");
    }
}
