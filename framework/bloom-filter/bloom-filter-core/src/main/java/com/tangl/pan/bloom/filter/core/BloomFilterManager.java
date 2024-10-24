package com.tangl.pan.bloom.filter.core;

import java.util.Collection;

/**
 * 布隆过滤器管理器的顶级接口
 */
public interface BloomFilterManager<T> {

    /**
     * 根据名称获取对应的过滤器
     *
     * @param name 名称
     * @return 过滤器
     */
    BloomFilter<T> getFilter(String name);

    /**
     * 获取目前管理器中布隆过滤器名称列表
     *
     * @return 过滤器列表
     */
    Collection<String> getFilterNames();
}
