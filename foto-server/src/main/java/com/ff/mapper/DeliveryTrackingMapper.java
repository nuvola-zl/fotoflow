package com.ff.mapper;

import com.ff.entity.DeliveryTracking;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 配送轨迹数据访问层
 * 负责骑手实时位置的上报和查询
 */
@Mapper
public interface DeliveryTrackingMapper {

    /**
     * 插入一条配送轨迹记录
     * 骑手每次上报位置时调用，保留历史轨迹用于绘制配送路径
     *
     * @param tracking 轨迹实体（包含订单ID、经纬度、上报时间、速度等）
     * @return 影响行数
     */
    int insert(DeliveryTracking tracking);

    /**
     * 查询订单最新的配送轨迹
     * 用户查看"订单物流"时调用，取最新一条位置信息展示骑手当前位置
     *
     * @param orderId 订单ID
     * @return 最新一条配送轨迹，无轨迹则返回null
     */
    DeliveryTracking selectLatestByOrderId(Long orderId);

    /**
     * 查询订单完整轨迹（按时间正序，用于画路线）
     */
    List<DeliveryTracking> selectByOrderId(Long orderId);
}