package com.campus.dining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.dining.common.PageResult;
import com.campus.dining.dto.UserUpdateDTO;
import com.campus.dining.entity.User;
import com.campus.dining.exception.BusinessException;
import com.campus.dining.mapper.UserMapper;
import com.campus.dining.service.UserService;
import com.campus.dining.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserVO getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return UserVO.fromEntity(user);
    }

    @Override
    public UserVO updateUser(Long id, UserUpdateDTO dto) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (StringUtils.hasText(dto.getNickname())) {
            user.setNickname(dto.getNickname());
        }
        if (StringUtils.hasText(dto.getPhone())) {
            user.setPhone(dto.getPhone());
        }
        if (StringUtils.hasText(dto.getEmail())) {
            user.setEmail(dto.getEmail());
        }
        if (StringUtils.hasText(dto.getAvatar())) {
            user.setAvatar(dto.getAvatar());
        }

        // 修改密码
        if (StringUtils.hasText(dto.getNewPassword())) {
            if (!StringUtils.hasText(dto.getOldPassword())) {
                throw new BusinessException("请输入旧密码");
            }
            if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
                throw new BusinessException("旧密码错误");
            }
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        userMapper.updateById(user);
        return UserVO.fromEntity(user);
    }

    @Override
    public PageResult<UserVO> listUsers(int page, int size, String keyword, Integer role, Integer status) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword));
        }
        if (role != null) {
            wrapper.eq(User::getRole, role);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreatedAt);

        Page<User> pageResult = userMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(
                pageResult.getRecords().stream().map(UserVO::fromEntity).collect(Collectors.toList()),
                pageResult.getTotal(),
                pageResult.getCurrent(),
                pageResult.getSize()
        );
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        userMapper.deleteById(id);
    }

    @Override
    public void recharge(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("充值金额必须大于0");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setBalance(user.getBalance().add(amount));
        userMapper.updateById(user);
    }
}
