package com.tangl.pan.cache.redis.test.instance;

import com.tangl.pan.cache.core.constants.CacheConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * redis cache注解测试实体
 */
@Component
@Slf4j
public class CacheAnnotationTester {
    /**
     * 测试自适应缓存注解
     *
     * @param name 参数
     * @return string
     */
    @Cacheable(cacheNames = CacheConstants.T_PAN_CACHE_NAME, key = "#name", sync = true)
    public String testCacheable(String name) {
        log.info("call com.tangl.pan.cache.redis.test.instance.CacheAnnotationTester.testCacheable, param is {}", name);
        return "hello" + name;
    }
}
