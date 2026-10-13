package com.ff.dto;

import lombok.Data;

@Data
public class UserUpdateDto {

    //普通修改
    private Long id;
    private String username;
    private String avatarUrl;

    //密码修改
    private String oldPassword;
    private String password;
    private String confirmPassword;

    //实名认证修改
    private String realName;
    private String idCard;
    private Integer isRealName;

}
