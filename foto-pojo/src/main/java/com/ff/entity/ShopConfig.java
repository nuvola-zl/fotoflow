package com.ff.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopConfig {
    // 【主键自增】
    private Long id;

    // 【外键】用户id
    private Long userId;

       // 【必填】店铺名称
    private String shopName;

    // 【必填】店铺文字地址
    private String shopAddress;

    // 【必填】店铺经度 地图用
    private Double longitude;

    // 【必填】店铺纬度 地图用
    private Double latitude;

    // 【数据库默认当前时间/自动更新】
    private LocalDateTime updateTime;
}