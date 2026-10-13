package com.ff.mapper;

import com.ff.entity.OrderStatusLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderStatusLogMapper {
    int insert(OrderStatusLog log);
}
