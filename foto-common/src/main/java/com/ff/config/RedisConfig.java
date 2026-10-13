package com.ff.config;

import com.ff.properties.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Redis 配置类
 * 作用：让 Spring Boot 项目可以连接 Redis，并且配置好连接池、序列化规则
 */
@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    // 读取配置文件里的 Redis 信息：host、port、password、database、连接池参数
    private final RedisProperties redisProperties;

    /**
     * 1. 创建 Redis 连接工厂（核心！真正和 Redis 建立连接的对象）
     * 底层使用 Lettuce 连接（Spring 默认）
     */
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        // ====================== 1. 配置 Redis 服务器地址信息 ======================
        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration();

        // Redis 服务器 IP
        serverConfig.setHostName(redisProperties.getHost());

        // Redis 端口
        serverConfig.setPort(redisProperties.getPort());

        // 使用 Redis 的哪个库（0~15）
        serverConfig.setDatabase(redisProperties.getDatabase());

        // 如果配置了密码，就设置密码
        if (StringUtils.hasText(redisProperties.getPassword())) {
            serverConfig.setPassword(redisProperties.getPassword());
        }

        // ====================== 2. 配置 Redis 连接池 ======================
        // 连接池：控制同时有多少个连接访问 Redis，防止把 Redis 冲垮
        GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();

        // 最大连接数（最多同时多少个连接连 Redis）
        poolConfig.setMaxTotal(redisProperties.getLettuce().getPool().getMaxActive());

        // 最大空闲连接（空闲时保留多少个连接）
        poolConfig.setMaxIdle(redisProperties.getLettuce().getPool().getMaxIdle());

        // 最小空闲连接（至少保留多少个连接）
        poolConfig.setMinIdle(redisProperties.getLettuce().getPool().getMinIdle());

        // 获取连接最大等待时间（拿不到连接就等多久）
        poolConfig.setMaxWaitMillis(redisProperties.getLettuce().getPool().getMaxWait());

        // 每隔 30 秒检查一次空闲连接
        poolConfig.setTimeBetweenEvictionRunsMillis(30000);

        // 检查空闲连接是否有效（无效就删掉，防止卡死）
        poolConfig.setTestWhileIdle(true);

        // ====================== 3. 配置 Lettuce 客户端 ======================
        LettucePoolingClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                // 执行 Redis 命令超时时间：5 秒
                .commandTimeout(Duration.ofSeconds(5))
                // 关闭超时时间
                .shutdownTimeout(Duration.ofMillis(100))
                // 使用上面配置的连接池
                .poolConfig(poolConfig)
                .build();

        // 返回连接工厂 → 给 RedisTemplate 使用
        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }

    /**
     * 2. 创建 RedisTemplate（项目中真正用来操作 Redis 的工具）
     * 作用：存数据、取数据、删数据、所有 Redis 操作都靠它
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // 设置连接工厂（让它能连上 Redis）
        template.setConnectionFactory(connectionFactory);

        // ====================== 序列化规则（非常重要） ======================
        // 序列化 = 规定数据怎么存进 Redis、怎么从 Redis 读出来

        // 1. Key 一律使用 String 序列化（防止乱码）
        StringRedisSerializer stringSerializer = new StringRedisSerializer(StandardCharsets.UTF_8);
        template.setKeySerializer(stringSerializer);        // 普通 key
        template.setHashKeySerializer(stringSerializer);    // hash 类型的 key

        // 2. Value 使用 JSON 序列化（存对象、读对象超级方便）
        // Spring 官方推荐：Jackson 序列化
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);        // 普通 value
        template.setHashValueSerializer(jsonSerializer);    // hash 类型的 value

        // 让配置生效
        template.afterPropertiesSet();

        // 返回 RedisTemplate → 项目里 @Autowired 直接用
        return template;
    }
}