package com.campus.dining.controller;

import com.campus.dining.common.PageResult;
import com.campus.dining.common.Result;
import com.campus.dining.dto.*;
import com.campus.dining.entity.*;
import com.campus.dining.security.LoginUser;
import com.campus.dining.service.*;
import com.campus.dining.service.impl.StatsServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MERCHANT')")
public class MerchantController {

    private final StallService stallService;
    private final DishService dishService;
    private final DishCategoryService dishCategoryService;
    private final OrderService orderService;
    private final ReviewService reviewService;
    private final AnnouncementService announcementService;
    private final ActivityService activityService;
    private final StatsServiceImpl statsService;

    // ==================== 档口管理 ====================

    /**
     * 获取商家自己的档口列表
     */
    @GetMapping("/stalls")
    public Result<List<Stall>> listMyStalls() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long merchantId = loginUser.getUser().getId();
        return Result.success(stallService.getStallsByMerchant(merchantId));
    }

    /**
     * 创建档口
     */
    @PostMapping("/stalls")
    public Result<Stall> createStall(@Valid @RequestBody StallDTO dto) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long merchantId = loginUser.getUser().getId();
        return Result.success(stallService.createStall(merchantId, dto));
    }

    /**
     * 更新档口
     */
    @PutMapping("/stalls/{id}")
    public Result<Stall> updateStall(@PathVariable Long id, @Valid @RequestBody StallDTO dto) {
        return Result.success(stallService.updateStall(id, dto));
    }

    /**
     * 更新档口状态（营业/休息/关闭）
     */
    @PutMapping("/stalls/{id}/status")
    public Result<?> updateStallStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        stallService.updateStallStatus(id, status);
        return Result.success();
    }

    // ==================== 菜品管理 ====================

    /**
     * 创建菜品
     */
    @PostMapping("/dishes")
    public Result<Dish> createDish(@Valid @RequestBody DishDTO dto) {
        return Result.success(dishService.createDish(dto));
    }

    /**
     * 更新菜品
     */
    @PutMapping("/dishes/{id}")
    public Result<Dish> updateDish(@PathVariable Long id, @Valid @RequestBody DishDTO dto) {
        return Result.success(dishService.updateDish(id, dto));
    }

    /**
     * 更新菜品状态（上架/下架）
     */
    @PutMapping("/dishes/{id}/status")
    public Result<?> updateDishStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        dishService.updateDishStatus(id, status);
        return Result.success();
    }

    /**
     * 更新库存
     */
    @PutMapping("/dishes/{id}/stock")
    public Result<?> updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer stock = body.get("stock");
        dishService.updateStock(id, stock);
        return Result.success();
    }

    // ==================== 分类管理 ====================

    /**
     * 创建分类
     */
    @PostMapping("/categories")
    public Result<DishCategory> createCategory(@Valid @RequestBody DishCategoryDTO dto) {
        return Result.success(dishCategoryService.create(dto));
    }

    /**
     * 更新分类
     */
    @PutMapping("/categories/{id}")
    public Result<DishCategory> updateCategory(@PathVariable Long id, @Valid @RequestBody DishCategoryDTO dto) {
        return Result.success(dishCategoryService.update(id, dto));
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/categories/{id}")
    public Result<?> deleteCategory(@PathVariable Long id) {
        dishCategoryService.delete(id);
        return Result.success();
    }

    // ==================== 订单管理 ====================

    /**
     * 分页查询档口订单
     */
    @GetMapping("/orders")
    public Result<PageResult<OrderInfo>> listStallOrders(
            @RequestParam Long stallId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        return Result.success(orderService.listStallOrders(stallId, page, size, status));
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/orders/{id}/status")
    public Result<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        orderService.updateOrderStatus(id, status);
        return Result.success();
    }

    // ==================== 评价管理 ====================

    /**
     * 回复评价
     */
    @PutMapping("/reviews/{id}/reply")
    public Result<?> replyReview(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String replyContent = body.get("replyContent");
        reviewService.replyReview(id, replyContent);
        return Result.success();
    }

    // ==================== 公告管理 ====================

    /**
     * 创建商家公告
     */
    @PostMapping("/announcements")
    public Result<Announcement> createAnnouncement(@Valid @RequestBody AnnouncementDTO dto) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long publisherId = loginUser.getUser().getId();
        return Result.success(announcementService.create(dto, publisherId));
    }

    /**
     * 更新公告
     */
    @PutMapping("/announcements/{id}")
    public Result<Announcement> updateAnnouncement(@PathVariable Long id, @Valid @RequestBody AnnouncementDTO dto) {
        return Result.success(announcementService.update(id, dto));
    }

    /**
     * 发布公告
     */
    @PutMapping("/announcements/{id}/publish")
    public Result<?> publishAnnouncement(@PathVariable Long id) {
        announcementService.publish(id);
        return Result.success();
    }

    /**
     * 下架公告
     */
    @PutMapping("/announcements/{id}/takedown")
    public Result<?> takeDownAnnouncement(@PathVariable Long id) {
        announcementService.takeDown(id);
        return Result.success();
    }

    // ==================== 活动管理 ====================

    /**
     * 创建活动
     */
    @PostMapping("/activities")
    public Result<Activity> createActivity(@Valid @RequestBody ActivityDTO dto) {
        return Result.success(activityService.create(dto));
    }

    /**
     * 更新活动
     */
    @PutMapping("/activities/{id}")
    public Result<Activity> updateActivity(@PathVariable Long id, @Valid @RequestBody ActivityDTO dto) {
        return Result.success(activityService.update(id, dto));
    }

    /**
     * 删除活动
     */
    @DeleteMapping("/activities/{id}")
    public Result<?> deleteActivity(@PathVariable Long id) {
        activityService.delete(id);
        return Result.success();
    }

    // ==================== 统计数据 ====================

    /**
     * 获取档口统计
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStallStats(@RequestParam Long stallId) {
        return Result.success(statsService.getStallStats(stallId));
    }

    /**
     * 获取菜品销量排名
     */
    @GetMapping("/stats/dishes")
    public Result<List<Map<String, Object>>> getDishRanking(
            @RequestParam Long stallId,
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(statsService.getDishRanking(stallId, limit));
    }

    /**
     * 获取收入统计
     */
    @GetMapping("/stats/revenue")
    public Result<List<Map<String, Object>>> getRevenueStats(
            @RequestParam Long stallId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return Result.success(statsService.getRevenueStats(stallId, startDate, endDate));
    }
}
