package com.campus.dining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.dining.common.PageResult;
import com.campus.dining.entity.Payment;
import com.campus.dining.mapper.PaymentMapper;
import com.campus.dining.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;

    @Override
    public Payment createPayment(Long orderId, BigDecimal amount, Integer method) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setPaymentNo("PAY" + System.currentTimeMillis());
        payment.setAmount(amount);
        payment.setMethod(method);
        payment.setStatus(0); // 待支付
        paymentMapper.insert(payment);
        return payment;
    }

    @Override
    public Payment getPaymentByOrderId(Long orderId) {
        return paymentMapper.selectOne(
                new LambdaQueryWrapper<Payment>().eq(Payment::getOrderId, orderId)
        );
    }

    @Override
    public PageResult<Payment> listPayments(int page, int size) {
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Payment::getCreatedAt);

        Page<Payment> pageResult = paymentMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }
}
