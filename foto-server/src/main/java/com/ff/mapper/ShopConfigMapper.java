package com.ff.mapper;

import com.ff.entity.ShopConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 店铺配置数据访问层
 */
@Mapper
public interface ShopConfigMapper {

    /**
     * 新增店铺配置
     * @param shopConfig 店铺实体
     * @return 影响行数
     */
    int insert(ShopConfig shopConfig);

    /**
     * 更新店铺配置
     * @param shopConfig 店铺实体
     * @return 影响行数
     */
    int update(ShopConfig shopConfig);

    /**
     * 根据ID查询店铺
     * @param id 店铺ID
     * @return 店铺实体
     */
    ShopConfig selectById(Long id);

    /**
     * 根据用户ID查询店铺
     * @param userId 用户ID
     * @return 店铺实体
     */
    ShopConfig selectByUserId(Long userId);
}