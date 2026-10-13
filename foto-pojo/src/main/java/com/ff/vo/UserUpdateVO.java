package com.ff.vo;

import lombok.Data;

@Data
public class UserUpdateVO {
    private Long id;
    private String username;
    private String password;
    private String avatarUrl;
}
