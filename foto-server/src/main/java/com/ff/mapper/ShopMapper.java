package com.ff.mapper;

import com.ff.entity.ShopConfig;
import com.ff.vo.ShopConfigShowConfigVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShopMapper {
    /**
     * 查询店铺配置
     * @param userId
     * @return
     */
    @Select("select * from shop_config where user_id = #{userId}")
    List<ShopConfigShowConfigVO> selectShopConfigByUserId(Long userId);

    /**
     * 根据用户ID查询店铺配置
     * @param userId
     * @return
     */
    @Select("select * from shop_config where user_id = #{userId} limit 1")
    ShopConfig selectByUserId(Long userId);

    @Select("select id from shop_config where user_id = #{userId}")
    List<Long> selectShopConfigIdsByUserId(Long userId);
}
