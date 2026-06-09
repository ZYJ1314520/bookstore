package com.bookstore.user.controller;

import com.bookstore.common.Result;
import com.bookstore.entity.Favorite;
import com.bookstore.user.service.FavoriteService;
import com.bookstore.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping
    public Result<List<Favorite>> getList() {
        return Result.success(favoriteService.getFavorites(UserContext.getUserId()));
    }

    @PostMapping
    public Result<Void> add(@RequestParam Long bookId) {
        favoriteService.addFavorite(UserContext.getUserId(), bookId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        favoriteService.removeFavorite(UserContext.getUserId(), id);
        return Result.success();
    }

    @GetMapping("/check")
    public Result<Map<String, Boolean>> check(@RequestParam Long bookId) {
        boolean isFav = favoriteService.isFavorite(UserContext.getUserId(), bookId);
        return Result.success(Map.of("isFavorite", isFav));
    }
}
