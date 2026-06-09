package com.bookstore.user.feign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.common.Result;
import com.bookstore.entity.Address;
import com.bookstore.entity.Shop;
import com.bookstore.entity.User;
import com.bookstore.user.mapper.AddressMapper;
import com.bookstore.user.mapper.ShopMapper;
import com.bookstore.user.mapper.UserMapper;
import com.bookstore.user.mapper.CartMapper;
import com.bookstore.entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内部Feign接口 - 供其他微服务调用
 */
@RestController
@RequestMapping("/internal")
public class UserInternalController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private CartMapper cartMapper;

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user != null) user.setPassword(null);
        return user;
    }

    @GetMapping("/shop/{id}")
    public Shop getShopById(@PathVariable Long id) {
        return shopMapper.selectById(id);
    }

    @GetMapping("/shop/user/{userId}")
    public Shop getShopByUserId(@PathVariable Long userId) {
        return shopMapper.selectOne(
                new LambdaQueryWrapper<Shop>().eq(Shop::getUserId, userId));
    }

    @GetMapping("/address/{id}")
    public Address getAddressById(@PathVariable Long id, @RequestParam Long userId) {
        Address address = addressMapper.selectById(id);
        if (address != null && address.getUserId().equals(userId)) {
            return address;
        }
        return null;
    }

    @DeleteMapping("/cart/{userId}/items")
    public void deleteCartItems(@PathVariable Long userId, @RequestBody List<Long> bookIds) {
        cartMapper.delete(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getUserId, userId)
                        .in(Cart::getBookId, bookIds));
    }
}
