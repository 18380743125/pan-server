package com.tangl.pan.cache.caffeine.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.tangl.pan.cache.core.constants.CacheConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

/**
 * caffeine cache 配置类
 */
@SpringBootConfiguration
@EnableCaching
@Slf4j
public class CaffeineCacheConfig {

    @Resource
    private CaffeineCacheProperties properties;

    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(CacheConstants.PAN_CACHE_NAME);
        cacheManager.setAllowNullValues(properties.getAllowNullValue());

        Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder()
                .initialCapacity(properties.getInitCacheCapacity())
                .maximumSize(properties.getMaxCacheCapacity());

        cacheManager.setCaffeine(caffeineBuilder);

        log.info("the caffeine cache manager has loaded successfully!");

        return cacheManager;
    }
}
