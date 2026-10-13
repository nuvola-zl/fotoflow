package com.ff.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiderLoginVO {
    private Long id;
    private String phone;
    private String realName;
    private String avatar;
    private Integer status;
    private Double rating;   // 评分

    private String token;  // 新增：登录后返回给前端
}