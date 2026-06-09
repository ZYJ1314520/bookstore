package com.bookstore.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.common.BusinessException;
import com.bookstore.entity.Book;
import com.bookstore.entity.Favorite;
import com.bookstore.user.feign.BookFeignClient;
import com.bookstore.user.mapper.FavoriteMapper;
import com.bookstore.user.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private BookFeignClient bookFeignClient;

    @Override
    public List<Favorite> getFavorites(Long userId) {
        return favoriteMapper.selectList(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getCreateTime));
    }

    @Override
    public void addFavorite(Long userId, Long bookId) {
        // 检查图书是否存在
        Book book = bookFeignClient.getBookById(bookId);
        if (book == null) throw new BusinessException("图书不存在");

        // 检查是否已收藏
        Long count = favoriteMapper.selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getBookId, bookId));
        if (count > 0) throw new BusinessException("已收藏该图书");

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setBookId(bookId);
        favorite.setCreateTime(LocalDateTime.now());
        favoriteMapper.insert(favorite);
    }

    @Override
    public void removeFavorite(Long userId, Long favoriteId) {
        Favorite favorite = favoriteMapper.selectById(favoriteId);
        if (favorite == null || !favorite.getUserId().equals(userId)) {
            throw new BusinessException("收藏记录不存在");
        }
        favoriteMapper.deleteById(favoriteId);
    }

    @Override
    public boolean isFavorite(Long userId, Long bookId) {
        Long count = favoriteMapper.selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getBookId, bookId));
        return count > 0;
    }
}
