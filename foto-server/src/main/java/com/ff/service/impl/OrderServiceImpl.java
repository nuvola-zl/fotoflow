
package com.ff.service.impl;
import com.ff.constant.OrderStatusConstant;
import com.ff.context.BaseContext;
import com.ff.dto.OrderBuyNowDto;
import com.ff.dto.OrderPayDto;
import com.ff.dto.OrderQueryDto;
import com.ff.entity.*;
import com.ff.exception.BaseException;
import com.ff.exception.CodeException;
import com.ff.mapper.*;
import com.ff.result.PageResult;
import com.ff.service.OrderNotifyService;
import com.ff.service.OrderService;
import com.ff.vo.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

     private final GoodsMapper goodsMapper;
     private final ShopConfigMapper shopConfigMapper;
     private final UserAddressMapper userAddressMapper;
     private final OrderMapper orderMapper;
     private final OrderItemMapper orderItemMapper;
     private final OrderStatusLogMapper orderStatusLogMapper;
     private final DeliveryTrackingMapper deliveryTrackingMapper;
     private final OrderNotifyService orderNotifyService;

    /**
     * 立即购买：生成预订单（status=0），不扣库存
     */
    @Override
    @Transactional
    public OrderVO buyNow(OrderBuyNowDto dto) {
        Long userId = BaseContext.getCurrentId();

        // 1. 校验商品
        Goods goods = goodsMapper.selectById(dto.getGoodsId());
        if (goods == null || goods.getDeleted() == 1) {
            throw new BaseException(CodeException.NOT_FOUND, "商品不存在");
        }
        if (goods.getStatus() != 1) {
            throw new BaseException(CodeException.NOT_FOUND, "商品已下架");
        }

        // 2. 数量校验（默认1，必须>0）
        int quantity = (dto.getQuantity() == null || dto.getQuantity() <= 0) ? 1 : dto.getQuantity();
        if (goods.getStock() < quantity) {
            throw new BaseException(CodeException.NOT_FOUND, "库存不足，剩余 " + goods.getStock() + goods.getUnit());
        }

        // 3. 解析地址（3级兜底）
        UserAddress address = resolveAddress(userId, dto);

        // 4. 查店铺配置
        ShopConfig shop = shopConfigMapper.selectById(goods.getGoodsConfigId());
        if (shop == null) {
            throw new BaseException(CodeException.NOT_FOUND, "店铺配置不存在");
        }

        // 5. 生成订单号
        String orderNo = generateOrderNo();

        // 6. 计算金额
        int totalAmount = goods.getPrice() * quantity;
        int freightAmount = 0;      // 后续可接入运费模板
        int discountAmount = 0;
        int payAmount = totalAmount + freightAmount - discountAmount;

        // 7. 构建订单主表（全是快照！与用户表、店铺表解耦）
        Orders order = Orders.builder()
                .orderNo(orderNo)
                .userId(userId)
                .totalAmount(totalAmount)
                .freightAmount(freightAmount)
                .discountAmount(discountAmount)
                .payAmount(payAmount)
                .totalQuantity(quantity)
                .status(OrderStatusConstant.PENDING)                          // 0待付款
                .remark(dto.getRemark())
                .receiver(address.getReceiver())
                .phone(address.getPhone())
                .addressDetail(address.getAddressDetail())
                .buyerLongitude(address.getLongitude())
                .buyerLatitude(address.getLatitude())
                .shopId(shop.getId())
                .shopName(shop.getShopName())
                .shopLongitude(shop.getLongitude())
                .shopLatitude(shop.getLatitude())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .deleted(0)
                .build();

        orderMapper.insert(order);

        // 8. 构建订单明细（商品快照）
        OrderItem item = OrderItem.builder()
                .orderId(order.getId())
                .goodsId(goods.getId())
                .goodsName(goods.getGoodsName())
                .goodsImg(goods.getGoodsImg())
                .goodsSpec(null)                    // 暂无规格，后续扩展
                .unit(goods.getUnit())
                .price(goods.getPrice())
                .quantity(quantity)
                .subtotal(totalAmount)
                .deliveryStatus(0)
                .createTime(LocalDateTime.now())
                .build();

        orderItemMapper.insert(item);

        // 9. 记录状态日志（审计用）
        orderStatusLogMapper.insert(OrderStatusLog.builder()
                .orderId(order.getId())
                .beforeStatus(null)
                .afterStatus(0)
                .operator("system")
                .remark("创建预订单-立即购买")
                .createTime(LocalDateTime.now())
                .build());

        // 10. 返回VO
        return assembleOrderVO(order, Collections.singletonList(item));
    }

    /**
     * 支付成功
     * 1. 更新订单状态
     * 2. 扣库存
     * 3. 【异步】发送支付成功通知
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO pay(OrderPayDto dto) {
        Long userId = BaseContext.getCurrentId();

        Orders order = orderMapper.selectByOrderNo(dto.getOrderNo());
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BaseException(CodeException.NOT_FOUND, "订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BaseException(CodeException.NOT_FOUND, "订单状态异常，请勿重复支付");
        }

        // 乐观锁更新：只有 status=0 的才能被更新
        int rows = orderMapper.updateOnPay(order.getId(), 1, dto.getPayType(), LocalDateTime.now());
        if (rows == 0) {
            throw new BaseException(CodeException.COMPLETED, "订单已支付，请勿重复提交");
        }

        // 支付成功，扣减库存 + 增加销量
        List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
        for (OrderItem item : items) {
            int affected = goodsMapper.decreaseStock(item.getGoodsId(), item.getQuantity());
            if (affected == 0) {
                throw new BaseException(CodeException.NOT_FOUND, "商品【" + item.getGoodsName() + "】库存不足，支付失败");
            }
        }

        // 记录状态变更日志
        orderStatusLogMapper.insert(OrderStatusLog.builder()
                .orderId(order.getId())
                .beforeStatus(0)
                .afterStatus(1)
                .operator("user")
                .remark("用户支付成功，支付类型：" + (dto.getPayType() == 1 ? "微信" : "支付宝"))
                .createTime(LocalDateTime.now())
                .build());

        // 重新查询最新订单数据返回
        Orders freshOrder = orderMapper.selectById(order.getId());

        // ====================== 异步通知（新加） ======================
        orderNotifyService.notifyPaySuccess(dto.getOrderNo());

        return assembleOrderVO(freshOrder, items);
    }

    // ==================== 私有方法 ====================

    /**
     * 地址解析：addressId > 手动传入 > 默认地址
     */
    private UserAddress resolveAddress(Long userId, OrderBuyNowDto dto) {
        // 优先级1：传了 addressId，查地址簿并校验归属
        if (dto.getAddressId() != null) {
            UserAddress addr = userAddressMapper.selectById(dto.getAddressId());
            if (addr == null) throw new BaseException(CodeException.NOT_FOUND, "地址不存在");
            if (!addr.getUserId().equals(userId)) throw new BaseException(CodeException.FORBIDDEN, "地址不属于当前用户");
            return addr;
        }

        // 优先级2：前端手动传入完整地址（新用户首次下单）
        if (dto.getAddressDetail() != null && dto.getPhone() != null && dto.getReceiver() != null) {
            return UserAddress.builder()
                    .addressDetail(dto.getAddressDetail())
                    .phone(dto.getPhone())
                    .receiver(dto.getReceiver())
                    .longitude(dto.getBuyerLongitude())
                    .latitude(dto.getBuyerLatitude())
                    .build();
        }

        // 优先级3：取默认地址
        UserAddress defaultAddr = userAddressMapper.selectDefaultByUserId(userId);
        if (defaultAddr != null) return defaultAddr;

        throw new BaseException(CodeException.NOT_FOUND, "请选择收货地址");
    }

    /**
     * 生成订单号：ORD + 时间戳 + 4位随机数
     * 后续并发高了建议换成雪花算法或Redis自增
     */
    private String generateOrderNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return "ORD" + time + random;
    }

    /**
     * 组装返回VO
     */
    private OrderVO assembleOrderVO(Orders order, List<OrderItem> items) {
        List<OrderItemVO> itemVOs = items.stream().map(i -> OrderItemVO.builder()
                .goodsId(i.getGoodsId())
                .goodsName(i.getGoodsName())
                .goodsImg(i.getGoodsImg())
                .unit(i.getUnit())
                .price(i.getPrice())
                .quantity(i.getQuantity())
                .subtotal(i.getSubtotal())
                .build()).collect(Collectors.toList());

        return OrderVO.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .totalAmount(order.getTotalAmount())
                .payAmount(order.getPayAmount())
                .status(order.getStatus())
                .statusDesc(getStatusDesc(order.getStatus()))
                .receiver(order.getReceiver())
                .phone(order.getPhone())
                .addressDetail(order.getAddressDetail())
                .createTime(order.getCreateTime())
                .payTime(order.getPayTime())
                .items(itemVOs)
                .build();
    }

    private String getStatusDesc(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "待付款";
            case 1 -> "已付款待发货";
            case 2 -> "已发货待接单";  // 商家发货，等骑手接单
            case 3 -> "配送中";         // 骑手已接单，正在配送
            case 4 -> "已完成";
            case 5 -> "已取消";
            default -> "未知";
        };
    }

    /**
     * 用户查询自己的订单列表
     * 只查当前用户的订单，按创建时间倒序
     */
    @Override
    public PageResult<OrderListVO> queryUserOrderList(OrderQueryDto dto) {
        Long userId = BaseContext.getCurrentId();

        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<Orders> list = orderMapper.selectByUserId(userId, dto.getStatus());

        Page<Orders> page = (Page<Orders>) list;

        List<OrderListVO> voList = list.stream()
                .map(this::toOrderListVO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), voList);
    }

    /**
     * 商家查询自己店铺的订单列表
     * 先查商家的 shop_id，再查该店铺的订单
     */
    @Override
    public PageResult<OrderListVO> queryShopOrderList(OrderQueryDto dto) {
        Long userId = BaseContext.getCurrentId();

        // 获取商家店铺ID
        ShopConfig shop = shopConfigMapper.selectByUserId(userId);
        if (shop == null) {
            return new PageResult<>();
        }

        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<Orders> list = orderMapper.selectByShopId(shop.getId(), dto.getStatus());

        Page<Orders> page = (Page<Orders>) list;

        List<OrderListVO> voList = list.stream()
                .map(this::toOrderListVO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), voList);
    }

    /**
     * 查询订单详情
     * 包含：订单主表 + 所有商品明细 + 最新物流轨迹
     * 校验：用户只能查自己的，商家只能查自己店铺的
     */
    @Override
    public OrderDetailVO getOrderDetail(String orderNo) {
        Long userId = BaseContext.getCurrentId();

        Orders order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || order.getDeleted() == 1) {
            throw new BaseException(CodeException.NOT_FOUND, "订单不存在");
        }

        // 权限校验：要么是买家本人，要么是店铺商家
        ShopConfig shop = shopConfigMapper.selectById(order.getShopId());
        boolean isBuyer = order.getUserId().equals(userId);
        boolean isSeller = (shop != null && shop.getUserId().equals(userId));
        if (!isBuyer && !isSeller) {
            throw new BaseException(CodeException.UNAUTHORIZED, "无权查看该订单");
        }

        // 查商品明细
        List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
        List<OrderItemVO> itemVOs = items.stream().map(i -> OrderItemVO.builder()
                .goodsId(i.getGoodsId())
                .goodsName(i.getGoodsName())
                .goodsImg(i.getGoodsImg())
                .unit(i.getUnit())
                .price(i.getPrice())
                .quantity(i.getQuantity())
                .subtotal(i.getSubtotal())
                .build()).collect(Collectors.toList());

        // 查最新物流
        DeliveryTrackingVO trackingVO = null;
        if (order.getStatus() >= 2 && order.getStatus() <= 3) {
            DeliveryTracking tracking = deliveryTrackingMapper.selectLatestByOrderId(order.getId());
            if (tracking != null) {
                trackingVO = DeliveryTrackingVO.builder()
                        .currentLongitude(tracking.getCurrentLongitude())
                        .currentLatitude(tracking.getCurrentLatitude())
                        .locationTime(tracking.getLocationTime())
                        .distanceLeft(tracking.getDistanceLeft())
                        .build();
            }
        }

        return OrderDetailVO.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .totalAmount(order.getTotalAmount())
                .freightAmount(order.getFreightAmount())
                .discountAmount(order.getDiscountAmount())
                .payAmount(order.getPayAmount())
                .totalQuantity(order.getTotalQuantity())
                .status(order.getStatus())
                .statusDesc(getStatusDesc(order.getStatus()))
                .remark(order.getRemark())
                .receiver(order.getReceiver())
                .phone(order.getPhone())
                .addressDetail(order.getAddressDetail())
                .buyerLongitude(order.getBuyerLongitude())
                .buyerLatitude(order.getBuyerLatitude())
                .shopId(order.getShopId())
                .shopName(order.getShopName())
                .shopLongitude(order.getShopLongitude())
                .shopLatitude(order.getShopLatitude())
                //骑手信息
                .riderId(order.getRiderId())
                .riderName(order.getRiderName())
                .riderPhone(order.getRiderPhone())
                .riderTakeTime(order.getRiderTakeTime())
                .createTime(order.getCreateTime())
                .payTime(order.getPayTime())
                .deliveryTime(order.getDeliveryTime())
                .receiveTime(order.getReceiveTime())
                .items(itemVOs)
                .latestTracking(trackingVO)
                .build();
    }

    /**
     * 商家发货
     * 状态流转：1已付款 → 2已发货
     * 记录发货时间，初始化配送轨迹（从店铺坐标出发）
     */
    @Override
    @Transactional
    public void deliveryStart(String orderNo) {
        Long userId = BaseContext.getCurrentId();

        Orders order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || order.getDeleted() == 1) {
            throw new BaseException(CodeException.NOT_FOUND, "订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BaseException(CodeException.STATUS_ERROR, "订单状态异常，非待发货状态");
        }

        // 校验商家权限：订单的 shop_id 必须属于当前商家
        ShopConfig shop = shopConfigMapper.selectById(order.getShopId());
        if (shop == null || !shop.getUserId().equals(userId)) {
            throw new BaseException(CodeException.UNAUTHORIZED, "无权操作该订单");
        }

        // 更新订单状态
        orderMapper.updateDeliveryStart(order.getId(), 2, LocalDateTime.now());

        // 初始化配送轨迹：从店铺坐标出发
        DeliveryTracking tracking = new DeliveryTracking();
        tracking.setOrderId(order.getId());
        tracking.setRiderId(null); // 后续分配骑手时更新
        tracking.setCurrentLongitude(order.getShopLongitude());
        tracking.setCurrentLatitude(order.getShopLatitude());
        tracking.setLocationTime(LocalDateTime.now());
        tracking.setDistanceLeft(null);
        tracking.setCreateTime(LocalDateTime.now());
        deliveryTrackingMapper.insert(tracking);

        // 记录日志
        orderStatusLogMapper.insert(OrderStatusLog.builder()
                .orderId(order.getId())
                .beforeStatus(1)
                .afterStatus(2)
                .operator(String.valueOf(userId))
                .remark("商家发货")
                .createTime(LocalDateTime.now())
                .build());

        // ====================== 异步通知（新加） ======================
        orderNotifyService.notifyDeliveryStart(orderNo);
    }

    /**
     * 用户确认收货
     * 状态流转：3派送中 → 4已完成
     */
    @Override
    @Transactional
    public void receiveConfirm(String orderNo) {
        Long userId = BaseContext.getCurrentId();

        Orders order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || order.getDeleted() == 1) {
            throw new BaseException(CodeException.NOT_FOUND, "订单不存在");
        }
        // 只有派送中(3)才能确认收货
        if (order.getStatus() != 3) {
            throw new BaseException(CodeException.STATUS_ERROR,"订单状态异常，无法确认收货");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BaseException(CodeException.UNAUTHORIZED, "无权操作该订单");
        }

        LocalDateTime now = LocalDateTime.now();
        int rows = orderMapper.updateReceiveConfirm(order.getId(), 4, now, now);
        if (rows == 0) {
            throw new BaseException(CodeException.STATUS_ERROR, "订单状态已变更，请刷新重试");
        }

        orderStatusLogMapper.insert(OrderStatusLog.builder()
                .orderId(order.getId())
                .beforeStatus(3)
                .afterStatus(4)
                .operator(String.valueOf(userId))
                .remark("用户确认收货")
                .createTime(now)
                .build());

        orderNotifyService.notifyReceiveConfirm(orderNo);
    }
    // ==================== 私有方法 ====================

    /**
     * 将 Orders 转换为列表页 VO
     * 只取第一条商品作为展示缩略图
     */
    private OrderListVO toOrderListVO(Orders order) {
        List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
        List<OrderItemVO> itemVOs = items.stream()
                .limit(2) // 列表页只展示前2条商品
                .map(i -> OrderItemVO.builder()
                        .goodsId(i.getGoodsId())
                        .goodsName(i.getGoodsName())
                        .goodsImg(i.getGoodsImg())
                        .unit(i.getUnit())
                        .price(i.getPrice())
                        .quantity(i.getQuantity())
                        .subtotal(i.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return OrderListVO.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .totalAmount(order.getTotalAmount())
                .payAmount(order.getPayAmount())
                .status(order.getStatus())
                .statusDesc(getStatusDesc(order.getStatus()))
                .receiver(order.getReceiver())
                .phone(order.getPhone())
                .addressDetail(order.getAddressDetail())
                .createTime(order.getCreateTime())
                .itemCount(items.size())
                .items(itemVOs)
                .build();
    }

    /**
     * 用户主动取消订单
     * 流程：
     * 1. 校验订单存在且未删除
     * 2. 校验当前用户是订单买家（只能取消自己的）
     * 3. 校验订单状态为待付款（status=0），已付款/已发货的不能取消
     * 4. 回滚库存：遍历订单明细，把商品库存加回去
     * 5. 更新订单状态为已取消（status=5）
     * 6. 记录状态变更日志
     */
    @Override
    @Transactional
    public void cancelOrder(String orderNo) {
        Long userId = BaseContext.getCurrentId();

        // 1. 查询订单
        Orders order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || order.getDeleted() == 1) {
            throw new BaseException(CodeException.NOT_FOUND, "订单不存在");
        }

        // 2. 权限校验：只能取消自己的订单
        if (!order.getUserId().equals(userId)) {
            throw new BaseException(CodeException.UNAUTHORIZED, "无权操作该订单");
        }

        // 3. 状态校验：只有待付款（0）才能取消
        if (order.getStatus() != 0) {
            throw new BaseException(CodeException.STATUS_ERROR, "订单状态异常，无法取消");
        }

        // 4. 回滚库存：查订单明细，逐条恢复商品库存
        List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
        for (OrderItem item : items) {
            goodsMapper.increaseStock(item.getGoodsId(), item.getQuantity());
        }

        // 5. 更新订单状态为已取消（5），乐观锁防止并发重复取消
        int rows = orderMapper.updateCancelStatus(order.getId(), 5);
        if (rows == 0) {
            throw new BaseException(CodeException.STATUS_ERROR, "订单状态已变更，请刷新后重试");
        }

        // 6. 记录状态日志
        orderStatusLogMapper.insert(OrderStatusLog.builder()
                .orderId(order.getId())
                .beforeStatus(0)
                .afterStatus(5)
                .operator(String.valueOf(userId))
                .remark("用户主动取消订单")
                .createTime(LocalDateTime.now())
                .build());
    }


}