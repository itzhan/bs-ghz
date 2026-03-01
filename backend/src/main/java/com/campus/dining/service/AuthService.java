package com.campus.dining.service;

import com.campus.dining.dto.LoginDTO;
import com.campus.dining.dto.RegisterDTO;
import com.campus.dining.vo.LoginVO;
import com.campus.dining.vo.UserVO;

public interface AuthService {

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO dto);

    /**
     * 用户注册
     */
    UserVO register(RegisterDTO dto);

    /**
     * 获取当前登录用户信息
     */
    UserVO getCurrentUser(Long userId);
}
