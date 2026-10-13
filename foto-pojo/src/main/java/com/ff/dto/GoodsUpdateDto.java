package com.ff.dto;

import lombok.Data;

/**
 * 商品修改参数
 * 商家修改商品时传入，字段均为可选
 */
@Data
public class GoodsUpdateDto {
    /** 商品名称 */
    private String goodsName;
    /** 商品主图URL */
    private String goodsImg;
    /** 商品描述 */
    private String content;
    /** 单价（单位：分） */
    private Integer price;
    /** 单位 */
    private String unit;
    /** 库存 */
    private Integer stock;
}