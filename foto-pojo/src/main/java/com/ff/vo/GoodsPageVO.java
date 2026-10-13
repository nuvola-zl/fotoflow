package com.ff.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品列表页 VO
 * 精简字段，减少列表页数据传输量
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsPageVO {
    private Long id;
    private String goodsName;
    private String goodsImg;
    private Integer price;
    private String unit;
    private Integer stock;
    private Integer sales;
    private Integer status;
}