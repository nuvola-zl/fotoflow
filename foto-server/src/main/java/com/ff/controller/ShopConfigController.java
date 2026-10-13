package com.ff.controller;

import com.ff.dto.ShopConfigDto;
import com.ff.result.Result;
import com.ff.service.ShopConfigService;
import com.ff.vo.ShopConfigVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺配置管理控制层
 * 负责店铺的创建、修改、查询接口
 */
@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopConfigController {

    private final ShopConfigService shopConfigService;

    /**
     * 商家创建或更新店铺配置
     * 一个用户只能拥有一个店铺
     * @param dto 店铺配置参数
     * @return 店铺配置信息
     */
    @PostMapping("/config")
    public Result<ShopConfigVO> saveOrUpdate(@RequestBody ShopConfigDto dto) {
        return Result.success(shopConfigService.saveOrUpdate(dto));
    }

    /**
     * 获取当前登录商家自己的店铺配置
     * @return 店铺配置信息
     */
    @GetMapping("/config")
    public Result<ShopConfigVO> getMyShop() {
        return Result.success(shopConfigService.getMyShop());
    }

    /**
     * 根据店铺ID查询店铺信息（供用户/骑手查看）
     * @param shopId 店铺ID
     * @return 店铺配置信息
     */
    @GetMapping("/config/{shopId}")
    public Result<ShopConfigVO> getById(@PathVariable Long shopId) {
        return Result.success(shopConfigService.getById(shopId));
    }
}