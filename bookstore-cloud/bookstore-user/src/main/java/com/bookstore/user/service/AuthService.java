package com.bookstore.user.service;

import com.bookstore.dto.LoginDTO;
import com.bookstore.dto.RegisterDTO;
import com.bookstore.dto.ShopRegisterDTO;
import java.util.Map;

public interface AuthService {
    Map<String, Object> userLogin(LoginDTO dto);
    Map<String, Object> shopLogin(LoginDTO dto);
    Map<String, Object> adminLogin(LoginDTO dto);
    void userRegister(RegisterDTO dto);
    void shopRegister(ShopRegisterDTO dto);
}
