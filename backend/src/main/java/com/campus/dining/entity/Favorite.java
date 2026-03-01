package com.campus.dining.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("favorite")
public class Favorite {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long stallId;

    private Long dishId;

    /** 1-档口 2-菜品 */
    private Integer type;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
