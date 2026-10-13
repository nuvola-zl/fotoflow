package com.ff.mapper;

import com.ff.dto.GoodsQueryDto;
import com.ff.entity.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品数据访问层
 */
@Mapper
public interface GoodsMapper {

    /**
     * 插入商品
     */
    int insert(Goods goods);

    /**
     * 根据ID查询商品（包含已删除的，用于商家管理）
     */
    Goods selectById(Long id);

    /**
     * 更新商品信息
     */
    int update(Goods goods);

    /**
     * 逻辑删除商品（将 deleted 置为 1）
     */
    int logicDelete(@Param("id") Long id);

    /**
     * 修改商品上下架状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 商家端条件查询商品列表
     * 查询范围限定为某个店铺（通过 shopConfigId）
     */
    List<Goods> selectByCondition(GoodsQueryDto dto);

    /**
     * 用户端查询商品列表
     * 强制过滤：status=1（上架）且 deleted=0（未删除）
     */
    List<Goods> selectUserGoodsList(GoodsQueryDto dto);

    /**
     * 扣减库存（乐观锁）
     * WHERE stock >= #{quantity} 防止超卖
     */
    int decreaseStock(@Param("goodsId") Long goodsId, @Param("quantity") Integer quantity);

    /**
     * 回滚库存（取消订单时调用）
     * 将商品库存加回去
     *
     * @param goodsId 商品ID
     * @param quantity 回滚数量
     * @return 影响行数
     */
    int increaseStock(@Param("goodsId") Long goodsId, @Param("quantity") Integer quantity);
}