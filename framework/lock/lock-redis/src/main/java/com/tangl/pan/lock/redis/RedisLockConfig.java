package com.tangl.pan.lock.redis;

import com.tangl.pan.lock.core.LockConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

/**
 * @author tangl
 * @description 基于 redis 的分布式锁，该方法集成 spring-data-redis，配置项复用原来的配置
 * @create 2023-09-24 15:33
 */
@SpringBootConfiguration
@Slf4j
public class RedisLockConfig {

    @Bean
    public LockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        RedisLockRegistry lockRegistry = new RedisLockRegistry(redisConnectionFactory, LockConstants.T_PAN_LOCK);
        log.info("redis lock is loaded successfully");
        return lockRegistry;
    }

}
