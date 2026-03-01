package com.campus.dining.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("complaint")
public class Complaint {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long stallId;

    private Long orderId;

    /** 1-食品质量 2-服务态度 3-环境卫生 4-其他 */
    private Integer type;

    private String content;

    private String images;

    /** 0-待处理 1-处理中 2-已解决 3-已驳回 */
    private Integer status;

    private String replyContent;

    private Long handlerId;

    private LocalDateTime handledAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
