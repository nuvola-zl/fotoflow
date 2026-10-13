package com.ff.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情VO
 * 包含完整地址、所有商品明细、最新物流
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVO {
    private Long orderId;
    private String orderNo;
    private Integer totalAmount;
    private Integer freightAmount;
    private Integer discountAmount;
    private Integer payAmount;
    private Integer totalQuantity;
    private Integer status;
    private String statusDesc;
    private String remark;

    // 地址快照
    private String receiver;
    private String phone;
    private String addressDetail;
    private Double buyerLongitude;
    private Double buyerLatitude;

    // 店铺快照
    private Long shopId;
    private String shopName;
    private Double shopLongitude;
    private Double shopLatitude;

    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime receiveTime;

    private List<OrderItemVO> items;           // 全部商品明细
    private DeliveryTrackingVO latestTracking; // 最新物流位置（如果有）

    // ========== 新增：骑手信息 ==========
    /** 骑手ID，null表示未分配骑手 */
    private Long riderId;
    /** 骑手姓名 */
    private String riderName;
    /** 骑手电话 */
    private String riderPhone;
    /** 骑手接单时间 */
    private LocalDateTime riderTakeTime;
    /** 骑手实时位置（配送中才有） */
    private RiderLocationVO riderLocation;
}