package org.example.fotoflow.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtils {
    // 密钥（自己随便改）
    private static final String KEY = "000000";


    // 生成 JWT 令牌
    public static String generateToken(Map<String, Object> claims) {
        return JWT.create()
                .withClaim("claims", claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 有效期1天
                .sign(Algorithm.HMAC256(KEY));
    }

    // 解析 JWT 令牌
    public static Map<String, Object> parseToken(String token) {
        return JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token)
                .getClaim("claims")
                .asMap();
    }
}
