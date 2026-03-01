package com.campus.dining.controller;

import com.campus.dining.common.PageResult;
import com.campus.dining.common.Result;
import com.campus.dining.entity.Dish;
import com.campus.dining.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;

    /**
     * 分页查询菜品列表（公开）
     */
    @GetMapping
    public Result<PageResult<Dish>> listDishes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long stallId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(dishService.listDishes(page, size, stallId, categoryId, keyword, status));
    }

    /**
     * 获取菜品详情（公开）
     */
    @GetMapping("/{id}")
    public Result<Dish> getDish(@PathVariable Long id) {
        return Result.success(dishService.getDishById(id));
    }
}
