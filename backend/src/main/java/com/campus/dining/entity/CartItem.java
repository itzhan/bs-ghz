package com.campus.dining.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("cart_item")
public class CartItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long dishId;

    private Long stallId;

    private Integer quantity;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
