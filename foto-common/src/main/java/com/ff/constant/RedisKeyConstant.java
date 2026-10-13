package com.ff.constant;

public class RedisKeyConstant {
    /**
     * 点赞锁
     * 参数：userId + videoId
     */
    public static final String LOCK_LIKE = "lock:like:%s:%s";

    /**
     * 收藏锁
     * 参数：userId + videoId
     */
    public static final String LOCK_COLLECT = "lock:collect:%s:%s";
}
