package com.campus.dining.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_info")
public class OrderInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long userId;

    private Long stallId;

    private BigDecimal totalAmount;

    private BigDecimal actualAmount;

    /** 0-待付款 1-已付款 2-制作中 3-待取餐 4-已完成 5-已取消 6-已退款 */
    private Integer status;

    private String remark;

    /** 1-余额 2-微信 3-支付宝 */
    private Integer paymentMethod;

    private LocalDateTime paidAt;

    private LocalDateTime completedAt;

    private LocalDateTime cancelledAt;

    private String cancelReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
