package com.ff.dto;

import lombok.Data;

/**
 * 订单查询参数（分页）
 * 用户端和商家端共用
 */
@Data
public class OrderQueryDto {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    /** 状态筛选：0待付款 1已付款 2已发货 3配送中 4已完成 5已取消 */
    private Integer status;
}