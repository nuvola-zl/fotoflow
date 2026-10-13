package com.ff.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class AutoFillAspect {

//@Before("execution(* com.ff.mapper.*.*(..)) && @annotation(com.ff.annotation.AutoFill)")
// 因为 IDEA 的 AspectJ 插件无法解析切入点表达式中的包路径，不影响实际运行，但看着难受。

    // 定义常量，避免直接写在注解里
    private static final String POINTCUT = "execution(* com.ff.mapper.*.*(..)) && @annotation(com.ff.annotation.AutoFill)";

    @Before(POINTCUT)
    public void autoFill(JoinPoint joinPoint) throws Throwable {

        Method method = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getMethod();
        com.ff.annotation.AutoFill autoFill = method.getAnnotation(com.ff.annotation.AutoFill.class);
        String operation = autoFill.value();

        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) return;

        Object entity = args[0];
        Class<?> clazz = entity.getClass();
        LocalDateTime now = LocalDateTime.now();

        if ("INSERT".equals(operation)) {
            setField(entity, clazz, "setCreateTime", LocalDateTime.class, now);
            setField(entity, clazz, "setUpdateTime", LocalDateTime.class, now);
            log.debug("自动填充 createTime、updateTime");

        } else if ("UPDATE".equals(operation)) {
            setField(entity, clazz, "setUpdateTime", LocalDateTime.class, now);
            log.debug("自动填充 updateTime");
        }
    }

    private void setField(Object entity, Class<?> clazz, String methodName, Class<?> paramType, Object value) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, paramType);
            method.invoke(entity, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.warn("实体类缺少方法: {}", methodName);
        }
    }
}