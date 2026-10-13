package com.ff.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderItemDto {
    // 【必填】唯一订单号 后端生成
    private String orderNo;

    // 【必填】下单用户ID
    private Long userId;

    // 【必填】订单总金额 分
    private Integer totalAmount;


    // 【必填】收货详细地址
    private String addressDetail;
    // 【必填】收件电话
    private String phone;

    // 【必填】收件人
    private String receiver;

    // 【必填】买家收货地址经纬度
    private Double buyerLongitude;
    private Double buyerLatitude;
    // 【必填】店铺经纬度 下单时从shop_config复制过来存快照
    private Double shopLongitude;
    private Double shopLatitude;

    // 【必填】商品配置ID（店铺配置ID）
    private Long goodsConfigId;

    // 【必填】商品ID
    private Long goodsId;

    // 购买数量，默认1
    private Integer count;

}
