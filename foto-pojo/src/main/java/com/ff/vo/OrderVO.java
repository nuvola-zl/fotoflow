package com.ff.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO {
    private Long orderId;
    private String orderNo;
    private Integer totalAmount;    // 商品总金额
    private Integer payAmount;      // 实付金额
    private Integer status;
    private String statusDesc;
    private String receiver;
    private String phone;
    private String addressDetail;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private List<OrderItemVO> items;
}