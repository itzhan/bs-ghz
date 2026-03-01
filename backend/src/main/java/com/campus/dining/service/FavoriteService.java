package com.campus.dining.service;

import com.campus.dining.common.PageResult;
import com.campus.dining.entity.Favorite;

public interface FavoriteService {

    /**
     * 添加收藏
     */
    Favorite addFavorite(Long userId, Long stallId, Long dishId, Integer type);

    /**
     * 取消收藏
     */
    void removeFavorite(Long id);

    /**
     * 分页查询用户收藏列表
     */
    PageResult<Favorite> listFavorites(Long userId, Integer type, int page, int size);

    /**
     * 判断是否已收藏
     */
    boolean isFavorite(Long userId, Long targetId, Integer type);
}
