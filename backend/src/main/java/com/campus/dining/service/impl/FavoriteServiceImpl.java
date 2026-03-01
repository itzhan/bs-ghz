package com.campus.dining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.dining.common.Constants;
import com.campus.dining.common.PageResult;
import com.campus.dining.entity.Favorite;
import com.campus.dining.exception.BusinessException;
import com.campus.dining.mapper.FavoriteMapper;
import com.campus.dining.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;

    @Override
    public Favorite addFavorite(Long userId, Long stallId, Long dishId, Integer type) {
        // 检查是否已收藏
        LambdaQueryWrapper<Favorite> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(Favorite::getUserId, userId).eq(Favorite::getType, type);
        if (type == Constants.FAV_STALL) {
            checkWrapper.eq(Favorite::getStallId, stallId);
        } else {
            checkWrapper.eq(Favorite::getDishId, dishId);
        }
        Long count = favoriteMapper.selectCount(checkWrapper);
        if (count > 0) {
            throw new BusinessException("已收藏，请勿重复操作");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setStallId(stallId);
        favorite.setDishId(dishId);
        favorite.setType(type);
        favoriteMapper.insert(favorite);
        return favorite;
    }

    @Override
    public void removeFavorite(Long id) {
        favoriteMapper.deleteById(id);
    }

    @Override
    public PageResult<Favorite> listFavorites(Long userId, Integer type, int page, int size) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        if (type != null) {
            wrapper.eq(Favorite::getType, type);
        }
        wrapper.orderByDesc(Favorite::getCreatedAt);

        Page<Favorite> pageResult = favoriteMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    public boolean isFavorite(Long userId, Long targetId, Integer type) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getType, type);
        if (type == Constants.FAV_STALL) {
            wrapper.eq(Favorite::getStallId, targetId);
        } else {
            wrapper.eq(Favorite::getDishId, targetId);
        }
        return favoriteMapper.selectCount(wrapper) > 0;
    }
}
