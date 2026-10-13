package com.ff.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 骑手位置汇总表
 * 作用：定时聚合骑手最新位置，减少轨迹表查询压力
 * 一个订单 对应 一条最新位置记录
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class RiderLocationSummary {

    /**
     * 订单ID（主键，非自增，一个订单一条记录）
     * 对应数据库 order_id BIGINT PRIMARY KEY
     */
    private Long orderId;

    /**
     * 骑手ID
     * 对应数据库 rider_id BIGINT NOT NULL
     */
    private Long riderId;

    /**
     * 当前经度
     * 对应数据库 current_lng DOUBLE NOT NULL
     */
    private Double currentLng;

    /**
     * 当前纬度
     * 对应数据库 current_lat DOUBLE NOT NULL
     */
    private Double currentLat;

    /**
     * 位置上报时间
     * 对应数据库 location_time DATETIME NOT NULL
     */
    private LocalDateTime locationTime;

    /**
     * 预计送达时间（分钟）
     * 对应数据库 eta_minutes INT DEFAULT NULL
     */
    private Integer etaMinutes;

    /**
     * 数据更新时间（数据库自动维护）
     * 对应数据库 update_time DATETIME
     */
    private LocalDateTime updateTime;
}