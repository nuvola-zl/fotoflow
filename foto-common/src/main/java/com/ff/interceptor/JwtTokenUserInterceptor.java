package com.ff.interceptor;


import com.ff.constant.JwtClaimsConstant;
import com.ff.context.BaseContext;
import com.ff.properties.JwtProperties;
import com.ff.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.out.println("线程id"+Thread.currentThread().getId());

        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        String token = request.getHeader(jwtProperties.getUserTokenName());
        log.info("【JWT】URI={}, header字段={}, token={}",
                request.getRequestURI(),
                jwtProperties.getUserTokenName(),
                token);

        try {
            Claims claims = JwtUtils.parseToken(jwtProperties.getUserSecretKey(), token);
            log.info("【JWT】解析成功, claims={}", claims);

            Long currentId = null;
            if (claims.get(JwtClaimsConstant.USER_ID) != null) {
                currentId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
                log.info("【JWT】识别为用户, id={}", currentId);
            } else if (claims.get(JwtClaimsConstant.RIDER_ID) != null) {
                currentId = Long.valueOf(claims.get(JwtClaimsConstant.RIDER_ID).toString());
                log.info("【JWT】识别为骑手, id={}", currentId);
            } else {
                log.error("【JWT】claims里既没有userId也没有riderId!");
            }

            if (currentId != null) {
                BaseContext.setCurrentId(currentId);
            }
            return true;
        } catch (Exception ex) {
            log.error("【JWT】解析失败: {}", ex.getMessage());
            response.setStatus(401);
            return false;
        }
    }
}
