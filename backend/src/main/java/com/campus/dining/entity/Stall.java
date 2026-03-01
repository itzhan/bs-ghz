package com.campus.dining.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("stall")
public class Stall {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属商户ID */
    private Long merchantId;

    private String name;

    private String description;

    private String logo;

    /** 位置/楼层 */
    private String location;

    private String phone;

    /** 营业时间 */
    private String businessHours;

    /** 0-待审核 1-营业中 2-休息中 3-已关闭 */
    private Integer status;

    /** 平均评分 */
    private BigDecimal avgRating;

    /** 月销量 */
    private Integer monthlySales;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
