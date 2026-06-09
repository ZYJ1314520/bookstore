package com.bookstore.user.service;

import com.bookstore.dto.AddressDTO;
import com.bookstore.entity.Address;
import java.util.List;

public interface AddressService {
    List<Address> getAddressList(Long userId);
    void addAddress(Long userId, AddressDTO dto);
    void updateAddress(Long userId, AddressDTO dto);
    void deleteAddress(Long userId, Long addressId);
    void setDefaultAddress(Long userId, Long addressId);
}
