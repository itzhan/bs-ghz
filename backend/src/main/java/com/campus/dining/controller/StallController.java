package com.campus.dining.controller;

import com.campus.dining.common.PageResult;
import com.campus.dining.common.Result;
import com.campus.dining.entity.Activity;
import com.campus.dining.entity.DishCategory;
import com.campus.dining.entity.Review;
import com.campus.dining.entity.Stall;
import com.campus.dining.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stalls")
@RequiredArgsConstructor
public class StallController {

    private final StallService stallService;
    private final DishService dishService;
    private final DishCategoryService dishCategoryService;
    private final ReviewService reviewService;
    private final ActivityService activityService;

    /**
     * 分页查询档口列表（公开）
     */
    @GetMapping
    public Result<PageResult<Stall>> listStalls(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(stallService.listStalls(page, size, keyword, status));
    }

    /**
     * 获取档口详情（公开）
     */
    @GetMapping("/{id}")
    public Result<Stall> getStall(@PathVariable Long id) {
        return Result.success(stallService.getStallById(id));
    }

    /**
     * 获取档口下菜品列表（公开）
     */
    @GetMapping("/{id}/dishes")
    public Result<?> listDishesByStall(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        return Result.success(dishService.listDishes(page, size, id, categoryId, keyword, null));
    }

    /**
     * 获取档口下分类列表（公开）
     */
    @GetMapping("/{id}/categories")
    public Result<List<DishCategory>> listCategoriesByStall(@PathVariable Long id) {
        return Result.success(dishCategoryService.listByStall(id));
    }

    /**
     * 获取档口下评价列表（公开）
     */
    @GetMapping("/{id}/reviews")
    public Result<PageResult<Review>> listReviewsByStall(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(reviewService.listReviewsByStall(id, page, size));
    }

    /**
     * 获取档口下进行中的活动（公开）
     */
    @GetMapping("/{id}/activities")
    public Result<List<Activity>> listActiveActivities(@PathVariable Long id) {
        return Result.success(activityService.listActiveByStall(id));
    }
}
