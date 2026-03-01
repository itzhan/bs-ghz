package com.campus.dining.controller;

import com.campus.dining.common.PageResult;
import com.campus.dining.common.Result;
import com.campus.dining.dto.OrderDTO;
import com.campus.dining.entity.OrderInfo;
import com.campus.dining.entity.OrderItem;
import com.campus.dining.security.LoginUser;
import com.campus.dining.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping
    public Result<OrderInfo> createOrder(@Valid @RequestBody OrderDTO dto) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();
        return Result.success(orderService.createOrder(userId, dto));
    }

    /**
     * 分页查询当前用户订单
     */
    @GetMapping
    public Result<PageResult<OrderInfo>> listMyOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();
        return Result.success(orderService.listUserOrders(userId, page, size, status));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getOrder(@PathVariable Long id) {
        return Result.success(orderService.getOrderById(id));
    }

    /**
     * 获取订单项列表
     */
    @GetMapping("/{id}/items")
    public Result<List<OrderItem>> getOrderItems(@PathVariable Long id) {
        return Result.success(orderService.getOrderItems(id));
    }

    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    public Result<?> cancelOrder(@PathVariable Long id, @RequestBody Map<String, String> body) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();
        String reason = body.get("reason");
        orderService.cancelOrder(userId, id, reason);
        return Result.success();
    }
}
