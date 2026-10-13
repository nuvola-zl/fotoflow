package com.ff.mapper;

import com.ff.dto.OrdersPageDto;
import com.ff.entity.Orders;
import com.ff.vo.MyOrdersVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 新增订单
     * @param orders 订单实体
     */
    void insert(Orders orders);

    /**
     * 商家接受订单
     * @param id 订单ID
     */
    @Update("update orders set status = 2 where id = #{id}")
    void acceptOrder(Long id);

    /**
     * 商家取消订单
     * @param id 订单ID
     */
    @Update("update orders set status = 3 where id = #{id}")
    void cancelOrder(Long id);

    /**
     * 商家完成订单
     * @param id 订单ID
     */
    @Update("update orders set status = 4, finish_time = now() where id = #{id} and status = 5")
    void completeOrder(Long id);

    @Update("update orders set status = 5 where id = #{id} and user_id = #{userId} and status = 2")
    int confirmOrder(@Param("id") Long id, @Param("userId") Long userId);

    @Update("update orders set delivery_longitude = #{deliveryLongitude}, delivery_latitude = #{deliveryLatitude} where id = #{id}")
    void updateDeliveryLocation(@Param("id") Long id,
                                @Param("deliveryLongitude") Double deliveryLongitude,
                                @Param("deliveryLatitude") Double deliveryLatitude);

    @Select("select * from orders where id = #{id}")
    Orders selectById(Long id);

    @Update("update orders set status = 3 where id = #{id} and user_id = #{userId} and status in (0, 1)")
    int cancelMyOrderByUser(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 商家删除订单
     * @param id 订单ID
     */
    @Delete("delete from orders where id = #{id}")
    void deleteOrder(Long id);

    /**
     * 查询我的订单
     * @param ordersPageDto 订单分页查询
     * @return 订单分页查询结果
     */
    @Select("select * from orders where user_id = #{userId} order by id desc")
    Page<MyOrdersVO> selectMyOrder(OrdersPageDto ordersPageDto, Long userId);

    /**
     * 用户取消订单（兼容旧接口，建议使用 cancelMyOrderByUser）
     */
    @Update("update orders set status = 3 where id = #{id}")
    void cancelMylOrder(Long id);

    /**
     * 根据店铺配置ID列表查询订单
     */
    @Select("<script>" +
            "select * from orders where shop_config_id in " +
            "<foreach collection='shopConfigIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " order by id desc" +
            "</script>")
    Page<MyOrdersVO> selectByShopConfigIds(@Param("shopConfigIds") List<Long> shopConfigIds);
}
