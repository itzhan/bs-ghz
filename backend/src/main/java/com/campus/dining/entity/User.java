package com.campus.dining.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String phone;

    private String email;

    private String avatar;

    /** 0-普通用户 1-商户 2-管理员 */
    private Integer role;

    /** 0-禁用 1-正常 */
    private Integer status;

    /** 账户余额 */
    private BigDecimal balance;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private LocalDateTime deletedAt;
}
