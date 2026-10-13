
    package com.ff.controller;

    import com.ff.dto.GoodsAddDto;
    import com.ff.dto.GoodsQueryDto;
    import com.ff.dto.GoodsUpdateDto;
    import com.ff.result.PageResult;
    import com.ff.result.Result;
    import com.ff.service.GoodsService;
    import com.ff.vo.GoodsDetailVO;
    import com.ff.vo.GoodsPageVO;

    import jakarta.validation.Valid;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.*;

    /**
     * 商品控制器
     * 提供商家商品管理和用户商品查询接口
     */
    @RestController
    @RequestMapping("/goods")
    public class GoodsController {

        @Autowired
        private GoodsService goodsService;

        // ==================== 商家端接口 ====================

        /**
         * 商家新增商品
         * 商家登录后调用，自动关联到当前商家的店铺
         */
        @PostMapping
        public Result<GoodsDetailVO> add(@RequestBody @Valid GoodsAddDto dto) {
            return Result.success(goodsService.addGoods(dto));
        }

        /**
         * 商家修改商品
         * 只能修改自己店铺的商品
         */
        @PutMapping("/{goodsId}")
        public Result<GoodsDetailVO> update(@PathVariable Long goodsId, @RequestBody GoodsUpdateDto dto) {
            return Result.success(goodsService.updateGoods(goodsId, dto));
        }

        /**
         * 商家删除商品（逻辑删除）
         */
        @DeleteMapping("/{goodsId}")
        public Result<Void> delete(@PathVariable Long goodsId) {
            goodsService.deleteGoods(goodsId);
            return Result.success();
        }

        /**
         * 商家上下架商品
         * status: 0下架 1上架
         */
        @PutMapping("/{goodsId}/status/{status}")
        public Result<Void> updateStatus(@PathVariable Long goodsId, @PathVariable Integer status) {
            goodsService.updateGoodsStatus(goodsId, status);
            return Result.success();
        }

        /**
         * 商家查询自己店铺的商品列表（分页）
         * 用于商家后台管理商品
         */
        @GetMapping("/my-list")
        public Result<PageResult<GoodsPageVO>> myList(GoodsQueryDto dto) {
            return Result.success(goodsService.queryMyGoodsList(dto));
        }

        // ==================== 用户端接口 ====================

        /**
         * 用户查询商品列表（分页）
         * 只展示上架且未删除的商品
         * 支持按店铺筛选、按名称模糊搜索
         */
        @GetMapping("/list")
        public Result<PageResult<GoodsPageVO>> list(GoodsQueryDto dto) {
            return Result.success(goodsService.queryUserGoodsList(dto));
        }

        /**
         * 查询商品详情
         * 用户端和商家端共用
         */
        @GetMapping("/{goodsId}")
        public Result<GoodsDetailVO> detail(@PathVariable Long goodsId) {
            return Result.success(goodsService.getGoodsDetail(goodsId));
        }
    }