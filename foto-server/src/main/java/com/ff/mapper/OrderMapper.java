package com.ff.mapper;

import com.ff.dto.OrdersPageDto;
import com.ff.entity.Orders;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    int insert(Orders orders);
    Orders selectById(Long id);
    Orders selectByOrderNo(String orderNo);

    /**
     * 乐观锁支付更新：只有 status=0 才能更新成功
     */
    int updateOnPay(@Param("id") Long id,
                    @Param("status") Integer status,
                    @Param("payType") Integer payType,
                    @Param("payTime") LocalDateTime payTime);


    /**
     * 用户查订单列表
     */
    List<Orders> selectByUserId(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 商家查店铺订单列表
     */
    List<Orders> selectByShopId(@Param("shopId") Long shopId, @Param("status") Integer status);

    /**
     * 商家发货更新
     */
    int updateDeliveryStart(@Param("id") Long id, @Param("status") Integer status, @Param("deliveryTime") LocalDateTime deliveryTime);

    /**
     * 用户确认收货更新
     */
    int updateReceiveConfirm(@Param("id") Long id, @Param("status") Integer status,
                             @Param("receiveTime") LocalDateTime receiveTime,
                             @Param("finishTime") LocalDateTime finishTime);


    /**
     * 查询【超时未支付】的订单
     * 用于：订单超时自动取消任务
     * 规则：创建时间 < 指定时间（如15分钟前） + 订单状态=待支付(0) + 未删除
     *
     * @param timeoutTime 超时时间点（当前时间 - 15分钟）
     * @return 超时订单列表
     */
    List<Orders> selectTimeoutOrders(@Param("timeoutTime") LocalDateTime timeoutTime);

    /**
     * 查询【待催付】的订单
     * 用于：下单10~15分钟未支付，发送支付提醒
     * 规则：创建时间在 [15分钟前 ~ 10分钟前] 之间 + 状态=待支付(0) + 未删除
     *
     * @param startTime  开始时间（15分钟前）
     * @param endTime    结束时间（10分钟前）
     * @return 待催付订单列表
     */
    List<Orders> selectTimeoutNotifyList(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 更新订单为取消状态（乐观锁：只有 status=0 才能更新成功）
     *
     * @param id     订单ID
     * @param status 目标状态（5=已取消）
     * @return 影响行数（0表示已被其他线程更新）
     */
    int updateCancelStatus(@Param("id") Long id, @Param("status") Integer status);



    /**
     * 统一修改订单状态
     */
    int updateOrderStatus(@Param("id") Long id, @Param("status") Integer status);


    /**
     * 骑手开始配送（status 2→3）
     */
    int updateStartDelivery(@Param("id") Long id, @Param("status") Integer status);



    /**
     * 查询待接单池
     */
    List<Orders> selectPendingOrders();

    /**
     * 骑手抢单（乐观锁）
     */
    int updateRider(@Param("id") Long id, @Param("riderId") Long riderId,
                    @Param("riderName") String riderName, @Param("riderPhone") String riderPhone,
                    @Param("riderTakeTime") LocalDateTime riderTakeTime);

    /**
     * 查询骑手今日订单
     */
    List<Orders> selectByRiderIdAndToday(@Param("riderId") Long riderId);


}
