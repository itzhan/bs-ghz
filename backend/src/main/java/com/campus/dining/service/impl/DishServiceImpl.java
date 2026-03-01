package com.campus.dining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.dining.common.PageResult;
import com.campus.dining.dto.DishDTO;
import com.campus.dining.entity.Dish;
import com.campus.dining.exception.BusinessException;
import com.campus.dining.mapper.DishMapper;
import com.campus.dining.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {

    private final DishMapper dishMapper;

    @Override
    public Dish createDish(DishDTO dto) {
        Dish dish = new Dish();
        dish.setStallId(dto.getStallId());
        dish.setCategoryId(dto.getCategoryId());
        dish.setName(dto.getName());
        dish.setDescription(dto.getDescription());
        dish.setImage(dto.getImage());
        dish.setPrice(dto.getPrice());
        dish.setOriginalPrice(dto.getOriginalPrice());
        dish.setStatus(0); // 待审核
        dish.setStock(dto.getStock() != null ? dto.getStock() : 999);
        dish.setDailyLimit(dto.getDailyLimit());
        dish.setWarningThreshold(dto.getWarningThreshold());
        dish.setMonthlySales(0);
        dish.setRating(BigDecimal.ZERO);
        dish.setIsRecommended(dto.getIsRecommended() != null ? dto.getIsRecommended() : 0);
        dishMapper.insert(dish);
        return dish;
    }

    @Override
    public Dish updateDish(Long id, DishDTO dto) {
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException("菜品不存在");
        }
        if (dto.getCategoryId() != null) {
            dish.setCategoryId(dto.getCategoryId());
        }
        if (StringUtils.hasText(dto.getName())) {
            dish.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            dish.setDescription(dto.getDescription());
        }
        if (dto.getImage() != null) {
            dish.setImage(dto.getImage());
        }
        if (dto.getPrice() != null) {
            dish.setPrice(dto.getPrice());
        }
        if (dto.getOriginalPrice() != null) {
            dish.setOriginalPrice(dto.getOriginalPrice());
        }
        if (dto.getStock() != null) {
            dish.setStock(dto.getStock());
        }
        if (dto.getDailyLimit() != null) {
            dish.setDailyLimit(dto.getDailyLimit());
        }
        if (dto.getWarningThreshold() != null) {
            dish.setWarningThreshold(dto.getWarningThreshold());
        }
        if (dto.getIsRecommended() != null) {
            dish.setIsRecommended(dto.getIsRecommended());
        }
        dishMapper.updateById(dish);
        return dish;
    }

    @Override
    public Dish getDishById(Long id) {
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException("菜品不存在");
        }
        return dish;
    }

    @Override
    public PageResult<Dish> listDishes(int page, int size, Long stallId, Long categoryId, String keyword, Integer status) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        if (stallId != null) {
            wrapper.eq(Dish::getStallId, stallId);
        }
        if (categoryId != null) {
            wrapper.eq(Dish::getCategoryId, categoryId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Dish::getName, keyword);
        }
        if (status != null) {
            wrapper.eq(Dish::getStatus, status);
        }
        wrapper.orderByDesc(Dish::getCreatedAt);

        Page<Dish> pageResult = dishMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    public void updateDishStatus(Long id, Integer status) {
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException("菜品不存在");
        }
        dish.setStatus(status);
        dishMapper.updateById(dish);
    }

    @Override
    public void auditDish(Long id, Integer status) {
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException("菜品不存在");
        }
        dish.setStatus(status);
        dishMapper.updateById(dish);
    }

    @Override
    public void updateStock(Long id, Integer stock) {
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException("菜品不存在");
        }
        dish.setStock(stock);
        dishMapper.updateById(dish);
    }
}
