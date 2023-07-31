package com.tangl.pan.cache.redis.test.config;

import com.tangl.pan.core.constants.TPanConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author tangl
 * @description redis cache 的配置类
 * 该缓存方案不支持事务
 * 该缓存方案直接集成 spring-boot-starter-data-redis 直接默认使用 spring 配置
 * @create 2023-07-25 15:32
 */
@SpringBootConfiguration
@EnableCaching
@Slf4j
@ComponentScan(value = TPanConstants.BASE_COMPONENT_SCAN_PATH + ".cache.redis.test")
public class RedisCacheConfig {
    /**
     * 定制链接和操作 redis的客户端工具
     *
     * @param redisConnectionFactory redis 链接工厂
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        StringRedisSerializer redisSerializer = new StringRedisSerializer();

        redisTemplate.setKeySerializer(redisSerializer);
        redisTemplate.setHashKeySerializer(redisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        return redisTemplate;
    }

    /**
     * 定制化 redis 的缓存管理器
     *
     * @param redisConnectionFactory redis 链接工厂
     * @return CacheManager
     */
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(Object.class)));

        RedisCacheManager cacheManager = RedisCacheManager
                .builder(RedisCacheWriter.lockingRedisCacheWriter(redisConnectionFactory))
                .cacheDefaults(redisCacheConfiguration)
                .transactionAware()
                .build();

        log.info("the redis cache manager is loaded successfully!");

        return cacheManager;
    }
}
