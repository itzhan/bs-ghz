package com.campus.dining.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class OrderDTO {
    @NotNull(message = "档口ID不能为空")
    private Long stallId;

    private String remark;

    /** 1-余额 2-微信 3-支付宝 */
    @NotNull(message = "支付方式不能为空")
    private Integer paymentMethod;

    @NotNull(message = "订单明细不能为空")
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        @NotNull(message = "菜品ID不能为空")
        private Long dishId;

        @NotNull(message = "数量不能为空")
        private Integer quantity;
    }
}
