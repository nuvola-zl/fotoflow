package com.ff.utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 通用工具类
 * 封装常用的缓存操作，带 key 前缀管理
 */
@Component
@RequiredArgsConstructor
public class RedisUtil {


    private final RedisTemplate<String, Object> redisTemplate;

    /** 项目前缀，防止 key 冲突 */
    private static final String PREFIX = "ff:";

    /**
     * 拼接带前缀的 key
     */
    private String key(String module, String key) {
        return PREFIX + module + ":" + key;
    }

    // ==================== String 操作 ====================

    /**
     * 存入缓存，带过期时间
     *
     * @param module 模块名（如 goods、order、user）
     * @param key    业务 key
     * @param value  值
     * @param minutes 过期时间（分钟）
     */
    public void set(String module, String key, Object value, long minutes) {
        redisTemplate.opsForValue().set(key(module, key), value, minutes, TimeUnit.MINUTES);
    }

    /**
     * 存入缓存，永不过期（慎用）
     */
    public void set(String module, String key, Object value) {
        redisTemplate.opsForValue().set(key(module, key), value);
    }

    /**
     * 读取缓存
     *
     * @return 值，不存在返回 null
     */
    public Object get(String module, String key) {
        return redisTemplate.opsForValue().get(key(module, key));
    }

    /**
     * 删除缓存
     */
    public void delete(String module, String key) {
        redisTemplate.delete(key(module, key));
    }

    /**
     * 批量删除（支持通配符，如 goods:* 删除所有商品缓存）
     */
    public void deleteByPattern(String pattern) {
        String fullPattern = PREFIX + pattern;
        redisTemplate.keys(fullPattern).forEach(redisTemplate::delete);
    }

    // ==================== 常用业务封装 ====================

    /**
     * 缓存商品详情
     */
    public void setGoods(Long goodsId, Object value, long minutes) {
        set("goods", String.valueOf(goodsId), value, minutes);
    }

    /**
     * 读取商品详情
     */
    public Object getGoods(Long goodsId) {
        return get("goods", String.valueOf(goodsId));
    }

    /**
     * 删除商品详情缓存
     */
    public void deleteGoods(Long goodsId) {
        delete("goods", String.valueOf(goodsId));
    }

    /**
     * 清空所有商品缓存（商品批量更新时调用）
     */
    public void deleteAllGoods() {
        deleteByPattern("goods:*");
    }
}