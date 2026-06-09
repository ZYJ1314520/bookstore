package com.bookstore.user.service;

import com.bookstore.dto.PasswordDTO;
import com.bookstore.dto.ProfileDTO;
import com.bookstore.entity.User;

public interface UserService {
    User getUserInfo(Long userId);
    void updateProfile(Long userId, ProfileDTO dto);
    void updatePassword(Long userId, PasswordDTO dto);
}
