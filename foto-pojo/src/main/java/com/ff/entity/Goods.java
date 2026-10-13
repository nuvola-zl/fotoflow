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
public class Goods {
    // 主键 | 必须 | 自增
    private Long id;

    // 商品名称 | 必须 | 无默认
    private String goodsName;

    // 商品主图 | 非必须 | 默认NULL
    private String goodsImg;

    // 商品描述 | 非必须 | 默认NULL
    private String content;

    // 单价（分） | 必须 | 默认0
    private Integer price;

    // 单位 | 必须 | 无默认
    private String unit;

    // 库存 | 必须 | 默认0
    private Integer stock;

    // 销量 | 必须 | 默认0
    private Integer sales;

    // 状态 0下架 1上架 | 必须 | 默认1
    private Integer status;

    // 店铺配置ID | 非必须 | 默认NULL
    private Long goodsConfigId;

    // 逻辑删除 0正常 1删除 | 必须 | 默认0
    private Integer deleted;

    // 创建时间 | 必须 | 默认CURRENT_TIMESTAMP
    private LocalDateTime createTime;

    // 更新时间 | 必须 | 默认CURRENT_TIMESTAMP ON UPDATE
    private LocalDateTime updateTime;
}