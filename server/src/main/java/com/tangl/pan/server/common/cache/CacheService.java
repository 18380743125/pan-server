package com.tangl.pan.server.common.cache;

import java.io.Serializable;

/**
 * @author tangl
 * @description 支持业务缓存的 Service
 * @create 2023-09-21 22:31
 */
public interface CacheService<V> {

    /**
     * 根据 id 查询实体信息
     *
     * @param id id
     * @return V
     */
    V getById(Serializable id);

    /**
     * 根据 id 更新缓存
     *
     * @param id     id
     * @param entity V
     * @return boolean
     */
    boolean updateById(Serializable id, V entity);

    /**
     * 根据 id 删除缓存
     *
     * @param id id
     * @return boolean
     */
    boolean removeById(Serializable id);
}
