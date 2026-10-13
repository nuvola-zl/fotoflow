package com.ff.vo;

import lombok.Data;

@Data
public class UserInfoVO {
    private Long id;     //用户id
    private String username; //用户名
    private String phone; //手机号
    private Integer isRealName;//是否实名认证
    private String role;//角色
    private String realName;//真实姓名
    private String idCard;//身份证号
    private String avatarUrl;//头像url
    private Integer status;//状态
}
