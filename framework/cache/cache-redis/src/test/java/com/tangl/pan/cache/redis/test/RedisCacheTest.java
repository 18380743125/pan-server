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
    public void redisCacheManagerTest() {
        Cache cache = cacheManager.getCache(CacheConstants.PAN_CACHE_NAME);
        cache.put("name", "value");
        String value = cache.get("name", String.class);
        Assert.isTrue("value".equals(value));
    }

    @Test
    public void redisCacheAnnotationTest() {
        for (int i = 0; i < 2; i++) {
            cacheAnnotationTester.testCacheable("tangl");
        }
    }

}
