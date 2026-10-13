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
public class UserAddress {
    // 主键 | 必须 | 自增
    private Long id;

    // 用户ID | 必须 | 无默认
    private Long userId;

    // 详细地址 | 必须 | 无默认
    private String addressDetail;

    // 电话 | 必须 | 无默认
    private String phone;

    // 收件人 | 必须 | 无默认
    private String receiver;

    // 经度 | 必须 | 无默认
    private Double longitude;

    // 纬度 | 必须 | 无默认
    private Double latitude;

    // 标签 家/公司 | 非必须 | 默认NULL
    private String tag;

    // 是否默认 0否 1是 | 必须 | 默认0
    private Integer isDefault;

    // 创建时间 | 必须 | 默认CURRENT_TIMESTAMP
    private LocalDateTime createTime;

    // 更新时间 | 必须 | 默认CURRENT_TIMESTAMP ON UPDATE
    private LocalDateTime updateTime;
}
