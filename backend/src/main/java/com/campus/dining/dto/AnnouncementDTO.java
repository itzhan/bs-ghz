package com.campus.dining.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnnouncementDTO {
    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    /** 0-系统公告 1-商户公告 */
    private Integer type;

    private Long stallId;
}
