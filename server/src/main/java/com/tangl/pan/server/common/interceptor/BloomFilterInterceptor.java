package com.tangl.pan.server.common.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 布隆过滤器拦截器的顶级接口
 */
public interface BloomFilterInterceptor extends HandlerInterceptor {

    /**
     * 拦截器名称
     */
    String getName();

    /**
     * 要拦截的 URI 的集合
     */
    String[] getPathPatterns();

    /**
     * 要排除 URI 的集合
     */
    String[] getExcludePatterns();

}
