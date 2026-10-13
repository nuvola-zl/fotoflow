package com.ff.mapper;

import com.ff.entity.Rider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 骑手数据访问层
 */
@Mapper
public interface RiderMapper {

    /**
     * 根据ID查询骑手
     */
    Rider selectById(Long id);

    /**
     * 根据手机号查询骑手
     */
    Rider selectByPhone(String phone);

    /**
     * 新增骑手
     */
    int insert(Rider rider);

    /**
     * 更新骑手工作状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 今日订单数 +1
     */
    int incrTodayOrders(Long id);

    /**
     * 总订单数 +1
     */
    int incrTotalOrders(Long id);

    /**
     * 每日零点重置今日订单数
     */
    int resetTodayOrders();
}