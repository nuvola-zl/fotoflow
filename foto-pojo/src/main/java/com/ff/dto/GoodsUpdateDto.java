package com.ff.dto;

import lombok.Data;

@Data
public class GoodsUpdateDto {
    private Long id;
    private String goodsName;
    private String goodsImg;
    private String content;
    private Integer price;
    private String unit;
    private Integer stock;
    private Integer status;
}
