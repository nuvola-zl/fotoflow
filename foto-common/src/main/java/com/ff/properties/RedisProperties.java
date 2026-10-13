package com.ff.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ff.redis")
@Data
public class RedisProperties {
    private String host = "localhost";
    private int port = 6379;
    private int database = 0;
    private String password;

    // 新增连接池配置
    private Lettuce lettuce = new Lettuce();

    @Data
    public static class Lettuce {
        private Pool pool = new Pool();

        @Data
        public static class Pool {
            private int maxActive = 8;
            private int maxIdle = 8;
            private int minIdle = 0;
            private long maxWait = 1000; // 毫秒
        }
    }
}
