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
public class OrderItem {
    // 【主键自增】
    private Long id;

    // 【必填】关联订单号
    private String orderNo;

    // 【必填】商品ID
    private Long goodsId;

    // 【必填】商品名称 下单快照
    private String goodsName;

    // 【必填】商品图片 下单快照
    private String goodsImg;

    // 【必填】下单时单价
    private Integer price;

    // 【必填】购买数量
    private Integer count;

    // 【数据库默认当前时间】
    private LocalDateTime createTime;
}