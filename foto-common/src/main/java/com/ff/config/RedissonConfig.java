package com.ff.config;
import com.ff.properties.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RedissonConfig {


    private final  RedisProperties redisProperties;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        String address = String.format("redis://%s:%d",
                redisProperties.getHost(),
                redisProperties.getPort());

        config.useSingleServer()
                .setAddress(address)
                .setDatabase(redisProperties.getDatabase());

        // 密码非空才设置
        String password = redisProperties.getPassword();
        if (password != null && !password.isEmpty()) {
            config.useSingleServer().setPassword(password);
        }

        return Redisson.create(config);
    }
}