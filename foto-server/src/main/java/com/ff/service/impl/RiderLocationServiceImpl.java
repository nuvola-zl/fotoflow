package com.ff.service.impl;

import com.ff.context.BaseContext;
import com.ff.dto.RiderLocationDto;
import com.ff.entity.DeliveryTracking;
import com.ff.entity.Orders;
import com.ff.entity.RiderLocationSummary;
import com.ff.entity.ShopConfig;
import com.ff.exception.BaseException;
import com.ff.exception.CodeException;
import com.ff.mapper.DeliveryTrackingMapper;
import com.ff.mapper.OrderMapper;
import com.ff.mapper.RiderLocationSummaryMapper;
import com.ff.mapper.ShopConfigMapper;
import com.ff.service.RiderLocationService;
import com.ff.vo.RiderLocationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 骑手位置服务实现
 * 性能优化：汇总表缓存最新位置，1小时级刷新
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RiderLocationServiceImpl implements RiderLocationService {

    private final DeliveryTrackingMapper trackingMapper;
    private final OrderMapper orderMapper;
    private final ShopConfigMapper shopConfigMapper;
    private final RiderLocationSummaryMapper summaryMapper;


    /**
     * 上报实时位置
     * 1. 保存历史轨迹（不变）
     * 2. 更新汇总表（覆盖最新位置，供用户查询）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadLocation(RiderLocationDto dto) {
        Long riderId = BaseContext.getCurrentId();

        Orders order = orderMapper.selectByOrderNo(dto.getOrderNo());
        if (order == null) {
            throw new BaseException(CodeException.NOT_FOUND,"订单不存在");
        }
        if (!riderId.equals(order.getRiderId())) {
            throw new BaseException(CodeException.UNAUTHORIZED,"无权操作该订单");
        }
        if (order.getStatus() != 3) {
            throw new BaseException(CodeException.NOT_STARTED,"订单不在配送中");
        }

        // 1. 保存历史轨迹
        Integer distanceLeft = calculateDistance(
                dto.getLongitude(), dto.getLatitude(),
                order.getBuyerLongitude(), order.getBuyerLatitude()
        );

        DeliveryTracking tracking = new DeliveryTracking();
        tracking.setOrderId(order.getId());
        tracking.setRiderId(riderId);
        tracking.setCurrentLongitude(dto.getLongitude());
        tracking.setCurrentLatitude(dto.getLatitude());
        tracking.setLocationTime(LocalDateTime.now());
        tracking.setDistanceLeft(distanceLeft);
        tracking.setCreateTime(LocalDateTime.now());
        trackingMapper.insert(tracking);



        // 2. 更新汇总表（性能优化，用户查这个表）
        Integer eta = calculateEta(dto.getLongitude(), dto.getLatitude(),
                order.getBuyerLongitude(), order.getBuyerLatitude());

        summaryMapper.saveOrUpdate(RiderLocationSummary.builder()
                .orderId(order.getId())
                .riderId(riderId)
                .currentLng(dto.getLongitude())
                .currentLat(dto.getLatitude())
                .locationTime(LocalDateTime.now())
                .etaMinutes(eta)
                .updateTime(LocalDateTime.now())
                .build());

        log.debug("骑手 {} 位置上报并汇总, orderNo={}, eta={}分钟",
                riderId, dto.getOrderNo(), eta);
    }

    /**
     * 用户查看骑手最新位置
     * 直接查汇总表，O(1)性能
     */
    @Override
    public RiderLocationVO getLatestLocation(String orderNo) {
        Orders order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || order.getDeleted() == 1) {
            throw new BaseException(CodeException.NOT_FOUND,"订单不存在");
        }

        Long userId = BaseContext.getCurrentId();
        boolean isBuyer = order.getUserId().equals(userId);
        ShopConfig shop = shopConfigMapper.selectById(order.getShopId());
        boolean isSeller = (shop != null && shop.getUserId().equals(userId));
        if (!isBuyer && !isSeller) {
            throw new BaseException(CodeException.UNAUTHORIZED,"无权查看");
        }

        if (order.getRiderId() == null || order.getStatus() < 2 || order.getStatus() > 3) {
            return null;
        }

        // 查汇总表（主键命中，性能极高）
        RiderLocationSummary summary = summaryMapper.selectByOrderId(order.getId());
        if (summary == null) {
            return null;
        }

        return RiderLocationVO.builder()
                .longitude(summary.getCurrentLng())
                .latitude(summary.getCurrentLat())
                .locationTime(summary.getLocationTime())
                .estimatedMinutes(summary.getEtaMinutes())
                .build();
    }

    /**
     * 查询配送完整轨迹（画路线）
     */
    @Override
    public List<RiderLocationVO> getDeliveryPath(String orderNo) {
        Orders order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || order.getDeleted() == 1) {
            throw new BaseException(CodeException.NOT_FOUND,"订单不存在");
        }

        Long userId = BaseContext.getCurrentId();
        boolean isBuyer = order.getUserId().equals(userId);
        ShopConfig shop = shopConfigMapper.selectById(order.getShopId());
        boolean isSeller = (shop != null && shop.getUserId().equals(userId));
        if (!isBuyer && !isSeller) {
            throw new BaseException(CodeException.UNAUTHORIZED,"无权查看");
        }

        List<DeliveryTracking> list = trackingMapper.selectByOrderId(order.getId());
        return list.stream().map(t -> RiderLocationVO.builder()
                .longitude(t.getCurrentLongitude())
                .latitude(t.getCurrentLatitude())
                .locationTime(t.getLocationTime())
                .build()).collect(Collectors.toList());
    }

    /**
     * 简化ETA：直线距离 / 速度 + 缓冲
     */
    private Integer calculateEta(Double riderLng, Double riderLat,
                                 Double buyerLng, Double buyerLat) {
        int distance = calculateDistance(riderLng, riderLat, buyerLng, buyerLat);
        // 20km/h = 333米/分钟，加5分钟缓冲
        return distance / 333 + 5;
    }

    /**
     * 简化距离计算（直线距离，单位：米）
     */
    private Integer calculateDistance(Double lng1, Double lat1, Double lng2, Double lat2) {
        double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (int) (R * c);
    }
}