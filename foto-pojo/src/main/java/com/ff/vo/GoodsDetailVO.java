package com.ff.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商品详情 VO
 * 用于商品详情页展示，包含完整信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsDetailVO {
    private Long id;
    private String goodsName;
    private String goodsImg;
    private String content;
    private Integer price;
    private String unit;
    private Integer stock;
    private Integer sales;
    private Integer status;
    private Long goodsConfigId;
    private LocalDateTime createTime;
}