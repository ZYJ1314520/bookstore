package com.bookstore.controller.admin;

import com.bookstore.common.Result;
import com.bookstore.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员端数据大屏接口
 */
@Tag(name = "管理员端数据大屏", description = "数据统计")
@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private AdminService adminService;

    @Operation(summary = "获取统计数据")
    @GetMapping
    public Result<?> getStats() {
        return Result.success(adminService.getDashboardStats());
    }

    @Operation(summary = "获取销售趋势")
    @GetMapping("/sales")
    public Result<?> getSalesTrend(
            @RequestParam(defaultValue = "7") Integer days) {
        return Result.success(adminService.getSalesTrend(days));
    }

    @Operation(summary = "热销图书排行")
    @GetMapping("/hot-books")
    public Result<?> getHotBooks(
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(adminService.getHotBooks(limit));
    }

    @Operation(summary = "商家销售排行")
    @GetMapping("/shop-rank")
    public Result<?> getShopRank(
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(adminService.getShopRank(limit));
    }

    @Operation(summary = "分类销售占比")
    @GetMapping("/category-sales")
    public Result<?> getCategorySales() {
        return Result.success(adminService.getCategorySales());
    }
}
