package com.ff.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderBuyNowDto {
    @NotNull(message = "商品ID不能为空")
    private Long goodsId;       // 商品ID

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;   // 购买数量，不传默认1

    // 地址优先级：addressId > 前端手动传入 > 后端默认地址
    private Long addressId;

    // 如果不传 addressId，以下字段必填（新用户手动填地址）
    private String addressDetail;
    private String phone;
    private String receiver;
    private Double buyerLongitude;
    private Double buyerLatitude;

    private String remark;      // 订单备注，非必填
}