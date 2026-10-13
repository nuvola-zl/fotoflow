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
public class ShopConfigVO {
    private Long id;
    private Long userId;
    private String shopName;
    private String shopAddress;
    private Double longitude;
    private Double latitude;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}