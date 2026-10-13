package com.ff.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemVO {
    private Long goodsId;
    private String goodsName;
    private String goodsImg;
    private String unit;
    private Integer price;      // 分
    private Integer quantity;
    private Integer subtotal;   // 小计
}