package com.ff.service;

import com.ff.dto.*;
import com.ff.result.PageResult;
import com.ff.vo.MyOrdersVO;
import com.ff.vo.OrderItemVO;
import com.ff.vo.ShopConfigShowConfigVO;
import com.ff.vo.UserAddressVO;

import java.util.List;

public interface GoodsService {
    /**
     * 查询出的商品
     * @param goodsPageDto
     * @return
     */
    PageResult selectGoodsRandom(GoodsPageDto goodsPageDto);

    void updateShopConfig(ShopConfigDto shopConfigDto);

    void addGoods(GoodsAddDto goodsAddDto);

    OrderItemVO addMyGoods(Long id);

    OrderItemVO addMyGoods(Long id, Long addressId);

    OrderItemVO SetOrder(OrderItemDto orderItemDto);

    /**
     * 新增店铺配置
     * @param shopConfigDto
     * @return 返回生成的店铺配置ID
     */
    Long addShopConfig(ShopConfigDto shopConfigDto);

    /**
     * 查询店铺配置
     * @return
     */
    List<ShopConfigShowConfigVO> showShopsConfig();


    /**
     * 商家查询所有订单
     * @return
     */
    PageResult orderAll(OrdersPageDto ordersPageDto);

    /**
     * 商家接受订单
     * @param id
     */
    void acceptOrder(Long id);

    /**
     * 商家取消订单
     * @param id
     */
    void cancelOrder(Long id);

    /**
     * 商家完成订单
     * @param id
     */
    void completeOrder(Long id);

    /**
     * 商家删除订单
     * @param id
     */
    void deleteOrder(Long id);

    /**
     * 用户查看我的订单
     * @return
     */
    PageResult MyOrder(OrdersPageDto ordersPageDto);

    /**
     * 用户取消订单
     * @param id
     */
    void cancelMylOrder(Long id);

    /**
     * 查看我的收货地址
     * @return
     */
    List<UserAddressVO> MyAddress();

    /**
     * 查看我的收货地址详情的回显
     * @param id
     * @return
     */
    UserAddressVO MyAddressShow(Long id);


    /**
     * 更新我的收货地址
     * @param id
     * @param userAddressDto
     */
    void MyAddressUpdate(Long id, UserAddressDto userAddressDto);

    void MyAddressAdd(UserAddressDto userAddressDto);

    void MyAddressDelete(Long id);

    void confirmOrder(Long id);

    MyOrdersVO myOrderDetail(Long id);

    void updateDeliveryLocation(Long id, DeliveryLocationDto dto);

    void updateGoods(GoodsUpdateDto goodsUpdateDto);

}
