package com.campus.dining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.dining.common.Constants;
import com.campus.dining.common.PageResult;
import com.campus.dining.dto.OrderDTO;
import com.campus.dining.entity.*;
import com.campus.dining.exception.BusinessException;
import com.campus.dining.mapper.*;
import com.campus.dining.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderInfoMapper orderInfoMapper;
    private final OrderItemMapper orderItemMapper;
    private final DishMapper dishMapper;
    private final UserMapper userMapper;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderInfo createOrder(Long userId, OrderDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("订单明细不能为空");
        }

        // 生成订单号: CD + 时间戳 + 随机4位
        String orderNo = "CD" + System.currentTimeMillis() + String.format("%04d", new Random().nextInt(10000));

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // 计算总价并校验库存
        for (OrderDTO.OrderItemDTO itemDTO : dto.getItems()) {
            Dish dish = dishMapper.selectById(itemDTO.getDishId());
            if (dish == null) {
                throw new BusinessException("菜品不存在，ID: " + itemDTO.getDishId());
            }
            if (dish.getStatus() != Constants.DISH_ON_SALE) {
                throw new BusinessException("菜品「" + dish.getName() + "」已下架");
            }
            if (dish.getStock() < itemDTO.getQuantity()) {
                throw new BusinessException("菜品「" + dish.getName() + "」库存不足");
            }

            BigDecimal subtotal = dish.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setDishId(dish.getId());
            orderItem.setDishName(dish.getName());
            orderItem.setDishImage(dish.getImage());
            orderItem.setPrice(dish.getPrice());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setSubtotal(subtotal);
            orderItems.add(orderItem);

            // 扣减库存
            dish.setStock(dish.getStock() - itemDTO.getQuantity());
            dish.setMonthlySales(dish.getMonthlySales() + itemDTO.getQuantity());
            dishMapper.updateById(dish);
        }

        // 创建订单
        OrderInfo order = new OrderInfo();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setStallId(dto.getStallId());
        order.setTotalAmount(totalAmount);
        order.setActualAmount(totalAmount);
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setRemark(dto.getRemark());

        // 余额支付直接扣款
        if (dto.getPaymentMethod() == Constants.PAY_BALANCE) {
            User user = userMapper.selectById(userId);
            if (user.getBalance().compareTo(totalAmount) < 0) {
                throw new BusinessException("余额不足");
            }
            user.setBalance(user.getBalance().subtract(totalAmount));
            userMapper.updateById(user);

            order.setStatus(Constants.ORDER_PAID);
            order.setPaidAt(LocalDateTime.now());
        } else {
            order.setStatus(Constants.ORDER_UNPAID);
        }

        orderInfoMapper.insert(order);

        // 保存订单项
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        // 余额支付创建支付记录
        if (dto.getPaymentMethod() == Constants.PAY_BALANCE) {
            Payment payment = new Payment();
            payment.setOrderId(order.getId());
            payment.setPaymentNo("PAY" + System.currentTimeMillis());
            payment.setAmount(totalAmount);
            payment.setMethod(Constants.PAY_BALANCE);
            payment.setStatus(Constants.PAY_STATUS_SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
            paymentMapper.insert(payment);
        }

        // 更新档口月销量
        // (简单处理：累加订单项数量)

        return order;
    }

    @Override
    public Map<String, Object> getOrderById(Long id) {
        OrderInfo order = orderInfoMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, id)
        );
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("items", items);
        return result;
    }

    @Override
    public PageResult<OrderInfo> listUserOrders(Long userId, int page, int size, Integer status) {
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getUserId, userId);
        if (status != null) {
            wrapper.eq(OrderInfo::getStatus, status);
        }
        wrapper.orderByDesc(OrderInfo::getCreatedAt);

        Page<OrderInfo> pageResult = orderInfoMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    public PageResult<OrderInfo> listStallOrders(Long stallId, int page, int size, Integer status) {
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getStallId, stallId);
        if (status != null) {
            wrapper.eq(OrderInfo::getStatus, status);
        }
        wrapper.orderByDesc(OrderInfo::getCreatedAt);

        Page<OrderInfo> pageResult = orderInfoMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    public PageResult<OrderInfo> listAllOrders(int page, int size, Integer status, String keyword) {
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(OrderInfo::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(OrderInfo::getOrderNo, keyword);
        }
        wrapper.orderByDesc(OrderInfo::getCreatedAt);

        Page<OrderInfo> pageResult = orderInfoMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, Long orderId, String reason) {
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getStatus() != Constants.ORDER_UNPAID && order.getStatus() != Constants.ORDER_PAID) {
            throw new BusinessException("当前订单状态不可取消");
        }

        // 已付款需退款
        if (order.getStatus() == Constants.ORDER_PAID) {
            User user = userMapper.selectById(userId);
            user.setBalance(user.getBalance().add(order.getActualAmount()));
            userMapper.updateById(user);

            // 更新支付记录状态为已退款
            Payment payment = paymentMapper.selectOne(
                    new LambdaQueryWrapper<Payment>().eq(Payment::getOrderId, orderId)
            );
            if (payment != null) {
                payment.setStatus(Constants.PAY_STATUS_REFUNDED);
                paymentMapper.updateById(payment);
            }

            order.setStatus(Constants.ORDER_REFUNDED);
        } else {
            order.setStatus(Constants.ORDER_CANCELLED);
        }

        order.setCancelReason(reason);
        order.setCancelledAt(LocalDateTime.now());
        orderInfoMapper.updateById(order);

        // 恢复库存
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId)
        );
        for (OrderItem item : items) {
            Dish dish = dishMapper.selectById(item.getDishId());
            if (dish != null) {
                dish.setStock(dish.getStock() + item.getQuantity());
                dishMapper.updateById(dish);
            }
        }
    }

    @Override
    public void updateOrderStatus(Long orderId, Integer status) {
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        order.setStatus(status);
        if (status == Constants.ORDER_COMPLETED) {
            order.setCompletedAt(LocalDateTime.now());
        }
        orderInfoMapper.updateById(order);
    }

    @Override
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId)
        );
    }
}
