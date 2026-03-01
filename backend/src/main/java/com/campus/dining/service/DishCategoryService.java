package com.campus.dining.service;

import com.campus.dining.dto.DishCategoryDTO;
import com.campus.dining.entity.DishCategory;

import java.util.List;

public interface DishCategoryService {

    /**
     * 创建菜品分类
     */
    DishCategory create(DishCategoryDTO dto);

    /**
     * 更新菜品分类
     */
    DishCategory update(Long id, DishCategoryDTO dto);

    /**
     * 删除菜品分类
     */
    void delete(Long id);

    /**
     * 获取档口下的分类列表
     */
    List<DishCategory> listByStall(Long stallId);
}
