package com.campus.dining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.dining.dto.DishCategoryDTO;
import com.campus.dining.entity.DishCategory;
import com.campus.dining.exception.BusinessException;
import com.campus.dining.mapper.DishCategoryMapper;
import com.campus.dining.service.DishCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DishCategoryServiceImpl implements DishCategoryService {

    private final DishCategoryMapper dishCategoryMapper;

    @Override
    public DishCategory create(DishCategoryDTO dto) {
        DishCategory category = new DishCategory();
        category.setStallId(dto.getStallId());
        category.setName(dto.getName());
        category.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        dishCategoryMapper.insert(category);
        return category;
    }

    @Override
    public DishCategory update(Long id, DishCategoryDTO dto) {
        DishCategory category = dishCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        if (StringUtils.hasText(dto.getName())) {
            category.setName(dto.getName());
        }
        if (dto.getSortOrder() != null) {
            category.setSortOrder(dto.getSortOrder());
        }
        dishCategoryMapper.updateById(category);
        return category;
    }

    @Override
    public void delete(Long id) {
        DishCategory category = dishCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        dishCategoryMapper.deleteById(id);
    }

    @Override
    public List<DishCategory> listByStall(Long stallId) {
        return dishCategoryMapper.selectList(
                new LambdaQueryWrapper<DishCategory>()
                        .eq(DishCategory::getStallId, stallId)
                        .orderByAsc(DishCategory::getSortOrder)
        );
    }
}
