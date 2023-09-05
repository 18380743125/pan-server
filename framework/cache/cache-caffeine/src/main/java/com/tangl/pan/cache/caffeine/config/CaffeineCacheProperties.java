package com.tangl.pan.cache.caffeine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author tangl
 * @description caffeine cache 配置属性类
 * @create 2023-07-24 10:59
 */
@Data
@Component
@ConfigurationProperties(value = "com.tangl.pan.cache.caffeine.config")
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
