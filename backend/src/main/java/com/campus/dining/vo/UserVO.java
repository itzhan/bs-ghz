package com.campus.dining.vo;

import com.campus.dining.entity.User;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private Integer role;
    private Integer status;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public static UserVO fromEntity(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setBalance(user.getBalance());
        vo.setCreatedAt(user.getCreatedAt());
        return vo;
    }
}
