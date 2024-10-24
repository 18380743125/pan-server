package com.tangl.pan.server.common.cache;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.common.collect.Lists;
import com.tangl.pan.cache.core.constants.CacheConstants;
import com.tangl.pan.core.exception.PanFrameworkException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 手动处理缓存的公用顶级父类
 */
public abstract class AbstractManualCacheService<V> implements ManualCacheService<V> {

    @Autowired(required = false)
    private CacheManager cacheManager;

    private final Object lock = new Object();

    protected abstract BaseMapper<V> getBaseMapper();

    /**
     * 根据 id 查询实体信息
     * 1、查询缓存，如果命中，直接返回
     * 2、如果没有命中，查询数据库
     * 3、如果数据库有对应的记录，回填缓存
     *
     * @param id id
     * @return V
     */
    @Override
    public V getById(Serializable id) {
        V result = getByCache(id);
        if (Objects.nonNull(result)) {
            return result;
        }
        // 使用锁机制来保障避免缓存的击穿问题
        synchronized (lock) {
            result = getByCache(id);
            if (Objects.nonNull(result)) {
                return result;
            }
            result = getByDB(id);
            if (Objects.nonNull(result)) {
                putCache(id, result);
            }
        }
        return result;
    }

    /**
     * 根据 ID 来更新缓存信息
     *
     * @param id     id
     * @param entity V
     * @return 是否更新成功
     */
    @Override
    public boolean updateById(Serializable id, V entity) {
        int rowNum = getBaseMapper().updateById(entity);
        removeCache(id);
        return rowNum == 1;
    }

    /**
     * 根据 id 来删除缓存信息
     *
     * @param id id
     * @return 是否删除成功
     */
    @Override
    public boolean removeById(Serializable id) {
        int rowNum = getBaseMapper().deleteById(id);
        removeCache(id);
        return rowNum == 1;
    }

    @Override
    public List<V> getByIds(Collection<? extends Serializable> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        return ids.stream().map(this::getById).collect(Collectors.toList());
    }

    @Override
    public boolean updateByIds(Map<? extends Serializable, V> entityMap) {
        if (MapUtils.isEmpty(entityMap)) {
            return false;
        }
        for (Map.Entry<? extends Serializable, V> entry : entityMap.entrySet()) {
            if (!updateById(entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        for (Serializable id : ids) {
            if (!removeById(id)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Cache getCache() {
        if (Objects.isNull(cacheManager)) {
            throw new PanFrameworkException("the cache manager is empty!");
        }
        return cacheManager.getCache(CacheConstants.T_PAN_CACHE_NAME);
    }

    /**
     * 删除缓存信息
     *
     * @param id id
     */
    private void removeCache(Serializable id) {
        String cacheKey = getCacheKey(id);
        Cache cache = getCache();
        if (Objects.isNull(cache)) {
            return;
        }
        cache.evict(cacheKey);
    }

    /**
     * 将实体信息保存到缓存中
     *
     * @param id     id
     * @param entity value
     */
    private void putCache(Serializable id, V entity) {
        String cacheKey = getCacheKey(id);
        Cache cache = getCache();
        if (Objects.isNull(cache) || Objects.isNull(entity)) {
            return;
        }
        cache.put(cacheKey, entity);
    }

    /**
     * 根据 主键查询对应的实体信息
     *
     * @param id id
     * @return V
     */
    private V getByDB(Serializable id) {
        return getBaseMapper().selectById(id);
    }

    /**
     * 根据 ID 从缓存中查询对应的实体信息
     *
     * @param id id
     * @return V
     */
    private V getByCache(Serializable id) {
        String cacheKey = getCacheKey(id);
        Cache cache = getCache();
        if (Objects.isNull(cache)) {
            return null;
        }
        Cache.ValueWrapper valueWrapper = cache.get(cacheKey);
        if (Objects.isNull(valueWrapper)) {
            return null;
        }
        return (V) valueWrapper.get();
    }

    /**
     * 生成对应的缓存 key
     *
     * @param id id
     * @return 缓存 key
     */
    private String getCacheKey(Serializable id) {
        return String.format(getKeyFormat(), id);
    }
}
