package com.campus.dining.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.dining.dto.LoginDTO;
import com.campus.dining.dto.RegisterDTO;
import com.campus.dining.entity.User;
import com.campus.dining.exception.BusinessException;
import com.campus.dining.mapper.UserMapper;
import com.campus.dining.security.JwtTokenProvider;
import com.campus.dining.service.AuthService;
import com.campus.dining.vo.LoginVO;
import com.campus.dining.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, dto.getUsername())
        );
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginVO(token, UserVO.fromEntity(user));
    }

    @Override
    public UserVO register(RegisterDTO dto) {
        // 检查用户名唯一
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, dto.getUsername())
        );
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setStatus(1);
        user.setBalance(BigDecimal.ZERO);
        userMapper.insert(user);
        return UserVO.fromEntity(user);
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return UserVO.fromEntity(user);
    }
}
