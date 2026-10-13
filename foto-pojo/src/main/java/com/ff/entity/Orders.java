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
public class Orders {
    // 主键 | 必须 | 自增
    private Long id;

    // 订单号 | 必须 | 无默认
    private String orderNo;

    // 用户ID | 必须 | 无默认
    private Long userId;

    // 商品总金额(分) | 必须 | 默认0
    private Integer totalAmount;

    // 运费(分) | 必须 | 默认0
    private Integer freightAmount;

    // 优惠金额(分) | 必须 | 默认0
    private Integer discountAmount;

    // 实付金额(分) | 必须 | 默认0
    private Integer payAmount;

    // 总件数 | 必须 | 默认0
    private Integer totalQuantity;

    // 订单状态 | 必须 | 默认0
    private Integer status;

    // 用户备注 | 非必须 | 默认NULL
    private String remark;

    // 收件人 | 必须 | 无默认
    private String receiver;

    // 电话 | 必须 | 无默认
    private String phone;

    // 详细地址 | 必须 | 无默认
    private String addressDetail;

    // 买家经度 | 非必须 | 默认NULL
    private Double buyerLongitude;

    // 买家纬度 | 非必须 | 默认NULL
    private Double buyerLatitude;

    // 店铺ID | 必须 | 无默认
    private Long shopId;

    // 店铺名称 | 非必须 | 默认NULL
    private String shopName;

    // 店铺经度 | 非必须 | 默认NULL
    private Double shopLongitude;

    // 店铺纬度 | 非必须 | 默认NULL
    private Double shopLatitude;

    // 支付方式 1微信 2支付宝 | 非必须 | 默认NULL
    private Integer payType;

    // 支付时间 | 非必须 | 默认NULL
    private LocalDateTime payTime;

    // 发货时间 | 非必须 | 默认NULL
    private LocalDateTime deliveryTime;

    // 签收时间 | 非必须 | 默认NULL
    private LocalDateTime receiveTime;

    // 完成时间 | 非必须 | 默认NULL
    private LocalDateTime finishTime;

    // 创建时间 | 必须 | 默认CURRENT_TIMESTAMP
    private LocalDateTime createTime;

    // 更新时间 | 必须 | 默认CURRENT_TIMESTAMP ON UPDATE
    private LocalDateTime updateTime;

    // 逻辑删除 | 必须 | 默认0
    private Integer deleted;


    // ====================== 【骑手相关字段 - 新增】 ======================
    // 骑手ID | 非必须 | 默认NULL
    private Long riderId;

    // 骑手姓名快照 | 非必须 | 默认NULL
    private String riderName;

    // 骑手电话快照 | 非必须 | 默认NULL
    private String riderPhone;

    // 骑手接单时间 | 非必须 | 默认NULL
    private LocalDateTime riderTakeTime;


}