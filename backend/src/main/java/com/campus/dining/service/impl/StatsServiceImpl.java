package com.campus.dining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.dining.common.Constants;
import com.campus.dining.entity.*;
import com.campus.dining.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl {

    private final UserMapper userMapper;
    private final OrderInfoMapper orderInfoMapper;
    private final OrderItemMapper orderItemMapper;
    private final StallMapper stallMapper;
    private final PaymentMapper paymentMapper;

    /**
     * 获取仪表板统计数据
     */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // 总用户数
        Long totalUsers = userMapper.selectCount(null);
        stats.put("totalUsers", totalUsers);

        // 总订单数
        Long totalOrders = orderInfoMapper.selectCount(null);
        stats.put("totalOrders", totalOrders);

        // 总收入（已完成订单）
        List<OrderInfo> completedOrders = orderInfoMapper.selectList(
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getStatus, Constants.ORDER_COMPLETED)
        );
        BigDecimal totalRevenue = completedOrders.stream()
                .map(OrderInfo::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalRevenue", totalRevenue);

        // 总档口数
        Long totalStalls = stallMapper.selectCount(null);
        stats.put("totalStalls", totalStalls);

        return stats;
    }

    /**
     * 获取档口统计数据
     */
    public Map<String, Object> getStallStats(Long stallId) {
        Map<String, Object> stats = new HashMap<>();

        // 档口收入
        List<OrderInfo> orders = orderInfoMapper.selectList(
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getStallId, stallId)
                        .eq(OrderInfo::getStatus, Constants.ORDER_COMPLETED)
        );
        BigDecimal revenue = orders.stream()
                .map(OrderInfo::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("revenue", revenue);

        // 订单数
        Long orderCount = orderInfoMapper.selectCount(
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getStallId, stallId)
        );
        stats.put("orderCount", orderCount);

        // 平均评分
        Stall stall = stallMapper.selectById(stallId);
        stats.put("avgRating", stall != null ? stall.getAvgRating() : BigDecimal.ZERO);

        return stats;
    }

    /**
     * 获取订单统计（按日期范围，每日订单数和收入）
     */
    public List<Map<String, Object>> getOrderStats(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE);

        List<OrderInfo> orders = orderInfoMapper.selectList(
                new LambdaQueryWrapper<OrderInfo>()
                        .ge(OrderInfo::getCreatedAt, start.atStartOfDay())
                        .le(OrderInfo::getCreatedAt, end.atTime(LocalTime.MAX))
        );

        // 按日期分组统计
        Map<LocalDate, List<OrderInfo>> grouped = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getCreatedAt().toLocalDate()));

        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            Map<String, Object> dayStats = new HashMap<>();
            dayStats.put("date", current.toString());

            List<OrderInfo> dayOrders = grouped.getOrDefault(current, Collections.emptyList());
            dayStats.put("orderCount", dayOrders.size());

            BigDecimal dayRevenue = dayOrders.stream()
                    .filter(o -> o.getStatus() == Constants.ORDER_COMPLETED)
                    .map(OrderInfo::getActualAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dayStats.put("revenue", dayRevenue);

            result.add(dayStats);
            current = current.plusDays(1);
        }
        return result;
    }

    /**
     * 获取菜品销量排名
     */
    public List<Map<String, Object>> getDishRanking(Long stallId, int limit) {
        // 查询该档口所有已完成订单的订单项
        List<OrderInfo> orders = orderInfoMapper.selectList(
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getStallId, stallId)
                        .eq(OrderInfo::getStatus, Constants.ORDER_COMPLETED)
        );
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> orderIds = orders.stream().map(OrderInfo::getId).collect(Collectors.toList());
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .in(OrderItem::getOrderId, orderIds)
        );

        // 按菜品汇总销量
        Map<String, Integer> salesMap = new LinkedHashMap<>();
        for (OrderItem item : items) {
            salesMap.merge(item.getDishName(), item.getQuantity(), Integer::sum);
        }

        // 排序并取前N个
        return salesMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("dishName", entry.getKey());
                    map.put("sales", entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取收入统计（按日期范围，每日收入）
     */
    public List<Map<String, Object>> getRevenueStats(Long stallId, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE);

        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        if (stallId != null) {
            wrapper.eq(OrderInfo::getStallId, stallId);
        }
        wrapper.eq(OrderInfo::getStatus, Constants.ORDER_COMPLETED)
                .ge(OrderInfo::getCreatedAt, start.atStartOfDay())
                .le(OrderInfo::getCreatedAt, end.atTime(LocalTime.MAX));

        List<OrderInfo> orders = orderInfoMapper.selectList(wrapper);

        Map<LocalDate, List<OrderInfo>> grouped = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getCreatedAt().toLocalDate()));

        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            Map<String, Object> dayStats = new HashMap<>();
            dayStats.put("date", current.toString());

            List<OrderInfo> dayOrders = grouped.getOrDefault(current, Collections.emptyList());
            BigDecimal dayRevenue = dayOrders.stream()
                    .map(OrderInfo::getActualAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dayStats.put("revenue", dayRevenue);

            result.add(dayStats);
            current = current.plusDays(1);
        }
        return result;
    }
}
