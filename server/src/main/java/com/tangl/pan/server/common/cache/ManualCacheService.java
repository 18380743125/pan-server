package com.tangl.pan.server.common.cache;

import org.springframework.cache.Cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author tangl
 * @description 手动缓存处理 Service 顶级接口
 * @create 2023-09-21 22:37
 */
public interface ManualCacheService<V> extends CacheService<V> {

    /**
     * 根据 id 集合查询实体记录列表
     *
     * @param ids id 集合
     * @return 实体记录列表
     */
    List<V> getByIds(Collection<? extends Serializable> ids);

    /**
     * 批量更新实体记录列表
     *
     * @param entityMap 更新的 id - V 的 Map
     * @return boolean
     */
    boolean updateByIds(Map<? extends Serializable, V> entityMap);

    /**
     * 批量删除实体记录列表
     *
     * @param ids id 集合
     * @return boolean
     */
    boolean removeByIds(Collection<? extends Serializable> ids);

    /**
     * 获取缓存 key 的模板信息
     *
     * @return String
     */
    String getKeyFormat();

    /**
     * 获取缓存对象
     *
     * @return 缓存对象
     */
    Cache getCache();
}
