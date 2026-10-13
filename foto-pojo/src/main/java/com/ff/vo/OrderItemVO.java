package com.ff.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemVO {
    // 【必填】唯一订单号 后端生成
    private String orderNo;

    // 【必填】下单用户ID
    private Long userId;

    // 【必填】订单总金额 分
    private Integer totalAmount;

    // 【数据库默认0】0待付款 1已付款待配送 2配送中 3已完成
    private Integer status;

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

    // 【必填】店铺配置ID
    private Long goodsConfigId;

    // 商品ID
    private Long goodsId;

    private String goodsName;
    private String goodsImg;
    private Integer price;
    private String unit;
    private Integer count;

    // 【数据库默认当前时间】下单时间
    private LocalDateTime createTime;

    //支付时间
    private LocalDateTime payTime;




}
