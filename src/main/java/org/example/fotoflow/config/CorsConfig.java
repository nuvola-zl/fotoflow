package org.example.fotoflow.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许所有路径跨域
        registry.addMapping("/**")
                // 允许所有请求头
                .allowedHeaders("*")
                // 允许所有请求方法
                .allowedMethods("*")
                // 允许所有来源（前端地址）
                .allowedOriginPatterns("*")
                // 允许携带cookie（token）
                .allowCredentials(true)
                // 预检请求有效期3600秒
                .maxAge(3600);
    }
}