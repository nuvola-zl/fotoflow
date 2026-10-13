package com.ff.controller;
import com.ff.annotation.RateLimiter;
import com.ff.dto.OrderBuyNowDto;
import com.ff.dto.OrderPayDto;
import com.ff.dto.OrderQueryDto;
import com.ff.result.PageResult;
import com.ff.result.Result;
import com.ff.service.OrderService;
import com.ff.vo.OrderDetailVO;
import com.ff.vo.OrderListVO;
import com.ff.vo.OrderVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

        private final OrderService orderService;

         /**
         * 立即购买（生成预订单，status=0 待付款）
         */
        @PostMapping("/buy-now")
        @RateLimiter(key = "'buy:' + #dto.goodsId", count = 5, window = 1)
        public Result<OrderVO> buyNow(@RequestBody @Valid OrderBuyNowDto dto) {
            return Result.success(orderService.buyNow(dto));
        }

         /**
         * 支付（模拟支付回调，status=0→1，扣库存）
         * 实际项目这里应该是微信/支付宝回调，你先调这个模拟支付流程
         */
        @PostMapping("/pay")
        @RateLimiter(key = "'pay:' + #dto.orderNo", count = 3, window = 1)
        public Result<OrderVO> pay(@RequestBody OrderPayDto dto) {
            return Result.success(orderService.pay(dto));
        }

    // ==================== 订单查询 ====================

    /**
     * 用户查询自己的订单列表
     */
    @GetMapping("/my-list")
    public Result<PageResult<OrderListVO>> myList(OrderQueryDto dto) {
        return Result.success(orderService.queryUserOrderList(dto));
    }

    /**
     * 商家查询自己店铺的订单列表
     */
    @GetMapping("/shop-list")
    public Result<PageResult<OrderListVO>> shopList(OrderQueryDto dto) {
        return Result.success(orderService.queryShopOrderList(dto));
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/{orderNo}")
    public Result<OrderDetailVO> detail(@PathVariable String orderNo) {
        return Result.success(orderService.getOrderDetail(orderNo));
    }

    // ==================== 订单操作 ====================

    /**
     * 商家发货
     */
    @PostMapping("/{orderNo}/delivery/start")
    public Result<Void> deliveryStart(@PathVariable String orderNo) {
        orderService.deliveryStart(orderNo);
        return Result.success();
    }

    /**
     * 用户确认收货
     */
    @PostMapping("/{orderNo}/receive")
    public Result<Void> receiveConfirm(@PathVariable String orderNo) {
        orderService.receiveConfirm(orderNo);
        return Result.success();
    }

    /**
     * 用户取消订单
     * 只能取消待付款的订单，取消后释放库存
     */
    @PostMapping("/{orderNo}/cancel")
    public Result<Void> cancel(@PathVariable String orderNo) {
        orderService.cancelOrder(orderNo);
        return Result.success();
    }
}
