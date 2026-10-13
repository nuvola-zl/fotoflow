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
public class Cart {
    // 主键 | 必须 | 自增
    private Long id;

    // 用户ID | 必须 | 无默认
    private Long userId;

    // 商品ID | 必须 | 无默认
    private Long goodsId;

    // 商品规格 | 非必须 | 默认NULL
    private String goodsSpec;

    // 单位 | 非必须 | 默认NULL
    private String unit;

    // 数量 | 必须 | 默认1
    private Integer quantity;

    // 是否选中 1是 0否 | 必须 | 默认1
    private Integer isChecked;

    // 创建时间 | 必须 | 默认CURRENT_TIMESTAMP
    private LocalDateTime createTime;

    // 更新时间 | 必须 | 默认CURRENT_TIMESTAMP ON UPDATE
    private LocalDateTime updateTime;
}