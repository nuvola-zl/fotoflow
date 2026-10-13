package com.ff.service.impl;

import com.ff.context.BaseContext;
import com.ff.dto.GoodsAddDto;
import com.ff.dto.GoodsQueryDto;
import com.ff.dto.GoodsUpdateDto;
import com.ff.entity.Goods;
import com.ff.entity.ShopConfig;
import com.ff.exception.BaseException;
import com.ff.exception.CodeException;
import com.ff.mapper.GoodsMapper;
import com.ff.mapper.ShopConfigMapper;
import com.ff.result.PageResult;
import com.ff.service.GoodsService;
import com.ff.utils.RedisUtil;
import com.ff.vo.GoodsDetailVO;
import com.ff.vo.GoodsPageVO;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品服务实现类
 * 负责商品全生命周期管理：创建、修改、删除、上下架、查询
 */
@Service
@RequiredArgsConstructor
public class GoodsServiceImpl implements GoodsService {

    private final GoodsMapper goodsMapper;

    private final ShopConfigMapper shopConfigMapper;

    private final RedisUtil redisUtil;

    /** 商品详情缓存时间：30 分钟 */
    private static final long GOODS_CACHE_MINUTES = 30;

    /**
     * 商家新增商品
     * 流程：
     * 1. 获取当前登录商家ID
     * 2. 校验商家是否已配置店铺（通过 userId 查 shop_config）
     * 3. 构建商品实体，设置默认状态（上架、未删除）
     * 4. 写入数据库
     * 5. 返回商品详情VO
     */
    @Override
    public GoodsDetailVO addGoods(GoodsAddDto dto) {
        Long userId = BaseContext.getCurrentId();
        ShopConfig shop = shopConfigMapper.selectByUserId(userId);
        if (shop == null) {
            throw new BaseException(CodeException.NOT_COMPLETED, "请先配置店铺信息");
        }

        Goods goods = new Goods();
        BeanUtils.copyProperties(dto, goods);
        goods.setGoodsConfigId(shop.getId());
        goods.setStatus(1);
        goods.setDeleted(0);
        goods.setSales(0);
        goods.setCreateTime(LocalDateTime.now());
        goods.setUpdateTime(LocalDateTime.now());

        goodsMapper.insert(goods);

        // 新增商品直接写入缓存（预热），避免首次查询击穿数据库
        GoodsDetailVO vo = toDetailVO(goods);
        redisUtil.setGoods(goods.getId(), vo, GOODS_CACHE_MINUTES);

        return vo;
    }

    /**
     * 商家修改商品信息
     * 安全校验：
     * 1. 商品必须存在且未删除
     * 2. 商品必须属于当前登录商家（通过 goodsConfigId 关联到 shop_config 的 userId）
     * 防止商家A修改商家B的商品
     */
    @Override
    public GoodsDetailVO updateGoods(Long goodsId, GoodsUpdateDto dto) {
        Long userId = BaseContext.getCurrentId();

        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null || goods.getDeleted() == 1) {
            throw new BaseException(CodeException.NOT_FOUND, "商品不存在");
        }

        ShopConfig shop = shopConfigMapper.selectById(goods.getGoodsConfigId());
        if (shop == null || !shop.getUserId().equals(userId)) {
            throw new BaseException(CodeException.FORBIDDEN, "无权操作该商品");
        }

        BeanUtils.copyProperties(dto, goods);
        goods.setId(goodsId);
        goods.setUpdateTime(LocalDateTime.now());

        // 1. 先更新数据库（保证数据库一定成功）
        goodsMapper.update(goods);

        // 2. 再删除缓存（下次查询自动从数据库加载并回写缓存）
        redisUtil.deleteGoods(goodsId);

