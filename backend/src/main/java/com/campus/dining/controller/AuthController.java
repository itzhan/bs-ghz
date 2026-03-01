package com.campus.dining.controller;

import com.campus.dining.common.Result;
import com.campus.dining.dto.LoginDTO;
import com.campus.dining.dto.RegisterDTO;
import com.campus.dining.security.LoginUser;
import com.campus.dining.service.AuthService;
import com.campus.dining.vo.LoginVO;
import com.campus.dining.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.login(dto));
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterDTO dto) {
        return Result.success(authService.register(dto));
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    public Result<UserVO> getCurrentUser() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();
        return Result.success(authService.getCurrentUser(userId));
    }
}
