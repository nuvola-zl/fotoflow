package com.ff.dto;

import lombok.Data;

/**
 * 加入购物车参数
 */
@Data
public class CartAddDto {
    /** 商品ID */
    private Long goodsId;
    /** 购买数量，默认1 */
    private Integer quantity;
    /** 商品规格，如"红色-大码"，非必填 */
    private String goodsSpec;
}