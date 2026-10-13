package com.ff.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyOrdersVO {
    // 【主键自增】
    private Long id;

    // 【必填】唯一订单号 后端生成
    private String orderNo;

    private Integer totalAmount;

    private String phone;
    private String receiver;

    // 0待付款 1已付款待接单 2配送中 3已取消 4已完成 5用户已确认收货
    private Integer status;

    // 【必填】收货详细地址
    private String addressDetail;

    // 【必填】买家收货地址经纬度
    private Double buyerLongitude;
    private Double buyerLatitude;

    // 【必填】店铺经纬度 下单时从shop_config复制过来存快照
    private Double shopLongitude;
    private Double shopLatitude;

    // 【非必填】配送经纬度（商家/骑手位置）
    private Double deliveryLongitude;
    private Double deliveryLatitude;

    // 【数据库默认当前时间】下单时间
    private LocalDateTime createTime;

    // 【非必填】完成时间
    private LocalDateTime finishTime;
}
