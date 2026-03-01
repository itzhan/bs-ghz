package com.campus.dining.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DishDTO {
    @NotNull(message = "档口ID不能为空")
    private Long stallId;

    private Long categoryId;

    @NotBlank(message = "菜品名称不能为空")
    private String name;

    private String description;
    private String image;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer stock;
    private Integer dailyLimit;
    private Integer warningThreshold;
    private Integer isRecommended;
}
