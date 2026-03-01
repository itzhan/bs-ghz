package com.campus.dining.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ActivityDTO {
    @NotNull(message = "档口ID不能为空")
    private Long stallId;

    @NotBlank(message = "活动标题不能为空")
    private String title;

    private String description;
    private String image;

    @NotNull(message = "折扣类型不能为空")
    private Integer discountType;

    private BigDecimal discountValue;
    private BigDecimal minAmount;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
}
