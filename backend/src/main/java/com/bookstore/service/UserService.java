package com.bookstore.service;

import com.bookstore.dto.PasswordDTO;
import com.bookstore.dto.ProfileDTO;
import com.bookstore.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 获取用户信息
     */
    User getUserInfo(Long userId);

    /**
     * 修改用户资料
     */
    void updateProfile(Long userId, ProfileDTO dto);

    /**
     * 修改密码
     */
    void updatePassword(Long userId, PasswordDTO dto);
}
