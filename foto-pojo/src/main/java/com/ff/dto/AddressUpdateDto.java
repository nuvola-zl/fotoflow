package com.ff.dto;

import lombok.Data;

@Data
public class AddressUpdateDto {
    private String addressDetail;
    private String phone;
    private String receiver;
    private Double longitude;
    private Double latitude;
    private String tag;
}