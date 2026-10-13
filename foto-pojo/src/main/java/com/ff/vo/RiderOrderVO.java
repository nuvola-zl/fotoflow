package com.ff.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiderOrderVO {
    private String orderNo;
    private Integer status;
    private String receiver;
    private String phone;
    private String addressDetail;
    private Double buyerLongitude;
    private Double buyerLatitude;
    private Double shopLongitude;
    private Double shopLatitude;
    private Integer payAmount;
    private LocalDateTime deliveryTime;
    private String shopName;
    private String shopAddress;
}