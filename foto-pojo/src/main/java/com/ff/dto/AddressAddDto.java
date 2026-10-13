package com.ff.dto;

import lombok.Data;

@Data
public class AddressAddDto {
    private String addressDetail;   // 详细地址
    private String phone;           // 电话
    private String receiver;        // 收件人
    private Double longitude;       // 经度
    private Double latitude;        // 纬度
    private String tag;             // 标签：家/公司/学校
    private Integer isDefault;      // 0普通 1默认，不传则自动处理
}