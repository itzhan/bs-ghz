package com.campus.dining.service;

import com.campus.dining.common.PageResult;
import com.campus.dining.entity.Payment;

import java.math.BigDecimal;

public interface PaymentService {

    /**
     * 创建支付记录
     */
    Payment createPayment(Long orderId, BigDecimal amount, Integer method);

    /**
     * 根据订单ID获取支付记录
     */
    Payment getPaymentByOrderId(Long orderId);

    /**
     * 分页查询支付记录
     */
    PageResult<Payment> listPayments(int page, int size);
}
