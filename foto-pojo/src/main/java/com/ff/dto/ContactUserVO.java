package com.ff.dto;

import lombok.Data;
@Data
public class ContactUserVO {
    private Long id;          // 用户ID
    private String username;  // 昵称
    private String avatarUrl;    // 头像
    private Integer unreadCount; // 未读消息数（小红点）
}
