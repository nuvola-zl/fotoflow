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
public class AddressVO {
    private Long id;
    private String addressDetail;
    private String phone;
    private String receiver;
    private Double longitude;
    private Double latitude;
    private String tag;
    private Integer isDefault;
    private LocalDateTime createTime;
}