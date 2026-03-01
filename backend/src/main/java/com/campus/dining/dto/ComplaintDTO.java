package com.campus.dining.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ComplaintDTO {
    @NotNull(message = "档口ID不能为空")
    private Long stallId;

    private Long orderId;

    @NotNull(message = "投诉类型不能为空")
    private Integer type;

    @NotBlank(message = "投诉内容不能为空")
    private String content;

    private String images;
}
