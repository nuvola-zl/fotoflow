package com.ff.service;

import com.ff.dto.OrderBuyNowDto;
import com.ff.dto.OrderPayDto;
import com.ff.dto.OrderQueryDto;
import com.ff.dto.OrdersPageDto;
import com.ff.result.PageResult;
import com.ff.vo.OrderDetailVO;
import com.ff.vo.OrderListVO;
import com.ff.vo.OrderVO;

public interface OrderService {
    /**
     * 立即购买（生成预订单，status=0 待付款）
     */
    OrderVO buyNow(OrderBuyNowDto dto);

    /**
     * 支付（模拟支付回调，status=0→1，扣库存）
     *  实际项目这里应该是微信/支付宝回调，你先调这个模拟支付流程
     * @param dto
     * @return
     */
    OrderVO pay(OrderPayDto dto);


    /**
     * 用户查询自己的订单列表（分页）
     * 可按状态筛选
     *
     * @param dto 查询条件 + 分页参数
     * @return 分页订单列表
     */
    PageResult<OrderListVO> queryUserOrderList(OrderQueryDto dto);

    /**
     * 商家查询自己店铺的订单列表（分页）
     * 可按状态筛选
     *
     * @param dto 查询条件 + 分页参数
     * @return 分页订单列表
     */
    PageResult<OrderListVO> queryShopOrderList(OrderQueryDto dto);

    /**
     * 查询订单详情（用户/商家共用）
     * 包含完整商品明细和最新物流轨迹
     *
     * @param orderNo 订单号
     * @return 订单详情
     */
    OrderDetailVO getOrderDetail(String orderNo);

    /**
     * 商家发货
     * 订单状态 1→2，记录发货时间，初始化配送轨迹
     *
     * @param orderNo 订单号
     */
    void deliveryStart(String orderNo);

    /**
     * 用户确认收货
     * 订单状态 2/3→4，记录完成时间
     *
     * @param orderNo 订单号
     */
    void receiveConfirm(String orderNo);

    /**
     * 用户取消订单
     * 只能取消待付款（status=0）的订单，取消后回滚库存
     *
     * @param orderNo 订单号
     */
    void cancelOrder(String orderNo);

}
