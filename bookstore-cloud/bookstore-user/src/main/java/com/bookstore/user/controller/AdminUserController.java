package com.bookstore.user.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.common.PageResult;
import com.bookstore.common.Result;
import com.bookstore.entity.User;
import com.bookstore.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    public Result<PageResult<User>> getList(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword);
        }
        wrapper.orderByAsc(User::getId);
        List<User> users = userMapper.selectList(wrapper);
        int total = users.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<User> pageUsers = start < total ? users.subList(start, end) : List.of();
        return Result.success(new PageResult<>(pageUsers, (long) total, page, size));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) return Result.error("用户不存在");
        user.setStatus(status);
        userMapper.updateById(user);
        return Result.success();
    }
}
