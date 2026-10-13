package com.ff.service;

import com.ff.dto.ShopConfigDto;
import com.ff.vo.ShopConfigVO;

/**
 * 店铺配置业务接口
 */
public interface ShopConfigService {

    /**
     * 创建或更新店铺配置
     * @param dto 店铺参数
     * @return 店铺VO
     */
    ShopConfigVO saveOrUpdate(ShopConfigDto dto);

    /**
     * 获取当前用户的店铺信息
     * @return 店铺VO
     */
    ShopConfigVO getMyShop();

    /**
     * 根据店铺ID查询店铺
     * @param shopId 店铺ID
     * @return 店铺VO
     */
    ShopConfigVO getById(Long shopId);
}