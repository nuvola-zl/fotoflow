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
public class RiderLocationVO {
    private Double longitude;
    private Double latitude;
    private LocalDateTime locationTime;
    private Integer estimatedMinutes;  // 预计送达分钟数
}