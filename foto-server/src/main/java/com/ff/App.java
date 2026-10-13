package com.ff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 项目唯一启动类
 */
@SpringBootApplication
@EnableScheduling  // 开启定时任务
@EnableConfigurationProperties
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        System.out.println("✅ FotoFlow 项目启动成功！");
    }
}