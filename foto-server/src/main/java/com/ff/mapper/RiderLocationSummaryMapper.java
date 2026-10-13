package com.ff.mapper;

import com.ff.entity.RiderLocationSummary;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RiderLocationSummaryMapper {

    /**
     * 保存或更新骑手位置汇总
     */
    int saveOrUpdate(RiderLocationSummary summary);

    /**
     * 根据订单ID查询最新位置
     */
    RiderLocationSummary selectByOrderId(Long orderId);
}