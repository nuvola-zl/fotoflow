package com.ff.dto;

import lombok.Data;

@Data
public class OrderPayDto {
    private String orderNo;     // 订单号
    private Integer payType;    // 1微信支付 2支付宝
}