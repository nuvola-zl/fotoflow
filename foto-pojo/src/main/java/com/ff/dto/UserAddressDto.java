package com.ff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class UserAddressDto {

        // 【主键自增】
        private Long id;

        // 【选填】所属用户ID
        private Long userId;

        // 【必填】详细收货地址
        private String addressDetail;

        // 【必填】收件人电话
        private String phone;

        // 【必填】收件人姓名
        private String receiver;

        // 【必填】经度 地图定位用
        private Double longitude;

        // 【必填】纬度 地图定位用
        private Double latitude;

        // 【数据库默认0】0普通地址 1默认地址
        private Integer isDefault;


}
