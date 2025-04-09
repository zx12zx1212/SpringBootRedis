package com.example.springBoot_Redis.config;

import java.time.Duration;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

    @Setter(onMethod_ = @Autowired)
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public CacheManager cacheManager() {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()                   // 使用 Redis 快取的預設設定
                .entryTtl(Duration.ofMinutes(6));       // 設定快取有效時間
        return RedisCacheManager
                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory)) // 使用非鎖定的 Redis Cache Writer，避免分布式環境中的效能問題
                .cacheDefaults(redisCacheConfiguration) // 設定 Redis 快取的預設行為
                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 設定 Redis 連接工廠，讓 RedisTemplate 能夠連接到 Redis 伺服器
        template.setConnectionFactory(factory);
        // 設定 Key 的序列化方式，確保 Key 是可讀的 String
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        // 設定 JSON 序列化，讓儲存的資料以 JSON 格式存入 Redis
        template.setDefaultSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        // 啟用 Redis 交易支援（可確保多個 Redis 操作在同一個交易中執行）
        template.setEnableTransactionSupport(true);
        // 初始化 RedisTemplate 的內部屬性（需要在設定完參數後呼叫）
        template.afterPropertiesSet();
        return template;
    }

}
