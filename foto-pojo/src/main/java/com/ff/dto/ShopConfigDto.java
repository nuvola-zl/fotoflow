package com.ff.dto;

import lombok.Data;

@Data
public class ShopConfigDto {
    private String shopName;        // 店铺名称
    private String shopAddress;     // 店铺文字地址
    private Double longitude;       // 经度
    private Double latitude;        // 纬度
    private Integer status;         // 0休息 1营业，不传默认1
}