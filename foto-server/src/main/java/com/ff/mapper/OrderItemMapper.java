package com.ff.mapper;

import com.ff.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface OrderItemMapper {
    int insert(OrderItem orderItem);
    List<OrderItem> selectByOrderId(Long orderId);
}