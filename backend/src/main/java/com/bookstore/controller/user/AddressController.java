package com.bookstore.controller.user;

import com.bookstore.common.Result;
import com.bookstore.dto.AddressDTO;
import com.bookstore.entity.Address;
import com.bookstore.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端地址接口
 */
@Tag(name = "用户端地址", description = "收货地址管理")
@RestController
@RequestMapping("/api/user/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Operation(summary = "获取地址列表")
    @GetMapping
    public Result<List<Address>> getAddressList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(addressService.getAddressList(userId));
    }

    @Operation(summary = "新增地址")
    @PostMapping
    public Result<?> addAddress(HttpServletRequest request,
                                @Valid @RequestBody AddressDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        addressService.addAddress(userId, dto);
        return Result.success("添加成功");
    }

    @Operation(summary = "编辑地址")
    @PutMapping("/{id}")
    public Result<?> updateAddress(HttpServletRequest request,
                                   @PathVariable Long id,
                                   @Valid @RequestBody AddressDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        dto.setId(id);
        addressService.updateAddress(userId, dto);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除地址")
    @DeleteMapping("/{id}")
    public Result<?> deleteAddress(HttpServletRequest request,
                                   @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        addressService.deleteAddress(userId, id);
        return Result.success("删除成功");
    }

    @Operation(summary = "设置默认地址")
    @PutMapping("/{id}/default")
    public Result<?> setDefaultAddress(HttpServletRequest request,
                                       @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        addressService.setDefaultAddress(userId, id);
        return Result.success("设置成功");
    }
}
