package com.ff.controller;

import com.ff.dto.RiderLoginDto;
import com.ff.dto.RiderLocationDto;
import com.ff.dto.RiderStatusDto;
import com.ff.result.PageResult;
import com.ff.result.Result;
import com.ff.service.RiderLocationService;
import com.ff.service.RiderService;
import com.ff.vo.RiderLocationVO;
import com.ff.vo.RiderOrderVO;
import com.ff.vo.RiderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 骑手端接口控制层
 * 提供骑手状态、接单、配送、订单、位置上报等接口
 */
@RestController
@RequestMapping("/rider")
@RequiredArgsConstructor
public class RiderController {

    private final RiderService riderService;
    private final RiderLocationService riderLocationService;

    /**
     * 切换骑手上班/休息状态
     */
    @PutMapping("/status")
    public Result<Void> status(@RequestBody RiderStatusDto dto) {
        riderService.updateStatus(dto);
        return Result.success();
    }

    /**
     * 获取当前骑手个人信息
     */
    @GetMapping("/info")
    public Result<RiderVO> info() {
        return Result.success(riderService.getRiderInfo());
    }

    /**
     * 获取待接单订单池
     */
    @GetMapping("/orders/pending")
    public Result<List<RiderOrderVO>> pendingOrders() {
        return Result.success(riderService.getPendingOrders());
    }

    /**
     * 骑手抢单/接单
     */
    @PostMapping("/orders/{orderNo}/take")
    public Result<Void> takeOrder(@PathVariable String orderNo) {
        riderService.takeOrder(orderNo);
        return Result.success();
    }

    /**
     * 开始配送（取货完成）
     */
    @PostMapping("/orders/{orderNo}/start")
    public Result<Void> startDelivery(@PathVariable String orderNo) {
        riderService.startDelivery(orderNo);
        return Result.success();
    }

    /**
     * 确认送达
     */
    @PostMapping("/orders/{orderNo}/arrive")
    public Result<Void> arriveOrder(@PathVariable String orderNo) {
        riderService.arriveOrder(orderNo);
        return Result.success();
    }

    /**
     * 分页查询今日订单
     */
    @GetMapping("/orders/today")
    public Result<PageResult<RiderOrderVO>> todayOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(riderService.getTodayOrders(pageNum, pageSize));
    }

    /**
     * 骑手实时上报位置
     */
    @PostMapping("/location")
    public Result<Void> uploadLocation(@RequestBody RiderLocationDto dto) {
        riderLocationService.uploadLocation(dto);
        return Result.success();
    }
}