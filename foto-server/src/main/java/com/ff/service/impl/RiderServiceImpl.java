package com.ff.service.impl;

import com.ff.dto.RiderLoginDto;
import com.ff.dto.RiderStatusDto;
import com.ff.entity.DeliveryTracking;
import com.ff.entity.Orders;
import com.ff.entity.Rider;
import com.ff.exception.BaseException;
import com.ff.mapper.DeliveryTrackingMapper;
import com.ff.mapper.OrderMapper;
import com.ff.mapper.RiderMapper;
import com.ff.result.PageResult;
import com.ff.service.RiderService;
import com.ff.vo.RiderOrderVO;
import com.ff.vo.RiderVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.ff.context.BaseContext.getCurrentId;

/**
 * 骑手业务实现类
 * 负责骑手状态管理、订单接单、配送、订单查询等核心逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RiderServiceImpl implements RiderService {

    private final RiderMapper riderMapper;
    private final OrderMapper orderMapper;
    private final DeliveryTrackingMapper deliveryTrackingMapper;

    /**
     * 切换骑手工作状态
     * 0 休息 ↔ 1 上班
     * 配送中状态 2 不允许切换
     */
    @Override
    public void updateStatus(RiderStatusDto dto) {
        Long riderId = getCurrentId();
        Rider rider = riderMapper.selectById(riderId);
        if (rider == null) {
            throw new BaseException("骑手不存在");
        }
        if (rider.getStatus() == 2) {
            throw new BaseException("配送中不能切换状态，请先完成当前订单");
        }

        riderMapper.updateStatus(riderId, dto.getStatus());
    }

    /**
     * 获取当前登录骑手的个人信息
     */
    @Override
    public RiderVO getRiderInfo() {
        Long riderId = getCurrentId();
        Rider rider = riderMapper.selectById(riderId);
        if (rider == null) {
            throw new BaseException("骑手不存在");
        }
        return toRiderVO(rider);
    }

    /**
     * 获取待接单订单池
     * 查询条件：已发货、未绑定骑手、状态为2
     */
    @Override
    public List<RiderOrderVO> getPendingOrders() {
        // 校验骑手状态
        Long riderId = getCurrentId();
        Rider rider = riderMapper.selectById(riderId);
        if (rider == null || rider.getStatus() == 0) {
            throw new BaseException("请先上班");
        }
        if (rider.getStatus() == 2) {
            throw new BaseException("您有配送中的订单，请先完成");
        }

        List<Orders> list = orderMapper.selectPendingOrders();
        return list.stream().map(this::toRiderOrderVO).collect(Collectors.toList());
    }

    /**
     * 骑手抢单/接单
     * 乐观锁保证订单不被重复抢走
     * 接单后骑手状态变为配送中
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void takeOrder(String orderNo) {
        Long riderId = getCurrentId();

        // 校验骑手状态
        Rider rider = riderMapper.selectById(riderId);
        if (rider == null || rider.getStatus() == 0) {
            throw new BaseException("请先上班");
        }
        if (rider.getStatus() == 2) {
            throw new BaseException("您有配送中的订单，请先完成");
        }

        // 查订单
        Orders order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || order.getDeleted() == 1) {
            throw new BaseException("订单不存在");
        }
        if (order.getStatus() != 2) {  // 必须是已发货才能接单
            throw new BaseException("订单状态异常");
        }
        if (order.getRiderId() != null) {
            throw new BaseException("手慢了，订单已被抢");
        }

        // 乐观锁更新：只绑骑手，不改 status
        int rows = orderMapper.updateRider(order.getId(), riderId, rider.getRealName(), rider.getPhone(),LocalDateTime.now());
        if (rows == 0) {
            throw new BaseException("手慢了，订单已被抢");
        }

        // 骑手状态变配送中（骑手表的状态，不是订单表）
        riderMapper.updateStatus(riderId, 2);
        riderMapper.incrTodayOrders(riderId);
        riderMapper.incrTotalOrders(riderId);

        // 初始化配送轨迹
        deliveryTrackingMapper.insert(DeliveryTracking.builder()
                .orderId(order.getId())
                .riderId(riderId)
                .currentLongitude(order.getShopLongitude())
                .currentLatitude(order.getShopLatitude())
                .locationTime(LocalDateTime.now())
                .createTime(LocalDateTime.now())
                .build());

        log.info("骑手 {} 接单成功, orderNo={}, status保持2", riderId, orderNo);
    }

    /**
     * 开始配送（取货完成，出发前往用户地址）
     * 订单状态 2 → 3
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startDelivery(String orderNo) {
        Long riderId = getCurrentId();

        Orders order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || !riderId.equals(order.getRiderId())) {
            throw new BaseException("订单不存在或无权操作");
        }
        if (order.getStatus() != 2) {  // 必须是已发货才能开始配送
            throw new BaseException("订单状态异常，无法开始配送");
        }

        // 更新订单状态：2→3
        int rows = orderMapper.updateStartDelivery(order.getId(), 3);
        if (rows == 0) {
            throw new BaseException("订单状态已变更，请刷新重试");
        }

        log.info("骑手 {} 开始配送, orderNo={}, status=2→3", riderId, orderNo);
    }

    /**
     * 确认送达
     * 记录送达位置，骑手恢复上班状态
     * 订单状态保持3，等待用户确认收货
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void arriveOrder(String orderNo) {
        Long riderId = getCurrentId();

        Orders order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || !riderId.equals(order.getRiderId())) {
            throw new BaseException("订单不存在或无权操作");
        }
        if (order.getStatus() != 3) {  // 必须是派送中
            throw new BaseException("订单状态异常");
        }

        // 记录送达位置
        deliveryTrackingMapper.insert(DeliveryTracking.builder()
                .orderId(order.getId())
                .riderId(riderId)
                .currentLongitude(order.getBuyerLongitude())
                .currentLatitude(order.getBuyerLatitude())
                .locationTime(LocalDateTime.now())
                .distanceLeft(0)
                .createTime(LocalDateTime.now())
                .build());

        // 骑手恢复上班状态
        riderMapper.updateStatus(riderId, 1);

        log.info("骑手 {} 确认送达, orderNo={}，等待用户确认收货", riderId, orderNo);
    }

    /**
     * 分页查询骑手今日完成的订单
     */
    @Override
    public PageResult<RiderOrderVO> getTodayOrders(Integer pageNum, Integer pageSize) {
        Long riderId = getCurrentId();

        PageHelper.startPage(pageNum, pageSize);
        List<Orders> list = orderMapper.selectByRiderIdAndToday(riderId);
        Page<Orders> page = (Page<Orders>) list;

        List<RiderOrderVO> voList = list.stream()
                .map(this::toRiderOrderVO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), voList);
    }

    // ==================== 私有转换方法 ====================

    /**
     * Rider 转 RiderVO
     */
    private RiderVO toRiderVO(Rider rider) {
        return RiderVO.builder()
                .id(rider.getId())
                .phone(rider.getPhone())
                .realName(rider.getRealName())
                .avatar(rider.getAvatar())
                .status(rider.getStatus())
                .rating(rider.getRating())
                .todayOrders(rider.getTodayOrders())
                .build();
    }

    /**
     * Orders 转 RiderOrderVO
     */
    private RiderOrderVO toRiderOrderVO(Orders order) {
        return RiderOrderVO.builder()
                .orderNo(order.getOrderNo())
                .status(order.getStatus())
                .receiver(order.getReceiver())
                .phone(order.getPhone())
                .addressDetail(order.getAddressDetail())
                .buyerLongitude(order.getBuyerLongitude())
                .buyerLatitude(order.getBuyerLatitude())
                .shopLongitude(order.getShopLongitude())
                .shopLatitude(order.getShopLatitude())
                .payAmount(order.getPayAmount())
                .deliveryTime(order.getDeliveryTime())
                .shopName(order.getShopName())
                .shopAddress("") // 从shop_config查，这里简化
                .build();
    }
}