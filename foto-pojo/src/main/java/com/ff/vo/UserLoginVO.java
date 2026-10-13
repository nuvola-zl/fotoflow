package com.ff.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginVO {
    private Long id;    //用户id
    private String username;   //用户名
    private String role;       //角色
    private String avatarUrl;  //头像url
    private Integer status;     //状态

    private String token;      //token

}
