package com.ff.annotation;

import java.lang.annotation.*;

/**
 * 接口限流注解
 * 基于 Redis 滑动窗口实现
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /**
     * 限流 key，支持 SpEL 表达式
     * 为空时用 "类名:方法名"
     */
    String key() default "";

    /**
     * 时间窗口内允许的最大请求数
     */
    int count() default 10;

    /**
     * 时间窗口大小（秒）
     */
    int window() default 1;

    /**
     * 提示信息
     */
    String message() default "请求过于频繁，请稍后再试";
}