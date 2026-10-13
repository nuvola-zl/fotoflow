package com.ff.aspect;

import com.ff.annotation.RateLimiter;
import com.ff.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 限流切面
 * 拦截 @RateLimiter 注解，基于 Redis ZSet 滑动窗口限流
 */
@Slf4j
@Aspect
@Component
public class RateLimiterAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final SpelExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(rateLimiter)")
    public Object around(ProceedingJoinPoint point, RateLimiter rateLimiter) throws Throwable {
        String key = generateKey(point, rateLimiter);
        String redisKey = "rate_limit:" + key;

        long now = System.currentTimeMillis();
        long windowStart = now - rateLimiter.window() * 1000L;

        // 1. 清理窗口外的旧记录
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, windowStart);

        // 2. 添加当前请求时间戳
        redisTemplate.opsForZSet().add(redisKey, String.valueOf(now), now);

        // 3. 设置 key 过期时间（窗口 + 1 秒缓冲）
        redisTemplate.expire(redisKey, java.time.Duration.ofSeconds(rateLimiter.window() + 1));

        // 4. 统计窗口内请求数
        Long currentCount = redisTemplate.opsForZSet().zCard(redisKey);

        if (currentCount != null && currentCount > rateLimiter.count()) {
            log.warn("限流触发：key={}，当前 {} 次/{}秒", key, currentCount, rateLimiter.window());
            throw new BaseException(rateLimiter.message());
        }

        return point.proceed();
    }

    /**
     * 生成限流 key
     */
    private String generateKey(ProceedingJoinPoint point, RateLimiter rateLimiter) {
        String keyExpr = rateLimiter.key();
        if (keyExpr.isEmpty()) {
            // 默认：类名:方法名
            return point.getTarget().getClass().getSimpleName() + ":" + point.getSignature().getName();
        }

        // SpEL 解析，从方法参数中提取值
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Object[] args = point.getArgs();

        StandardEvaluationContext context = new StandardEvaluationContext();
        String[] paramNames = discoverer.getParameterNames(method);
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }

        return parser.parseExpression(keyExpr).getValue(context, String.class);
    }
}