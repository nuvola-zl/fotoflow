package com.ff.mapper;

import com.ff.dto.GoodsPageDto;
import com.ff.dto.GoodsUpdateDto;
import com.ff.entity.Goods;
import com.ff.entity.ShopConfig;
import org.apache.ibatis.annotations.Mapper;
import com.github.pagehelper.Page;
import org.springframework.data.repository.query.Param;

@Mapper
public interface GoodsMapper {
    /**
     * 关联视频id查询所有的商品
     * @param dto
     * @return
     */
    Page<Goods> selectGoodsRandom(
            @Param("dto") GoodsPageDto dto
    );

    /**
     * 根据ID修改店铺配置
     * @param shopConfig 店铺配置实体
     */
    void updateShopConfig(ShopConfig shopConfig);

    /**
     * 新增商品
     * @param goods 商品实体
     */
    void insert(Goods goods);

    /**
     * 根据ID查询商品
     * @param id 商品ID
     * @return 商品实体
     */
    Goods selectByPrimaryKey(Long id);

    /**
     * 根据商品配置ID查询店铺配置
     * @param goodsConfigId 商品配置ID
     * @return 店铺配置实体
     */
    ShopConfig selectShopConfig(Long goodsConfigId);

//=----------------------
    /**
     * 新增店铺配置
     * @param shopConfig 店铺配置实体
     */
    void insertShopConfig(ShopConfig shopConfig);

    void updateGoods(GoodsUpdateDto goodsUpdateDto);

    int decreaseStock(@Param("id") Long id, @Param("count") Integer count);

}
