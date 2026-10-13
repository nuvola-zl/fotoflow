package com.ff.constant;

/**
 * 订单状态：0待付款 1已付款待接单 2配送中 3已取消 4已完成 5用户已确认收货
 */
public class OrderStatusConstant {

    public static final int PENDING_PAY = 0;
    public static final int PAID_PENDING_ACCEPT = 1;
    public static final int DELIVERING = 2;
    public static final int CANCELLED = 3;
    public static final int COMPLETED = 4;
    public static final int USER_CONFIRMED = 5;

    private OrderStatusConstant() {
    }
}
