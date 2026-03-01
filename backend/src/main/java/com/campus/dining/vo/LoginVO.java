package com.campus.dining.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginVO {
    private String token;
    private UserVO user;
}
