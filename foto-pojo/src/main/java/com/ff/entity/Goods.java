package com.ff.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Goods {

    // 【主键自增】无需传参
    private Long id;

    // 【关联视频】视频挂载商品用
    private Long videoId;

    // 【必填】商品名称（苹果、香蕉、矿泉水）
    private String goodsName;

    // 【必填】商品主图
    private String goodsImg;

    // 【非必填】商品描述
    private String content;

    // ==============================================
    // 【必填】单价（单位：分）
    // 例：5.99元/斤 → 存 599
    // 例：10元/个 → 存 1000
    // ==============================================
    private Integer price;

    // ==============================================
    // 【必填！！！】单位（关键！解决你的需求）
    // 可选值：斤、个、件、瓶、kg、袋、盒、箱
    // ==============================================
    private String unit;

    // 【默认0】库存
    private Integer stock;

    // 【默认0】销量
    private Integer sales;

    // 【默认1】0下架 1上架
    private Integer status;

    //关联的商品配置id
    private Long goodsConfigId;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}