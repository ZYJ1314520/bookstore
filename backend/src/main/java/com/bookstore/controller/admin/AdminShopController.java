package com.bookstore.controller.admin;

import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.entity.Shop;
import com.bookstore.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员端商家管理接口
 */
@Tag(name = "管理员端商家管理", description = "商家审核、管理")
@RestController
@RequestMapping("/api/admin/shops")
public class AdminShopController {

    @Autowired
    private AdminService adminService;

    @Operation(summary = "商家列表")
    @GetMapping
    public Result<PageResult<Shop>> getShopList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(adminService.getShopList(status, page, size));
    }

    @Operation(summary = "审核商家")
    @PutMapping("/{id}/audit")
    public Result<?> auditShop(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String remark) {
        adminService.auditShop(id, status, remark);
        return Result.success("审核完成");
    }
}
