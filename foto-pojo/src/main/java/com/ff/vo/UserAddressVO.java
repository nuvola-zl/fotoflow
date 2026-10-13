package com.ff.vo;

import lombok.Data;

@Data
public class UserAddressVO {
    private Long id;

    private String addressDetail;

    private String phone;

    private String receiver;

    private Integer isDefault;
}
