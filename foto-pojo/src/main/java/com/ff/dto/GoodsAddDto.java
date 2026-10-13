package com.ff.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 商品新增参数
 * 商家创建商品时传入
 */
@Data
public class GoodsAddDto {
    /** 商品名称 */
    @NotBlank(message = "商品名称不能为空")
    private String goodsName;
    /** 商品主图URL */
    @NotBlank(message = "商品图片不能为空")
    private String goodsImg;
    /** 商品描述（富文本或纯文本） */
    private String content;
    /** 单价（单位：分） */
    @NotNull(message = "价格不能为空")
    @Min(value = 1, message = "价格必须大于0")
    private Integer price;
    /** 单位：斤、个、瓶、箱等 */
    @NotBlank(message = "单位不能为空")
    private String unit;
    /** 初始库存 */
    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;
}