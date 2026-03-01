package com.campus.dining.service;

import com.campus.dining.common.PageResult;
import com.campus.dining.dto.UserUpdateDTO;
import com.campus.dining.vo.UserVO;

import java.math.BigDecimal;

public interface UserService {

    /**
     * 根据ID获取用户
     */
    UserVO getUserById(Long id);

    /**
     * 更新用户信息
     */
    UserVO updateUser(Long id, UserUpdateDTO dto);

    /**
     * 分页查询用户列表
     */
    PageResult<UserVO> listUsers(int page, int size, String keyword, Integer role, Integer status);

    /**
     * 更新用户状态（启用/禁用）
     */
    void updateUserStatus(Long id, Integer status);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 用户充值
     */
    void recharge(Long userId, BigDecimal amount);
}
