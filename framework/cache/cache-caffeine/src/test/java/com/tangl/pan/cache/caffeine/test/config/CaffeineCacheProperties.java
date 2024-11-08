package com.tangl.pan.cache.caffeine.test.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * caffeine cache 自定义配置属性类
 */
@Data
@Component
@ConfigurationProperties(prefix = "com.tangl.pan.cache.caffeine")
public class CaffeineCacheProperties {

    /**
     * 缓存初始容量
     */
    private Integer initCacheCapacity = 256;

    /**
     * 缓存最大容量, 超过之后按照 LRU 剔除
     */
    private Long maxCacheCapacity = 10000L;

    /**
     * 是否允许空值 null 作为缓存的 value
     */
    private Boolean allowNullValue = Boolean.TRUE;
}
