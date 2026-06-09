package com.bookstore.user.controller;

import com.bookstore.common.Result;
import com.bookstore.dto.AddressDTO;
import com.bookstore.entity.Address;
import com.bookstore.user.service.AddressService;
import com.bookstore.utils.UserContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    public Result<List<Address>> getList() {
        return Result.success(addressService.getAddressList(UserContext.getUserId()));
    }

    @PostMapping
    public Result<Void> add(@Valid @RequestBody AddressDTO dto) {
        addressService.addAddress(UserContext.getUserId(), dto);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody AddressDTO dto) {
        addressService.updateAddress(UserContext.getUserId(), dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        addressService.deleteAddress(UserContext.getUserId(), id);
        return Result.success();
    }

    @PutMapping("/{id}/default")
    public Result<Void> setDefault(@PathVariable Long id) {
        addressService.setDefaultAddress(UserContext.getUserId(), id);
        return Result.success();
    }
}
