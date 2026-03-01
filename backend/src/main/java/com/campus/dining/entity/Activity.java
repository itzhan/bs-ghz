package com.campus.dining.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("activity")
public class Activity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long stallId;

    private String title;

    private String description;

    private String image;

    /** 1-满减 2-折扣 3-赠品 */
    private Integer discountType;

    private BigDecimal discountValue;

    private BigDecimal minAmount;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    /** 0-未开始 1-进行中 2-已结束 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
