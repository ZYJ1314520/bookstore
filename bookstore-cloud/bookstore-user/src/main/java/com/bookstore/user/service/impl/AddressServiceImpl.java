package com.bookstore.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.common.BusinessException;
import com.bookstore.dto.AddressDTO;
import com.bookstore.entity.Address;
import com.bookstore.user.mapper.AddressMapper;
import com.bookstore.user.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public List<Address> getAddressList(Long userId) {
        return addressMapper.selectList(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .orderByDesc(Address::getIsDefault)
                        .orderByDesc(Address::getCreateTime));
    }

    @Override
    public void addAddress(Long userId, AddressDTO dto) {
        Long count = addressMapper.selectCount(
                new LambdaQueryWrapper<Address>().eq(Address::getUserId, userId));
        if (count >= 20) throw new BusinessException("地址数量已达上限");

        Address address = new Address();
        address.setUserId(userId);
        address.setReceiverName(dto.getReceiverName());
        address.setPhone(dto.getPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetailAddress(dto.getDetailAddress());
        address.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : 0);
        if (address.getIsDefault() == 1) clearDefaultAddress(userId);
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        addressMapper.insert(address);
    }

    @Override
    public void updateAddress(Long userId, AddressDTO dto) {
        Address address = addressMapper.selectById(dto.getId());
        if (address == null || !address.getUserId().equals(userId)) throw new BusinessException("地址不存在");
        address.setReceiverName(dto.getReceiverName());
        address.setPhone(dto.getPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetailAddress(dto.getDetailAddress());
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            clearDefaultAddress(userId);
            address.setIsDefault(1);
        }
        addressMapper.updateById(address);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) throw new BusinessException("地址不存在");
        addressMapper.deleteById(addressId);
    }

    @Override
    public void setDefaultAddress(Long userId, Long addressId) {
        Address address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) throw new BusinessException("地址不存在");
        clearDefaultAddress(userId);
        address.setIsDefault(1);
        addressMapper.updateById(address);
    }

    private void clearDefaultAddress(Long userId) {
        List<Address> addresses = addressMapper.selectList(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .eq(Address::getIsDefault, 1));
        for (Address addr : addresses) {
            addr.setIsDefault(0);
            addressMapper.updateById(addr);
        }
    }
}
