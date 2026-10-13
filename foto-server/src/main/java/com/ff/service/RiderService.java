package com.ff.service;

import com.ff.dto.RiderLoginDto;
import com.ff.dto.RiderStatusDto;
import com.ff.result.PageResult;
import com.ff.vo.RiderOrderVO;
import com.ff.vo.RiderVO;

import java.util.List;

/**
 * 骑手服务接口
 */
public interface RiderService {

    /**
     * 切换工作状态
     */
    void updateStatus(RiderStatusDto dto);

    /**
     * 查询骑手信息
     */
    RiderVO getRiderInfo();

    /**
     * 查看待接单池（商家已发货，未绑定骑手的订单）
     */
    List<RiderOrderVO> getPendingOrders();

    /**
     * 抢单/接单
     */
    void takeOrder(String orderNo);

    /**
     * 开始配送（取货完成，出发）
     */
    void startDelivery(String orderNo);

    /**
     * 确认送达
     */
    void arriveOrder(String orderNo);

    /**
     * 查询今日订单
     */
    PageResult<RiderOrderVO> getTodayOrders(Integer pageNum, Integer pageSize);
}