package com.campus.dining.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DishCategoryDTO {
    @NotNull(message = "档口ID不能为空")
    private Long stallId;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private Integer sortOrder;
}
