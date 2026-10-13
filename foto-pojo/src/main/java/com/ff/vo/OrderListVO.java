package com.ff.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单列表页VO
 * 展示订单基本信息 + 商品缩略列表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderListVO {
    private Long orderId;
    private String orderNo;
    private Integer totalAmount;
    private Integer payAmount;
    private Integer status;
    private String statusDesc;
    private String receiver;
    private String phone;
    private String addressDetail;
    private LocalDateTime createTime;
    private Integer itemCount;          // 商品种类数
    private List<OrderItemVO> items;   // 取前1-2条展示
}