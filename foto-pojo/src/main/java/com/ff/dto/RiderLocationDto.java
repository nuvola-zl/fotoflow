package com.ff.dto;

import lombok.Data;

/**
 * 骑手位置上报参数
 */
@Data
public class RiderLocationDto {

    /** 订单号 */
    private String orderNo;

    /** 当前经度 */
    private Double longitude;

    /** 当前纬度 */
    private Double latitude;
}