package com.campus.dining.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("announcement")
public class Announcement {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    /** 0-系统公告 1-商户公告 */
    private Integer type;

    private Long publisherId;

    private Long stallId;

    /** 0-草稿 1-已发布 2-已下架 */
    private Integer status;

    private LocalDateTime publishedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
