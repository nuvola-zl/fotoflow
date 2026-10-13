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
public class DeliveryTracking {
    // 主键 | 必须 | 自增
    private Long id;

    // 订单ID | 必须 | 无默认
    private Long orderId;

    // 骑手ID | 非必须 | 默认NULL
    private Long riderId;

    // 当前经度 | 必须 | 无默认
    private Double currentLongitude;

    // 当前纬度 | 必须 | 无默认
    private Double currentLatitude;

    // 位置时间 | 必须 | 无默认
    private LocalDateTime locationTime;

    // 速度 | 非必须 | 默认NULL
    private Double speed;

    // 剩余距离(米) | 非必须 | 默认NULL
    private Integer distanceLeft;

    // 创建时间 | 必须 | 默认CURRENT_TIMESTAMP
    private LocalDateTime createTime;
}