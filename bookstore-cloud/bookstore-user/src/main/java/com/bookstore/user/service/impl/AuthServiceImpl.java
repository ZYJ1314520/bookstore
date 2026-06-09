package com.bookstore.user.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.common.BusinessException;
import com.bookstore.dto.LoginDTO;
import com.bookstore.dto.RegisterDTO;
import com.bookstore.dto.ShopRegisterDTO;
import com.bookstore.entity.Admin;
import com.bookstore.entity.Shop;
import com.bookstore.entity.User;
import com.bookstore.user.mapper.AdminMapper;
import com.bookstore.user.mapper.ShopMapper;
import com.bookstore.user.mapper.UserMapper;
import com.bookstore.user.service.AuthService;
import com.bookstore.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Map<String, Object> userLogin(LoginDTO dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null) throw new BusinessException("用户名或密码错误");
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) throw new BusinessException("用户名或密码错误");
        if (user.getStatus() == 0) throw new BusinessException("账号已被禁用");

        String token = jwtUtils.generateToken(user.getId(), 1);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", buildUserInfo(user, 1));
        return result;
    }

    @Override
    public Map<String, Object> shopLogin(LoginDTO dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null) throw new BusinessException("用户名或密码错误");
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) throw new BusinessException("用户名或密码错误");

        Shop shop = shopMapper.selectOne(
                new LambdaQueryWrapper<Shop>().eq(Shop::getUserId, user.getId()));
        if (shop == null) throw new BusinessException("您还不是商家，请先注册");
        if (shop.getStatus() == 2) throw new BusinessException("店铺已被禁用");
        if (shop.getStatus() == 0) throw new BusinessException("店铺审核中，请耐心等待");
        if (shop.getStatus() == 3) throw new BusinessException("店铺审核未通过：" + shop.getAuditRemark());

        String token = jwtUtils.generateToken(user.getId(), 2);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", buildUserInfo(user, 2));
        result.put("shopInfo", shop);
        return result;
    }

    @Override
    public Map<String, Object> adminLogin(LoginDTO dto) {
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, dto.getUsername()));
        if (admin == null) throw new BusinessException("用户名或密码错误");
        if (!BCrypt.checkpw(dto.getPassword(), admin.getPassword())) throw new BusinessException("用户名或密码错误");

        String token = jwtUtils.generateToken(admin.getId(), 0);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", admin.getId());
        userInfo.put("username", admin.getUsername());
        userInfo.put("role", 0);
        result.put("userInfo", userInfo);
        return result;
    }

    @Override
    @Transactional
    public void userRegister(RegisterDTO dto) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (count > 0) throw new BusinessException("用户名已存在");

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
    }

    @Override
    @Transactional
    public void shopRegister(ShopRegisterDTO dto) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (count > 0) throw new BusinessException("用户名已存在");

        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setNickname(dto.getShopName());
        user.setPhone(dto.getContactPhone());
        user.setCreateTime(now);
        user.setUpdateTime(now);
        userMapper.insert(user);

        Shop shop = new Shop();
        shop.setUserId(user.getId());
        shop.setShopName(dto.getShopName());
        shop.setContactPhone(dto.getContactPhone());
        shop.setContactEmail(dto.getContactEmail());
        shop.setDescription(dto.getDescription());
        shop.setStatus(0);
        shop.setCreateTime(now);
        shop.setUpdateTime(now);
        shopMapper.insert(shop);
    }

    private Map<String, Object> buildUserInfo(User user, Integer role) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("nickname", user.getNickname());
        info.put("avatar", user.getAvatar());
        info.put("phone", user.getPhone());
        info.put("role", role);
        return info;
    }
}
