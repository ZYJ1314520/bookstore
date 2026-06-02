package com.bookstore.service;

import com.bookstore.dto.AddressDTO;
import com.bookstore.entity.Address;

import java.util.List;

/**
 * 地址服务接口
 */
public interface AddressService {

    /**
     * 获取地址列表
     */
    List<Address> getAddressList(Long userId);

    /**
     * 新增地址
     */
    void addAddress(Long userId, AddressDTO dto);

    /**
     * 编辑地址
     */
    void updateAddress(Long userId, AddressDTO dto);

    /**
     * 删除地址
     */
    void deleteAddress(Long userId, Long addressId);

    /**
     * 设置默认地址
     */
    void setDefaultAddress(Long userId, Long addressId);
}