        // 3. 返回最新数据（直接查库，不走缓存，避免读到旧值）
        Goods fresh = goodsMapper.selectById(goodsId);
        return toDetailVO(fresh);
    }


    /**
     * 商家删除商品（逻辑删除）
     * 将 deleted 置为 1，而非物理删除
     * 原因：历史订单中存有商品快照（goodsName/goodsImg/price），
     *      物理删除会导致订单详情无法关联商品信息
     * 安全校验同修改：只能删除自己的商品
     */
    @Override
    public void deleteGoods(Long goodsId) {
        Long userId = BaseContext.getCurrentId();

        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null || goods.getDeleted() == 1) {
            throw new BaseException(CodeException.NOT_FOUND, "商品不存在");
        }

        ShopConfig shop = shopConfigMapper.selectById(goods.getGoodsConfigId());
        if (shop == null || !shop.getUserId().equals(userId)) {
            throw new BaseException(CodeException.FORBIDDEN, "无权操作该商品");
        }

        goodsMapper.logicDelete(goodsId);

        // 删除缓存
        redisUtil.deleteGoods(goodsId);
    }


    /**
     * 商家上下架商品
     * status: 0下架（用户端不可见） 1上架（用户端可见）
     * 安全校验同修改
     */
    @Override
    public void updateGoodsStatus(Long goodsId, Integer status) {
        if (status != 0 && status != 1) {
            throw new BaseException(CodeException.PARAM_ERROR, "状态参数错误");
        }

        Long userId = BaseContext.getCurrentId();

        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null || goods.getDeleted() == 1) {
            throw new BaseException(CodeException.NOT_FOUND, "商品不存在");
        }

        ShopConfig shop = shopConfigMapper.selectById(goods.getGoodsConfigId());
        if (shop == null || !shop.getUserId().equals(userId)) {
            throw new BaseException(CodeException.FORBIDDEN, "无权操作该商品");
        }

        goodsMapper.updateStatus(goodsId, status);

        // 状态变更后清缓存
        redisUtil.deleteGoods(goodsId);
    }

    /**
     * 商家查询自己店铺的商品列表（分页）
     * 用于商家后台管理，可查看全部商品（包括下架的）
     * 支持按商品名称模糊搜索、按状态筛选
     */
    @Override
    public PageResult<GoodsPageVO> queryMyGoodsList(GoodsQueryDto dto) {
        Long userId = BaseContext.getCurrentId();
        ShopConfig shop = shopConfigMapper.selectByUserId(userId);
        if (shop == null) {
            return new PageResult<>();
        }

        dto.setShopConfigId(shop.getId());
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());

        List<Goods> list = goodsMapper.selectByCondition(dto);
        Page<Goods> page = (Page<Goods>) list;

        List<GoodsPageVO> voList = list.stream()
                .map(this::toPageVO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), voList);
    }

    /**
     * 用户端查询商品列表（分页）
     * 过滤条件：
     * 1. status = 1（只展示上架商品）
     * 2. deleted = 0（只展示未删除商品）
     * 3. stock > 0（可选，根据业务决定是否只展示有库存的）
     * 支持按店铺筛选、按名称搜索
     */
    /**
     * 用户端商品列表（分页）
     * 列表页数据变化快，一般不加缓存；如果加，过期时间设短（1-2分钟）
     */
    @Override
    public PageResult<GoodsPageVO> queryUserGoodsList(GoodsQueryDto dto) {
        dto.setStatus(1);
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<Goods> list = goodsMapper.selectUserGoodsList(dto);
        Page<Goods> page = (Page<Goods>) list;

        List<GoodsPageVO> voList = list.stream()
                .map(this::toPageVO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), voList);
    }

    /**
     * 查询商品详情（核心缓存逻辑）
     * 流程：
     * 1. 先读 Redis 缓存
     * 2. 缓存命中直接返回
     * 3. 缓存未命中，查数据库
     * 4. 数据库结果写入 Redis，再返回
     */
    @Override
    public GoodsDetailVO getGoodsDetail(Long goodsId) {
        // 1. 查缓存
        Object cache = redisUtil.getGoods(goodsId);
        if (cache != null) {
            return (GoodsDetailVO) cache;
        }

        // 2. 缓存未命中，查数据库
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null || goods.getDeleted() == 1) {
            throw new BaseException(CodeException.NOT_FOUND, "商品不存在");
        }

        // 3. 组装 VO
        GoodsDetailVO vo = toDetailVO(goods);

        // 4. 写入缓存（带过期时间，防止冷数据长期占用内存）
        redisUtil.setGoods(goodsId, vo, GOODS_CACHE_MINUTES);

        return vo;
    }

    // ==================== 私有转换方法 ====================

    /**
     * 将 Goods 实体转换为详情 VO
     * 包含完整商品信息，用于商品详情页展示
     */
    private GoodsDetailVO toDetailVO(Goods goods) {
        return GoodsDetailVO.builder()
                .id(goods.getId())
                .goodsName(goods.getGoodsName())
                .goodsImg(goods.getGoodsImg())
                .content(goods.getContent())
                .price(goods.getPrice())
                .unit(goods.getUnit())
                .stock(goods.getStock())
                .sales(goods.getSales())
                .status(goods.getStatus())
                .goodsConfigId(goods.getGoodsConfigId())
                .createTime(goods.getCreateTime())
                .build();
    }

    /**
     * 将 Goods 实体转换为列表页 VO
     * 精简字段，减少列表页数据传输量
     */
    private GoodsPageVO toPageVO(Goods goods) {
        return GoodsPageVO.builder()
                .id(goods.getId())
                .goodsName(goods.getGoodsName())
                .goodsImg(goods.getGoodsImg())
                .price(goods.getPrice())
                .unit(goods.getUnit())
                .stock(goods.getStock())
                .sales(goods.getSales())
                .status(goods.getStatus())
                .build();
    }
}