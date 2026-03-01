package com.campus.dining.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    private String phone;

    private String email;

    /** 0-普通用户 1-商户 */
    @NotNull(message = "角色不能为空")
    private Integer role;
}
