package com.tangl.pan.bloom.filter.core;

/**
 * @author tangl
 * @description 布隆过滤器的顶级接口
 * @create 2023-09-23 21:03
 */
public interface BloomFilter<T> {

    /**
     * 放入元素
     *
     * @param object 元素
     * @return boolean
     */
    boolean put(T object);

    /**
     * 判断元素是不是可能存在
     *
     * @param object 元素
     * @return boolean
     */
    boolean mightContain(T object);

    /**
     * 清空元素
     */
    void clear();
}
