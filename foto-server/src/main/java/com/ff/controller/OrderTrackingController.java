package com.ff.controller;

import com.ff.result.Result;
import com.ff.service.RiderLocationService;
import com.ff.vo.RiderLocationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户查看骑手位置（放在用户端路径下）
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderTrackingController {

    private final RiderLocationService riderLocationService;

    /** 查看骑手实时位置 */
    @GetMapping("/{orderNo}/rider-location")
    public Result<RiderLocationVO> riderLocation(@PathVariable String orderNo) {
        return Result.success(riderLocationService.getLatestLocation(orderNo));
    }

    /** 查看配送轨迹（画路线） */
    @GetMapping("/{orderNo}/delivery-path")
    public Result<List<RiderLocationVO>> deliveryPath(@PathVariable String orderNo) {
        return Result.success(riderLocationService.getDeliveryPath(orderNo));
    }
}