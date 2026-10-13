package com.ff.service.impl;

import com.ff.context.BaseContext;
import com.ff.dto.ShopConfigDto;
import com.ff.entity.ShopConfig;
import com.ff.exception.BaseException;
import com.ff.exception.CodeException;
import com.ff.mapper.ShopConfigMapper;
import com.ff.service.ShopConfigService;
import com.ff.vo.ShopConfigVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 店铺配置业务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShopConfigServiceImpl implements ShopConfigService {

    private final ShopConfigMapper shopConfigMapper;

    /**
     * 创建或更新店铺配置（一个用户只能开一个店）
     * 存在则更新，不存在则新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShopConfigVO saveOrUpdate(ShopConfigDto dto) {
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            throw new BaseException(CodeException.UNAUTHORIZED, "用户未登录");
        }

        ShopConfig exist = shopConfigMapper.selectByUserId(userId);

        if (exist == null) {
            // ========== 新建店铺 ==========
            ShopConfig shop = new ShopConfig();
            BeanUtils.copyProperties(dto, shop);
            shop.setUserId(userId);
            shop.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
            shop.setCreateTime(LocalDateTime.now());
            shop.setUpdateTime(LocalDateTime.now());

            try {
                shopConfigMapper.insert(shop);
            } catch (DuplicateKeyException e) {
                // 并发场景下唯一索引拦截重复插入，自动转为更新
                log.warn("用户 {} 并发创建店铺，转为更新", userId);
                return doUpdate(shopConfigMapper.selectByUserId(userId), dto, userId);
            }

            return toVO(shop);
        } else {
            // ========== 更新店铺 ==========
            return doUpdate(exist, dto, userId);
        }
    }

    /**
     * 抽取更新逻辑，防止null值覆盖
     */
    private ShopConfigVO doUpdate(ShopConfig exist, ShopConfigDto dto, Long userId) {
        // 手动赋值，防止 null 覆盖原有数据
        if (dto.getShopName() != null) exist.setShopName(dto.getShopName());
        if (dto.getShopAddress() != null) exist.setShopAddress(dto.getShopAddress());
        if (dto.getLongitude() != null) exist.setLongitude(dto.getLongitude());
        if (dto.getLatitude() != null) exist.setLatitude(dto.getLatitude());
        if (dto.getStatus() != null) exist.setStatus(dto.getStatus());

        exist.setUpdateTime(LocalDateTime.now());
        shopConfigMapper.update(exist);
        return toVO(exist);
    }

    /**
     * 获取当前用户自己的店铺信息
     */
    @Override
    public ShopConfigVO getMyShop() {
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            throw new BaseException(CodeException.UNAUTHORIZED, "用户未登录");
        }

        ShopConfig shop = shopConfigMapper.selectByUserId(userId);
        if (shop == null) {
            throw new BaseException(CodeException.NOT_FOUND, "店铺配置不存在，请先设置店铺信息");
        }
        return toVO(shop);
    }

    /**
     * 根据店铺ID查询店铺信息
     */
    @Override
    public ShopConfigVO getById(Long shopId) {
        if (shopId == null) {
            throw new BaseException(CodeException.PARAM_ERROR, "店铺ID不能为空");
        }

        ShopConfig shop = shopConfigMapper.selectById(shopId);
        if (shop == null) {
            throw new BaseException(CodeException.NOT_FOUND, "店铺不存在");
        }
        return toVO(shop);
    }

    /**
     * 将ShopConfig实体转为ShopConfigVO视图对象
     */
    private ShopConfigVO toVO(ShopConfig shop) {
        if (shop == null) return null;
        return ShopConfigVO.builder()
                .id(shop.getId())
                .userId(shop.getUserId())
                .shopName(shop.getShopName())
                .shopAddress(shop.getShopAddress())
                .longitude(shop.getLongitude())
                .latitude(shop.getLatitude())
                .status(shop.getStatus())
                .createTime(shop.getCreateTime())
                .updateTime(shop.getUpdateTime())
                .build();
    }
}