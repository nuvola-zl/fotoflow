package com.ff.service.impl;

import com.ff.context.BaseContext;
import com.ff.dto.CartAddDto;
import com.ff.entity.Cart;
import com.ff.entity.Goods;
import com.ff.mapper.CartMapper;
import com.ff.mapper.GoodsMapper;
import com.ff.service.CartService;
import com.ff.vo.CartVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车服务实现
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;

    private final GoodsMapper goodsMapper;

    /**
     * 加入购物车
     * 流程：
     * 1. 校验商品是否存在、上架、库存
     * 2. 查购物车是否已有该商品（同规格）
     * 3. 有则累加数量，无则新增
     */
    @Override
    public void addToCart(CartAddDto dto) {
        Long userId = BaseContext.getCurrentId();

        // 校验商品
        Goods goods = goodsMapper.selectById(dto.getGoodsId());
        if (goods == null || goods.getDeleted() == 1) {
            throw new RuntimeException("商品不存在");
        }
        if (goods.getStatus() != 1) {
            throw new RuntimeException("商品已下架");
        }

        int quantity = (dto.getQuantity() == null || dto.getQuantity() <= 0) ? 1 : dto.getQuantity();
        if (goods.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }

        // 查是否已在购物车（同商品同规格）
        Cart exist = cartMapper.selectByUserAndGoods(userId, dto.getGoodsId(), dto.getGoodsSpec());
        if (exist != null) {
            // 累加数量
            int newQty = exist.getQuantity() + quantity;
            if (goods.getStock() < newQty) {
                throw new RuntimeException("库存不足，购物车已有" + exist.getQuantity() + "件");
            }
            exist.setQuantity(newQty);
            exist.setUpdateTime(LocalDateTime.now());
            cartMapper.updateQuantity(exist);
        } else {
            // 新增
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setGoodsId(dto.getGoodsId());
            cart.setGoodsSpec(dto.getGoodsSpec());
            cart.setUnit(goods.getUnit());
            cart.setQuantity(quantity);
            cart.setIsChecked(1);
            cart.setCreateTime(LocalDateTime.now());
            cart.setUpdateTime(LocalDateTime.now());
            cartMapper.insert(cart);
        }
    }

    /**
     * 查询购物车列表
     * 关联商品表查实时信息，如果商品已删除/下架，标记 available=0
     */
    @Override
    public List<CartVO> listCart() {
        Long userId = BaseContext.getCurrentId();
        List<Cart> cartList = cartMapper.selectListByUserId(userId);

        return cartList.stream().map(cart -> {
            Goods goods = goodsMapper.selectById(cart.getGoodsId());

            int available = 1;
            String goodsName = "";
            String goodsImg = "";
            Integer price = 0;
            Integer stock = 0;

            if (goods == null || goods.getDeleted() == 1 || goods.getStatus() != 1) {
                available = 0; // 商品失效
            } else {
                goodsName = goods.getGoodsName();
                goodsImg = goods.getGoodsImg();
                price = goods.getPrice();
                stock = goods.getStock();
            }

            return CartVO.builder()
                    .cartId(cart.getId())
                    .goodsId(cart.getGoodsId())
                    .goodsName(goodsName)
                    .goodsImg(goodsImg)
                    .price(price)
                    .unit(cart.getUnit())
                    .stock(stock)
                    .quantity(cart.getQuantity())
                    .isChecked(cart.getIsChecked())
                    .subtotal(price * cart.getQuantity())
                    .available(available)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 修改数量
     * 校验归属权和库存
     */
    @Override
    public void updateQuantity(Long cartId, Integer quantity) {
        Long userId = BaseContext.getCurrentId();

        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("数量必须大于0");
        }

        Cart cart = cartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new RuntimeException("购物车条目不存在");
        }

        Goods goods = goodsMapper.selectById(cart.getGoodsId());
        if (goods == null || goods.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }

        cart.setQuantity(quantity);
        cart.setUpdateTime(LocalDateTime.now());
        cartMapper.updateQuantity(cart);
    }

    /**
     * 删除条目
     */
    @Override
    public void deleteCartItem(Long cartId) {
        Long userId = BaseContext.getCurrentId();

        Cart cart = cartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new RuntimeException("购物车条目不存在");
        }

        cartMapper.deleteById(cartId);
    }

    /**
     * 切换选中状态
     */
    @Override
    public void toggleChecked(Long cartId) {
        Long userId = BaseContext.getCurrentId();

        Cart cart = cartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new RuntimeException("购物车条目不存在");
        }

        int newChecked = cart.getIsChecked() == 1 ? 0 : 1;
        cartMapper.updateChecked(cartId, newChecked);
    }

    /**
     * 清空已选中的条目
     * 订单结算成功后调用
     */
    @Override
    public void clearCheckedCart() {
        Long userId = BaseContext.getCurrentId();
        cartMapper.deleteCheckedByUserId(userId);
    }
}