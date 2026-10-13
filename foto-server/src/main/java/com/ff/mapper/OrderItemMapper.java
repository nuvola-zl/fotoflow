package com.ff.mapper;

import com.ff.entity.OrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper {

    @Insert("insert into order_item (order_no, goods_id, goods_name, goods_img, price, count, create_time) " +
            "values (#{orderNo}, #{goodsId}, #{goodsName}, #{goodsImg}, #{price}, #{count}, #{createTime})")
    void insert(OrderItem orderItem);
}
