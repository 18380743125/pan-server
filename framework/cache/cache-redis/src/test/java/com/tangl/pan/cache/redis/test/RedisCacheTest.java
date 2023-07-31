package com.tangl.pan.cache.redis.test;

import cn.hutool.core.lang.Assert;
import com.tangl.pan.cache.core.constants.CacheConstants;
import com.tangl.pan.cache.redis.test.instance.CacheAnnotationTester;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author tangl
 * @description
 * @create 2023-07-25 18:23
 */
@SpringBootTest(classes = RedisCacheTest.class)
@SpringBootApplication
@RunWith(SpringJUnit4ClassRunner.class)
public class RedisCacheTest {
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
