package com.bookstore.service;

import com.bookstore.dto.LoginDTO;
import com.bookstore.dto.RegisterDTO;
import com.bookstore.dto.ShopRegisterDTO;

import java.util.Map;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    Map<String, Object> userLogin(LoginDTO dto);

    /**
     * 商家登录
     */
    Map<String, Object> shopLogin(LoginDTO dto);

    /**
     * 管理员登录
     */
    Map<String, Object> adminLogin(LoginDTO dto);

    /**
     * 用户注册
     */
    void userRegister(RegisterDTO dto);

    /**
     * 商家注册
     */
    void shopRegister(ShopRegisterDTO dto);
}
