package com.campus.dining.controller;

import com.campus.dining.common.PageResult;
import com.campus.dining.common.Result;
import com.campus.dining.dto.AnnouncementDTO;
import com.campus.dining.dto.StallDTO;
import com.campus.dining.dto.DishDTO;
import com.campus.dining.entity.*;
import com.campus.dining.security.LoginUser;
import com.campus.dining.service.*;
import com.campus.dining.vo.UserVO;
import com.campus.dining.service.impl.StatsServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import com.campus.dining.mapper.UserMapper;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final StallService stallService;
    private final DishService dishService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final ReviewService reviewService;
    private final ComplaintService complaintService;
    private final AnnouncementService announcementService;
    private final SysConfigService sysConfigService;
    private final StatsServiceImpl statsService;

    // ==================== 用户管理 ====================

    /**
     * 分页查询用户列表
     */
    @GetMapping("/users")
    public Result<PageResult<UserVO>> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) Integer status) {
        return Result.success(userService.listUsers(page, size, keyword, role, status));
    }

    /**
     * 更新用户状态（启用/禁用）
     */
    @PutMapping("/users/{id}/status")
    public Result<?> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        userService.updateUserStatus(id, status);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * 管理员编辑用户信息
     */
    @PutMapping("/users/{id}")
    public Result<?> adminUpdateUser(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        User user = userMapper.selectById(id);
        if (user == null) return Result.error("用户不存在");
        if (body.containsKey("nickname")) user.setNickname((String) body.get("nickname"));
        if (body.containsKey("phone")) user.setPhone((String) body.get("phone"));
        if (body.containsKey("email")) user.setEmail((String) body.get("email"));
        if (body.containsKey("role")) user.setRole((Integer) body.get("role"));
        if (body.containsKey("status")) user.setStatus((Integer) body.get("status"));
        userMapper.updateById(user);
        return Result.success();
    }

    // ==================== 档口审核 ====================

    /**
     * 审核档口
     */
    @PutMapping("/stalls/{id}/audit")
    public Result<?> auditStall(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        stallService.auditStall(id, status);
        return Result.success();
    }

    /**
     * 管理员编辑档口信息
     */
    @PutMapping("/stalls/{id}")
    public Result<?> adminUpdateStall(@PathVariable Long id, @RequestBody StallDTO dto) {
        return Result.success(stallService.updateStall(id, dto));
    }

    // ==================== 菜品审核 ====================

    /**
     * 审核菜品
     */
    @PutMapping("/dishes/{id}/audit")
    public Result<?> auditDish(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        dishService.auditDish(id, status);
        return Result.success();
    }

    /**
     * 管理员编辑菜品信息
     */
    @PutMapping("/dishes/{id}")
    public Result<?> adminUpdateDish(@PathVariable Long id, @RequestBody DishDTO dto) {
        return Result.success(dishService.updateDish(id, dto));
    }

    // ==================== 订单管理 ====================

    /**
     * 分页查询所有订单
     */
    @GetMapping("/orders")
    public Result<PageResult<OrderInfo>> listAllOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        return Result.success(orderService.listAllOrders(page, size, status, keyword));
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

    // ==================== 支付管理 ====================

    /**
     * 分页查询所有支付记录
     */
    @GetMapping("/payments")
    public Result<PageResult<Payment>> listPayments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(paymentService.listPayments(page, size));
    }

    // ==================== 评价管理 ====================

    /**
     * 查询所有评价
     */
    @GetMapping("/reviews")
    public Result<PageResult<Review>> listAllReviews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(reviewService.listAllReviews(page, size));
    }

    /**
     * 更新评价状态（显示/隐藏）
     */
    @PutMapping("/reviews/{id}/status")
    public Result<?> updateReviewStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        reviewService.updateReviewStatus(id, status);
        return Result.success();
    }

    // ==================== 投诉管理 ====================

    /**
     * 查询所有投诉
     */
    @GetMapping("/complaints")
    public Result<PageResult<Complaint>> listAllComplaints(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        return Result.success(complaintService.listAllComplaints(page, size, status));
    }

    /**
     * 处理投诉
     */
    @PutMapping("/complaints/{id}/handle")
    public Result<?> handleComplaint(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long handlerId = loginUser.getUser().getId();
        Integer status = body.get("status") != null ? Integer.valueOf(body.get("status").toString()) : null;
        String replyContent = body.get("replyContent") != null ? body.get("replyContent").toString() : null;
        complaintService.handleComplaint(id, handlerId, status, replyContent);
        return Result.success();
    }

    // ==================== 公告管理 ====================

    /**
     * 创建系统公告
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
     * 删除公告
     */
    @DeleteMapping("/announcements/{id}")
    public Result<?> deleteAnnouncement(@PathVariable Long id) {
        announcementService.delete(id);
        return Result.success();
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

    // ==================== 系统配置 ====================

    /**
     * 获取所有配置
     */
    @GetMapping("/configs")
    public Result<List<SysConfig>> listConfigs() {
        return Result.success(sysConfigService.listConfigs());
    }

    /**
     * 更新配置
     */
    @PutMapping("/configs")
    public Result<?> updateConfig(@RequestBody Map<String, String> body) {
        String key = body.get("key");
        String value = body.get("value");
        sysConfigService.updateConfig(key, value);
        return Result.success();
    }

    // ==================== 统计数据 ====================

    /**
     * 仪表板统计
     */
    @GetMapping("/stats/dashboard")
    public Result<Map<String, Object>> getDashboardStats() {
        return Result.success(statsService.getDashboardStats());
    }

    /**
     * 订单统计
     */
    @GetMapping("/stats/orders")
    public Result<List<Map<String, Object>>> getOrderStats(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return Result.success(statsService.getOrderStats(startDate, endDate));
    }

    /**
     * 菜品销量排名
     */
    @GetMapping("/stats/dishes")
    public Result<List<Map<String, Object>>> getDishRanking(
            @RequestParam(required = false) Long stallId,
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(statsService.getDishRanking(stallId, limit));
    }

    /**
     * 收入统计
     */
    @GetMapping("/stats/revenue")
    public Result<List<Map<String, Object>>> getRevenueStats(
            @RequestParam(required = false) Long stallId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return Result.success(statsService.getRevenueStats(stallId, startDate, endDate));
    }
}
