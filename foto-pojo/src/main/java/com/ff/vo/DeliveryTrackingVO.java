package com.ff.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 配送轨迹VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryTrackingVO {
    private Double currentLongitude;
    private Double currentLatitude;
    private LocalDateTime locationTime;
    private Integer distanceLeft;
}