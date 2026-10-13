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
public class Rider {

    // 主键 | 必须 | 自增
    private Long id;

    // 手机号 | 必须 | 无默认 | 唯一键
    private String phone;

    // 密码 | 非必须 | 默认NULL
    private String password;

    // 真实姓名 | 非必须 | 默认NULL
    private String realName;

    // 身份证号 | 非必须 | 默认NULL
    private String idCard;

    // 头像 | 非必须 | 默认NULL
    private String avatar;

    // 状态 0休息 1上班 2配送中 | 非必须 | 默认0
    private Integer status;

    // 评分 | 非必须 | 默认5.0
    private Double rating;

    // 累计接单数 | 非必须 | 默认0
    private Integer totalOrders;

    // 今日接单数 | 非必须 | 默认0
    private Integer todayOrders;

    // 创建时间 | 必须 | 默认CURRENT_TIMESTAMP
    private LocalDateTime createTime;

    // 更新时间 | 必须 | 默认CURRENT_TIMESTAMP ON UPDATE
    private LocalDateTime updateTime;
}