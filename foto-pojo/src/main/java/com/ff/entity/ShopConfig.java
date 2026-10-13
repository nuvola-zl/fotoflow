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
public class ShopConfig {
    // 主键 | 必须 | 自增
    private Long id;

    // 店主用户ID | 必须 | 无默认
    private Long userId;

    // 店铺名称 | 必须 | 无默认
    private String shopName;

    // 店铺文字地址 | 必须 | 无默认
    private String shopAddress;

    // 店铺经度 | 必须 | 无默认
    private Double longitude;

    // 店铺纬度 | 必须 | 无默认
    private Double latitude;

    // 状态 0休息 1营业 | 必须 | 默认1
    private Integer status;

    // 创建时间 | 必须 | 默认CURRENT_TIMESTAMP
    private LocalDateTime createTime;

    // 更新时间 | 必须 | 默认CURRENT_TIMESTAMP ON UPDATE
    private LocalDateTime updateTime;
}