package com.campus.dining.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("dish")
public class Dish {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long stallId;

    private Long categoryId;

    private String name;

    private String description;

    private String image;

    private BigDecimal price;

    private BigDecimal originalPrice;

    /** 0-待审核 1-在售 2-下架 */
    private Integer status;

    /** 库存 */
    private Integer stock;

    /** 日限量 */
    private Integer dailyLimit;

    /** 库存预警阈值 */
    private Integer warningThreshold;

    /** 月销量 */
    private Integer monthlySales;

    /** 评分 */
    private BigDecimal rating;

    /** 是否推荐 */
    private Integer isRecommended;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
