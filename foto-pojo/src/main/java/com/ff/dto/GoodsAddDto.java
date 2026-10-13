package com.ff.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class GoodsAddDto {
    private Long id;

    private Long videoId;

    @NotBlank(message = "商品名称不能为空")
    private String goodsName;

    // 【必填】商品主图
    @NotBlank(message = "商品主图不能为空")
    private String goodsImg;

    // 【非必填】商品描述
    private String content;

    // ==============================================
    // 【必填】单价（单位：分）
    // 例：5.99元/斤 → 存 599
    // 例：10元/个 → 存 1000
    // ==============================================
    @NotNull(message = "单价不能为空")
    private Integer price;

    // ==============================================
    // 【必填！！！】单位
    // 可选值：斤、个、件、瓶、kg、袋、盒、箱
    // ==============================================
    @NotBlank(message = "单位不能为空")
    private String unit;

    // 【必填】商品配置ID
    @NotNull(message = "商品配置ID不能为空")
    private Long goodsConfigId;

    // 【默认0】库存
    private Integer stock;
}
