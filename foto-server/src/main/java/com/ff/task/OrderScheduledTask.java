package com.ff.task;

import com.ff.entity.OrderItem;
import com.ff.entity.OrderStatusLog;
import com.ff.entity.Orders;
import com.ff.mapper.GoodsMapper;
import com.ff.mapper.OrderItemMapper;
import com.ff.mapper.OrderMapper;
import com.ff.mapper.OrderStatusLogMapper;
import com.ff.service.OrderNotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单定时任务
 * 包含：超时自动取消、催付通知
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduledTask {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final GoodsMapper goodsMapper;
    private final OrderStatusLogMapper orderStatusLogMapper;
    private final OrderNotifyService orderNotifyService;

    /** 超时时间：15分钟 */
    private static final int TIMEOUT_MINUTES = 15;
    /** 催付时间：10分钟（超时前5分钟） */
    private static final int NOTIFY_MINUTES = 10;

    /**
     * 自动取消超时订单
     * 每5分钟执行一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    @Transactional
    public void autoCancelTimeoutOrders() {
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(TIMEOUT_MINUTES);
        List<Orders> timeoutOrders = orderMapper.selectTimeoutOrders(timeoutTime);

        if (timeoutOrders.isEmpty()) return;

        log.info("扫描到 {} 个超时未支付订单，准备自动取消", timeoutOrders.size());

        for (Orders order : timeoutOrders) {
            try {
                // 回滚库存
                List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
                for (OrderItem item : items) {
                    goodsMapper.increaseStock(item.getGoodsId(), item.getQuantity());
                }

                // 取消订单
                int rows = orderMapper.updateCancelStatus(order.getId(), 5);
                if (rows == 0) {
                    log.warn("订单 {} 已被处理，跳过", order.getOrderNo());
                    continue;
                }

                // 记录日志
                orderStatusLogMapper.insert(OrderStatusLog.builder()
                        .orderId(order.getId())
                        .beforeStatus(0)
                        .afterStatus(5)
                        .operator("system")
                        .remark("系统自动取消：超时未支付")
                        .createTime(LocalDateTime.now())
                        .build());

                log.info("订单 {} 已自动取消", order.getOrderNo());

            } catch (Exception e) {
                log.error("自动取消订单 {} 失败: {}", order.getOrderNo(), e.getMessage());
            }
        }
    }

    /**
     * 催付通知
     * 每5分钟执行一次，通知下单10分钟仍未支付的订单
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void notifyPayTimeout() {
        // 10-15分钟前创建的订单，状态仍为0
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(TIMEOUT_MINUTES);
        LocalDateTime endTime = LocalDateTime.now().minusMinutes(NOTIFY_MINUTES);

        List<Orders> pendingOrders = orderMapper.selectTimeoutNotifyList(startTime, endTime);

        if (pendingOrders.isEmpty()) return;

        log.info("扫描到 {} 个待催付订单", pendingOrders.size());

        for (Orders order : pendingOrders) {
            try {
                orderNotifyService.notifyPayTimeout(order.getOrderNo());
            } catch (Exception e) {
                log.error("催付通知失败, orderNo={}", order.getOrderNo(), e);
            }
        }
    }
}