package com.campus.dining.service;

import com.campus.dining.common.PageResult;
import com.campus.dining.dto.DishDTO;
import com.campus.dining.entity.Dish;

public interface DishService {

    /**
     * 创建菜品
     */
    Dish createDish(DishDTO dto);

    /**
     * 更新菜品
     */
    Dish updateDish(Long id, DishDTO dto);

    /**
     * 根据ID获取菜品
     */
    Dish getDishById(Long id);

    /**
     * 分页查询菜品列表
     */
    PageResult<Dish> listDishes(int page, int size, Long stallId, Long categoryId, String keyword, Integer status);

    /**
     * 上架/下架菜品
     */
    void updateDishStatus(Long id, Integer status);

    /**
     * 管理员审核菜品
     */
    void auditDish(Long id, Integer status);

    /**
     * 更新库存
     */
    void updateStock(Long id, Integer stock);
}
