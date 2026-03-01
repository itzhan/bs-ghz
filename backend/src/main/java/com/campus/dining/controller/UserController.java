package com.campus.dining.controller;

import com.campus.dining.common.Result;
import com.campus.dining.dto.UserUpdateDTO;
import com.campus.dining.security.LoginUser;
import com.campus.dining.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public Result<?> getProfile() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();
        return Result.success(userService.getUserById(userId));
    }

    /**
     * 更新当前用户信息
     */
    @PutMapping("/profile")
    public Result<?> updateProfile(@Valid @RequestBody UserUpdateDTO dto) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();
        return Result.success(userService.updateUser(userId, dto));
    }

    /**
     * 余额充值
     */
    @PostMapping("/recharge")
    public Result<?> recharge(@RequestBody Map<String, BigDecimal> body) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();
        BigDecimal amount = body.get("amount");
        userService.recharge(userId, amount);
        return Result.success();
    }
}
