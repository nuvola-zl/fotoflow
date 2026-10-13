package com.ff.controller;
import com.ff.dto.CartAddDto;
import com.ff.result.Result;
import com.ff.service.CartService;
import com.ff.vo.CartVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 加入购物车
     */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody CartAddDto dto) {
        cartService.addToCart(dto);
        return Result.success();
    }

    /**
     * 查询购物车列表
     */
    @GetMapping("/list")
    public Result<List<CartVO>> list() {
        return Result.success(cartService.listCart());
    }

    /**
     * 修改数量
     */
    @PutMapping("/{cartId}/quantity/{quantity}")
    public Result<Void> updateQuantity(@PathVariable Long cartId, @PathVariable Integer quantity) {
        cartService.updateQuantity(cartId, quantity);
        return Result.success();
    }

    /**
     * 删除条目
     */
    @DeleteMapping("/{cartId}")
    public Result<Void> delete(@PathVariable Long cartId) {
        cartService.deleteCartItem(cartId);
        return Result.success();
    }

    /**
     * 切换选中状态
     */
    @PutMapping("/{cartId}/checked")
    public Result<Void> toggleChecked(@PathVariable Long cartId) {
        cartService.toggleChecked(cartId);
        return Result.success();
    }
}