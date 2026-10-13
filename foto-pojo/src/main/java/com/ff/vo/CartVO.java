package com.ff.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 购物车条目VO
 * 展示商品实时信息 + 购物车数量
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartVO {
    private Long cartId;
    private Long goodsId;
    private String goodsName;
    private String goodsImg;
    private Integer price;
    private String unit;
    private Integer stock;       // 商品实时库存，用于提示用户是否充足
    private Integer quantity;   // 购物车里的数量
    private Integer isChecked;  // 1选中 0未选中
    private Integer subtotal;   // 小计 = price * quantity
    private Integer available;  // 1正常 0商品下架或删除（提示用户失效）
}