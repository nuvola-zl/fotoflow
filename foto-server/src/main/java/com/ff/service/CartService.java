package com.ff.service;

import com.ff.dto.CartAddDto;
import com.ff.vo.CartVO;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {

    /**
     * 加入购物车
     * 如果商品已在购物车，累加数量；否则新增条目
     *
     * @param dto 商品ID + 数量 + 规格
     */
    void addToCart(CartAddDto dto);

    /**
     * 查询当前用户的购物车列表
     * 按选中状态排序（选中的在前），并计算每条的小计
     *
     * @return 购物车条目列表
     */
    List<CartVO> listCart();

    /**
     * 修改购物车商品数量
     * 校验库存是否充足
     *
     * @param cartId  购物车条目ID
     * @param quantity 新数量（必须>0）
     */
    void updateQuantity(Long cartId, Integer quantity);

    /**
     * 删除购物车条目
     *
     * @param cartId 购物车条目ID
     */
    void deleteCartItem(Long cartId);

    /**
     * 切换购物车条目的选中状态
     * 1↔0
     *
     * @param cartId 购物车条目ID
     */
    void toggleChecked(Long cartId);

    /**
     * 清空当前用户已选中的购物车条目
     * 在订单结算成功后调用，防止重复购买
     */
    void clearCheckedCart();
}