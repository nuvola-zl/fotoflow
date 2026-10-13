package com.ff.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id; // 必须，自增
    private String username;  // 必须
    private String password;  // 必须
    private String phone;     // 可选
    private Integer isRealName; // 可选，默认0
    private String role;      // 可选，默认user普通用户
    private LocalDateTime createTime; // 自动
    private LocalDateTime updateTime; // 自动
    private Integer status; // 可选，默认0正常 1禁用

    private String banReason; //禁用原因
    private Integer fansCount; // 粉丝数量 默认为0

    private String avatarUrl; // 可选，默认空字符串
}