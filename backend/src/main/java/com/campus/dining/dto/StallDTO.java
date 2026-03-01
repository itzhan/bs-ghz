package com.campus.dining.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StallDTO {
    @NotBlank(message = "档口名称不能为空")
    private String name;

    private String description;
    private String logo;
    private String location;
    private String phone;
    private String businessHours;
}
