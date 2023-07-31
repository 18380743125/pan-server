package com.tangl.pan.cache.caffeine.test;

import cn.hutool.core.lang.Assert;
import com.tangl.pan.cache.caffeine.test.config.CaffeineCacheConfig;
import com.tangl.pan.cache.caffeine.test.instance.CacheAnnotationTester;
import com.tangl.pan.cache.core.constants.CacheConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author tangl
 * @description caffeine 缓存单元测试
 * @create 2023-07-24 11:37
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CaffeineCacheConfig.class)
public class CaffeineCacheTest {
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CacheAnnotationTester cacheAnnotationTester;

    /**
     * 简单测试 CacheManager 功能以及获取 Cache 对象的功能
     */
    @Test
    public void caffeineCacheManagerTest() {
        Cache cache = cacheManager.getCache(CacheConstants.T_PAN_CACHE_NAME);
        assert cache != null;
        cache.put("name", "value");
        String value = cache.get("name", String.class);
        Assert.isTrue("value".equals(value));
    }

    @Test
    public void  caffeineCacheAnnotationTest() {
        for (int i = 0; i < 2; i++) {
            cacheAnnotationTester.testCacheable("tangl");
        }
    }

}
