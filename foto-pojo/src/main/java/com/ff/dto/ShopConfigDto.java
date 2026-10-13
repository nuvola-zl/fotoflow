package com.ff.dto;
import lombok.Data;
@Data

public class ShopConfigDto {
    private Long id;
    private Long userId;
    private String shopName;
    private String shopAddress;
    private Double longitude;
    private Double latitude;
}
