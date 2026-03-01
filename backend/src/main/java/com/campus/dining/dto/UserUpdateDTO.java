package com.campus.dining.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private String oldPassword;
    private String newPassword;
}
