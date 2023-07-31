package com.tangl.pan.cache.caffeine.test.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.tangl.pan.cache.core.constants.CacheConstants;
import com.tangl.pan.core.constants.TPanConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author tangl
 * @description
 * @create 2023-07-24 11:07
 */
@SpringBootConfiguration
@EnableCaching
@ComponentScan(value = TPanConstants.BASE_COMPONENT_SCAN_PATH + ".cache.caffeine.test")
public class CaffeineCacheConfig {
    @Autowired
    private CaffeineCacheProperties properties;

    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(CacheConstants.T_PAN_CACHE_NAME);
        cacheManager.setAllowNullValues(properties.getAllowNullValue());
        Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder()
                .initialCapacity(properties.getInitCacheCapacity())
                .maximumSize(properties.getMaxCacheCapacity());
        cacheManager.setCaffeine(caffeineBuilder);
        return cacheManager;
    }
}
