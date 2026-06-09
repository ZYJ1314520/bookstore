package com.bookstore.user.service;

import com.bookstore.entity.Favorite;

import java.util.List;

public interface FavoriteService {
    List<Favorite> getFavorites(Long userId);
    void addFavorite(Long userId, Long bookId);
    void removeFavorite(Long userId, Long favoriteId);
    boolean isFavorite(Long userId, Long bookId);
}
