package com.ff.service;

/**
 * 订单通知服务
 * 支付成功、发货、收货等节点通知用户和商家
 */
public interface OrderNotifyService {

    /**
     * 支付成功通知用户
     * @param orderNo 订单号
     */
    void notifyPaySuccess(String orderNo);

    /**
     * 商家发货通知用户
     * @param orderNo 订单号
     */
    void notifyDeliveryStart(String orderNo);

    /**
     * 用户确认收货通知商家
     * @param orderNo 订单号
     */
    void notifyReceiveConfirm(String orderNo);

    /**
     * 订单超时未支付通知用户（催付）
     * @param orderNo 订单号
     */
    void notifyPayTimeout(String orderNo);
}