package com.ff.constant;

public class OrderStatusConstant {

    /** 待付款 */
    public static final int PENDING = 0;
    /** 已付款待发货 */
    public static final int SHIPPED = 1;
    /** 已发货 */
    public static final int RECEIVED = 2;
    /** 派送中 */
    public static final int DELIVERING = 3;
    /** 已完成 */
    public static final int COMPLETED = 4;
    /** 已取消 */
    public static final int CANCELLED = 5;
}
