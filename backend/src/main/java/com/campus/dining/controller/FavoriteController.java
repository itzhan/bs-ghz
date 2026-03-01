package com.campus.dining.controller;

import com.campus.dining.common.PageResult;
import com.campus.dining.common.Result;
import com.campus.dining.entity.Favorite;
import com.campus.dining.security.LoginUser;
import com.campus.dining.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * 添加收藏
     */
    @PostMapping
    public Result<Favorite> addFavorite(@RequestBody Map<String, Object> body) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();
        Long stallId = body.get("stallId") != null ? Long.valueOf(body.get("stallId").toString()) : null;
        Long dishId = body.get("dishId") != null ? Long.valueOf(body.get("dishId").toString()) : null;
        Integer type = body.get("type") != null ? Integer.valueOf(body.get("type").toString()) : null;
        return Result.success(favoriteService.addFavorite(userId, stallId, dishId, type));
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{id}")
    public Result<?> removeFavorite(@PathVariable Long id) {
        favoriteService.removeFavorite(id);
        return Result.success();
    }

    /**
     * 查询收藏列表
     */
    @GetMapping
    public Result<PageResult<Favorite>> listFavorites(
            @RequestParam(required = false) Integer type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();
        return Result.success(favoriteService.listFavorites(userId, type, page, size));
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check")
    public Result<Boolean> isFavorite(
            @RequestParam Long targetId,
            @RequestParam Integer type) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();
        return Result.success(favoriteService.isFavorite(userId, targetId, type));
    }
}
