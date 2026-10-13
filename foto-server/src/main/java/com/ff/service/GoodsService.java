package com.ff.service;

import com.ff.dto.*;
import com.ff.result.PageResult;
import com.ff.vo.GoodsDetailVO;
import com.ff.vo.GoodsPageVO;

/**
 * 商品服务接口
 * 包含商家商品管理和用户商品查询两大功能模块
 */
public interface GoodsService {

    /**
     * 商家新增商品
     * 校验店铺配置是否存在，生成商品信息后入库
     *
     * @param dto 商品新增参数（名称、图片、价格、库存等）
     * @return 新增成功的商品详情
     */
    GoodsDetailVO addGoods(GoodsAddDto dto);

    /**
     * 商家修改商品信息
     * 只能修改自己店铺的商品，防止越权操作
     *
     * @param goodsId 商品ID
     * @param dto     商品修改参数
     * @return 修改后的商品详情
     */
    GoodsDetailVO updateGoods(Long goodsId, GoodsUpdateDto dto);

    /**
     * 商家删除商品（逻辑删除）
     * 将 deleted 字段置为 1，历史订单仍可正常显示商品快照
     *
     * @param goodsId 商品ID
     */
    void deleteGoods(Long goodsId);

    /**
     * 商家上下架商品
     * status: 0下架 1上架，控制用户端是否可见
     *
     * @param goodsId 商品ID
     * @param status  目标状态
     */
    void updateGoodsStatus(Long goodsId, Integer status);

    /**
     * 商家查询自己店铺的商品列表（分页）
     * 用于商家后台管理商品
     *
     * @param dto 查询条件（名称模糊搜索、状态筛选、分页参数）
     * @return 分页商品列表
     */
    PageResult<GoodsPageVO> queryMyGoodsList(GoodsQueryDto dto);

    /**
     * 用户端查询商品列表（分页）
     * 只展示 status=1（上架）且 deleted=0（未删除）的商品
     *
     * @param dto 查询条件（可按店铺筛选、名称搜索、分页）
     * @return 分页商品列表
     */
    PageResult<GoodsPageVO> queryUserGoodsList(GoodsQueryDto dto);

    /**
     * 查询商品详情
     * 用户端和商家端共用，但用户端只能查上架商品
     *
     * @param goodsId 商品ID
     * @return 商品详细信息
     */
    GoodsDetailVO getGoodsDetail(Long goodsId);
}