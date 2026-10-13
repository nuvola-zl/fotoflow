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
public class OrderItem {
    // 主键 | 必须 | 自增
    private Long id;

    // 订单ID | 必须 | 无默认
    private Long orderId;

    // 商品ID | 必须 | 无默认
    private Long goodsId;

    // 商品名称 | 必须 | 无默认
    private String goodsName;

    // 商品图片 | 非必须 | 默认NULL
    private String goodsImg;

    // 商品规格 | 非必须 | 默认NULL
    private String goodsSpec;

    // 单位 | 非必须 | 默认NULL
    private String unit;

    // 单价(分) | 必须 | 无默认
    private Integer price;

    // 数量 | 必须 | 无默认
    private Integer quantity;

    // 小计金额(分) | 必须 | 无默认
    private Integer subtotal;

    // 配送状态 | 必须 | 默认0
    private Integer deliveryStatus;

    // 创建时间 | 必须 | 默认CURRENT_TIMESTAMP
    private LocalDateTime createTime;
}
