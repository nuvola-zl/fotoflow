package com.ff.vo;

import lombok.Data;
@Data
public class GoodsVO {
    private Long id;
    private String goodsName;
    private String goodsImage;
    private String content;
    private Integer price;
    private Integer stock;
    private Integer sales;
}
