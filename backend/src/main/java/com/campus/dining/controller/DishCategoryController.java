package com.campus.dining.controller;

import com.campus.dining.common.Result;
import com.campus.dining.entity.DishCategory;
import com.campus.dining.service.DishCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dish-categories")
@RequiredArgsConstructor
public class DishCategoryController {

    private final DishCategoryService dishCategoryService;

    /**
     * 根据档口ID查询分类列表（公开）
     */
    @GetMapping
    public Result<List<DishCategory>> listByStall(@RequestParam Long stallId) {
        return Result.success(dishCategoryService.listByStall(stallId));
    }
}
