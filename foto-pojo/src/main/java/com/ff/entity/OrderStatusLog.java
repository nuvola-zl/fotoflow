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
public class OrderStatusLog {
    // 主键 | 必须 | 自增
    private Long id;

    // 订单ID | 必须 | 无默认
    private Long orderId;

    // 变更前状态 | 非必须 | 默认NULL
    private Integer beforeStatus;

    // 变更后状态 | 必须 | 无默认
    private Integer afterStatus;

    // 操作人 | 非必须 | 默认NULL
    private String operator;

    // 备注 | 非必须 | 默认NULL
    private String remark;

    // 创建时间 | 必须 | 默认CURRENT_TIMESTAMP
    private LocalDateTime createTime;
}