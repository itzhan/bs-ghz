package com.campus.dining.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("review")
public class Review {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long userId;

    private Long stallId;

    private Long dishId;

    /** 评分 1-5 */
    private Integer rating;

    private String content;

    /** 图片，多张用逗号分隔 */
    private String images;

    /** 商户回复 */
    private String replyContent;

    private LocalDateTime replyAt;

    /** 0-正常 1-隐藏 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
