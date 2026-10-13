package com.ff.mapper;

import com.ff.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购物车数据访问层
 * 负责用户购物车条目的增删改查
 */
@Mapper
public interface CartMapper {

    /**
     * 新增购物车条目
     *
     * @param cart 购物车实体（包含用户ID、商品ID、数量、规格等）
     * @return 影响行数
     */
    int insert(Cart cart);

    /**
     * 根据主键查询购物车条目
     *
     * @param id 购物车条目ID
     * @return 购物车实体
     */
    Cart selectById(Long id);

    /**
     * 根据用户ID和商品ID查询购物车条目
     * 用于判断该商品（同规格）是否已在购物车中，决定是否累加数量
     *
     * @param userId   用户ID
     * @param goodsId  商品ID
     * @param goodsSpec 商品规格（如"红色-大码"，可能为null）
     * @return 已存在的购物车条目，不存在则返回null
     */
    Cart selectByUserAndGoods(@Param("userId") Long userId, @Param("goodsId") Long goodsId, @Param("goodsSpec") String goodsSpec);

    /**
     * 更新购物车条目数量
     * 用户修改购买数量时调用
     *
     * @param cart 包含ID、新数量、更新时间
     * @return 影响行数
     */
    int updateQuantity(Cart cart);

    /**
     * 查询当前用户的全部购物车条目
     * 按选中状态降序、创建时间降序排列（选中的在前）
     *
     * @param userId 用户ID
     * @return 购物车条目列表
     */
    List<Cart> selectListByUserId(Long userId);

    /**
     * 根据ID删除购物车条目
     *
     * @param id 购物车条目ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 切换购物车条目的选中状态
     * 1选中 0未选中，用户勾选/取消时调用
     *
     * @param id        购物车条目ID
     * @param isChecked 目标状态：1或0
     * @return 影响行数
     */
    int updateChecked(@Param("id") Long id, @Param("isChecked") Integer isChecked);

    /**
     * 删除用户已选中的全部购物车条目
     * 订单结算成功后调用，清空本次购买的商品，防止重复下单
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteCheckedByUserId(Long userId);
}