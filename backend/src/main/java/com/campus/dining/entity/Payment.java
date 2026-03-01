package com.campus.dining.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment")
public class Payment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private String paymentNo;

    private BigDecimal amount;

    /** 1-余额 2-微信 3-支付宝 */
    private Integer method;

    /** 0-待支付 1-支付成功 2-支付失败 3-已退款 */
    private Integer status;

    private LocalDateTime paidAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
