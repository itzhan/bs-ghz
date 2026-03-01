package com.campus.dining.service;

import com.campus.dining.common.PageResult;
import com.campus.dining.dto.OrderDTO;
import com.campus.dining.entity.OrderInfo;
import com.campus.dining.entity.OrderItem;

import java.util.List;
import java.util.Map;

public interface OrderService {

    /**
     * 创建订单
     */
    OrderInfo createOrder(Long userId, OrderDTO dto);

    /**
     * 根据ID获取订单详情（含订单项）
     */
    Map<String, Object> getOrderById(Long id);

    /**
     * 分页查询用户订单
     */
    PageResult<OrderInfo> listUserOrders(Long userId, int page, int size, Integer status);

    /**
     * 分页查询档口订单
     */
    PageResult<OrderInfo> listStallOrders(Long stallId, int page, int size, Integer status);

    /**
     * 分页查询所有订单（管理员）
     */
    PageResult<OrderInfo> listAllOrders(int page, int size, Integer status, String keyword);

    /**
     * 取消订单
     */
    void cancelOrder(Long userId, Long orderId, String reason);

    /**
     * 更新订单状态
     */
    void updateOrderStatus(Long orderId, Integer status);

    /**
     * 获取订单项列表
     */
    List<OrderItem> getOrderItems(Long orderId);
}
