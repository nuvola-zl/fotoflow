package com.ff.controller;

import com.ff.constant.MessageConstant;
import com.ff.dto.*;
import com.ff.result.PageResult;
import com.ff.result.Result;
import com.ff.service.GoodsService;
import com.ff.vo.MyOrdersVO;
import com.ff.vo.OrderItemVO;
import com.ff.vo.ShopConfigShowConfigVO;
import com.ff.vo.UserAddressVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/goods/random")
    public Result<PageResult> random(GoodsPageDto goodsPageDto) {
        return Result.success(goodsService.selectGoodsRandom(goodsPageDto));
    }

    @PostMapping("/addGoods")
    public Result<String> addGoods(@Valid @RequestBody GoodsAddDto goodsAddDto) {
        goodsService.addGoods(goodsAddDto);
        return Result.success(MessageConstant.GOODS_ADD_SUCCESS);
    }

    @PostMapping("/updateGoods")
    public Result<String> updateGoods(@RequestBody GoodsUpdateDto goodsUpdateDto) {
        goodsService.updateGoods(goodsUpdateDto);
        return Result.success(MessageConstant.GOODS_UPDATE_SUCCESS);
    }

    @GetMapping("/show")
    public Result<List<ShopConfigShowConfigVO>> showShopsConfig() {
        return Result.success(goodsService.showShopsConfig());
    }

    @PostMapping("/update/shop")
    public Result<String> updateShopConfig(@RequestBody ShopConfigDto shopConfigDto) {
        goodsService.updateShopConfig(shopConfigDto);
        return Result.success(MessageConstant.SHOP_CONFIG_UPDATE_SUCCESS);
    }

    @PostMapping("/shop")
    public Result<Long> addShopConfig(@RequestBody ShopConfigDto shopConfigDto) {
        return Result.success(goodsService.addShopConfig(shopConfigDto));
    }

    @GetMapping("selectShop/{id}")
    public Result<Long> selectShop(@PathVariable Long id) {
        return Result.success(id);
    }

    @GetMapping("/orderAll")
    public Result<PageResult> orderAll(OrdersPageDto ordersPageDto) {
        return Result.success(goodsService.orderAll(ordersPageDto));
    }

    @PostMapping("/acceptOrder/{id}")
    public Result<String> acceptOrder(@PathVariable Long id) {
        goodsService.acceptOrder(id);
        return Result.success(MessageConstant.ORDER_ACCEPT_SUCCESS);
    }

    @PostMapping("/cancelOrder/{id}")
    public Result<String> cancelOrder(@PathVariable Long id) {
        goodsService.cancelOrder(id);
        return Result.success(MessageConstant.ORDER_CANCEL_SUCCESS);
    }

    @PostMapping("/completeOrder/{id}")
    public Result<String> completeOrder(@PathVariable Long id) {
        goodsService.completeOrder(id);
        return Result.success(MessageConstant.ORDER_COMPLETE_SUCCESS);
    }

    @PostMapping("/deleteOrder/{id}")
    public Result<String> deleteOrder(@PathVariable Long id) {
        goodsService.deleteOrder(id);
        return Result.success(MessageConstant.ORDER_DELETE_SUCCESS);
    }

    @PostMapping("/updateDelivery/{id}")
    public Result<String> updateDelivery(@PathVariable Long id, @RequestBody DeliveryLocationDto dto) {
        goodsService.updateDeliveryLocation(id, dto);
        return Result.success(MessageConstant.DELIVERY_UPDATE_SUCCESS);
    }

    @GetMapping("/MyOrder")
    public Result<PageResult> MyOrder(OrdersPageDto ordersPageDto) {
        return Result.success(goodsService.MyOrder(ordersPageDto));
    }

    @GetMapping("/MyOrder/{id}")
    public Result<MyOrdersVO> myOrderDetail(@PathVariable Long id) {
        return Result.success(goodsService.myOrderDetail(id));
    }

    @PostMapping("/confirmOrder/{id}")
    public Result<String> confirmOrder(@PathVariable Long id) {
        goodsService.confirmOrder(id);
        return Result.success(MessageConstant.ORDER_CONFIRM_SUCCESS);
    }

    @PostMapping("/cancelMylOrder/{id}")
    public Result<String> cancelMylOrder(@PathVariable Long id) {
        goodsService.cancelMylOrder(id);
        return Result.success(MessageConstant.ORDER_CANCEL_SUCCESS);
    }

    @GetMapping("/MyAddress")
    public Result<List<UserAddressVO>> MyAddress() {
        return Result.success(goodsService.MyAddress());
    }

    @PostMapping("/MyAddress")
    public Result<String> MyAddressAdd(@RequestBody UserAddressDto userAddressDto) {
        goodsService.MyAddressAdd(userAddressDto);
        return Result.success(MessageConstant.USER_ADDRESS_ADD_SUCCESS);
    }

    @DeleteMapping("/MyAddress/{id}")
    public Result<String> MyAddressDelete(@PathVariable Long id) {
        goodsService.MyAddressDelete(id);
        return Result.success(MessageConstant.USER_ADDRESS_DELETE_SUCCESS);
    }

    @GetMapping("/MyAddress/{id}")
    public Result<UserAddressVO> MyAddressShow(@PathVariable Long id) {
        return Result.success(goodsService.MyAddressShow(id));
    }

    @PostMapping("/MyAddress/{id}")
    public Result<String> MyAddressUpdate(@PathVariable Long id, @RequestBody UserAddressDto userAddressDto) {
        goodsService.MyAddressUpdate(id, userAddressDto);
        return Result.success(MessageConstant.USER_ADDRESS_UPDATE_SUCCESS);
    }

    /**
     * 购买商品预览；addressId 可选，传入则导入该收货地址，否则尝试默认地址
     */
    @GetMapping("/addMyGoods/{id}")
    public Result<OrderItemVO> addMyGoods(@PathVariable Long id,
                                          @RequestParam(required = false) Long addressId) {
        return Result.success(goodsService.addMyGoods(id, addressId));
    }

    @PostMapping("/order")
    public Result<OrderItemVO> SetOrder(@RequestBody OrderItemDto orderItemDto) {
        return Result.success(goodsService.SetOrder(orderItemDto));
    }
}
