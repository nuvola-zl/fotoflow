package com.ff.service.impl;

import com.ff.entity.Orders;
import com.ff.mapper.OrderMapper;
import com.ff.service.OrderNotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 订单通知实现
 * 所有通知方法异步执行，不阻塞主流程
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderNotifyServiceImpl implements OrderNotifyService {

    private final OrderMapper orderMapper;

    /**
     * 支付成功通知
     * 异步发送：微信订阅消息 / 短信 / 站内信
     */
    @Override
    @Async("orderNotifyExecutor")
    public void notifyPaySuccess(String orderNo) {
        Orders order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) return;

        try {
            // 1. 微信订阅消息（如果用户关注了）
            sendWxSubscribeMessage(order.getUserId(), "您的订单已支付成功，商家准备发货");

            // 2. 短信通知（如果用户绑定了手机号）
            if (order.getPhone() != null) {
                sendSms(order.getPhone(), "【XX平台】您的订单" + orderNo + "已支付成功");
            }

            log.info("支付成功通知完成, orderNo={}, userId={}", orderNo, order.getUserId());
        } catch (Exception e) {
            log.error("支付通知发送失败, orderNo={}", orderNo, e);
            // 通知失败不影响主流程，记录日志即可
        }
    }

    /**
     * 商家发货通知用户
     */
    @Override
    @Async("orderNotifyExecutor")
    public void notifyDeliveryStart(String orderNo) {
        Orders order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) return;

        try {
            sendWxSubscribeMessage(order.getUserId(), "您的订单已发货，请注意查收");

            log.info("发货通知完成, orderNo={}", orderNo);
        } catch (Exception e) {
            log.error("发货通知失败, orderNo={}", orderNo, e);
        }
    }

    /**
     * 用户确认收货通知商家
     */
    @Override
    @Async("orderNotifyExecutor")
    public void notifyReceiveConfirm(String orderNo) {
        Orders order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) return;

        try {
            // 通知商家：订单已完成，可以结算
            Long shopId = order.getShopId();
            sendMerchantNotify(shopId, "订单" + orderNo + "用户已确认收货");

            log.info("收货通知完成, orderNo={}, shopId={}", orderNo, shopId);
        } catch (Exception e) {
            log.error("收货通知失败, orderNo={}", orderNo, e);
        }
    }

    /**
     * 订单超时未支付催付通知
     * 下单后 10 分钟触发
     */
    @Override
    @Async("orderNotifyExecutor")
    public void notifyPayTimeout(String orderNo) {
        Orders order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || order.getStatus() != 0) return; // 已支付不通知

        try {
            sendWxSubscribeMessage(order.getUserId(), "您的订单即将超时关闭，请及时支付");

            log.info("催付通知完成, orderNo={}", orderNo);
        } catch (Exception e) {
            log.error("催付通知失败, orderNo={}", orderNo, e);
        }
    }

    // ==================== 私有发送方法（占位，后续接入真实通道）====================

    private void sendWxSubscribeMessage(Long userId, String content) {
        // TODO: 接入微信订阅消息 API
        log.debug("发送微信订阅消息, userId={}, content={}", userId, content);
    }

    private void sendSms(String phone, String content) {
        // TODO: 接入阿里云短信 / 腾讯云短信
        log.debug("发送短信, phone={}, content={}", phone, content);
    }

    private void sendMerchantNotify(Long shopId, String content) {
        // TODO: 接入商家端 WebSocket 推送或 APP 推送
        log.debug("通知商家, shopId={}, content={}", shopId, content);
    }
}