package com.liwq.bookmanager.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息DTO（返回给前端）
 */
@Data
public class UserDTO {

    private Long id;

    private String username;

    private String realName;

    private String phone;

    private String email;

    private String role;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * JWT令牌（仅登录时返回）
     */
    private String token;
}
