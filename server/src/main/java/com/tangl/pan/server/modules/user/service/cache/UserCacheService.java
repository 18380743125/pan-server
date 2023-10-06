package com.tangl.pan.server.modules.user.service.cache;

import com.tangl.pan.cache.core.constants.CacheConstants;
import com.tangl.pan.server.common.cache.AnnotationCacheService;
import com.tangl.pan.server.modules.user.entity.TPanUser;
import com.tangl.pan.server.modules.user.mapper.TPanUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author tangl
 * @description 用户模块缓存业务处理类
 * @create 2023-09-23 12:27
 */
@Component(value = "userAnnotationCacheService")
public class UserCacheService implements AnnotationCacheService<TPanUser> {

    @Autowired
    private TPanUserMapper mapper;

    /**
     * 根据 id 查询实体信息
     *
     * @param id id
     * @return V
     */
    @Cacheable(cacheNames = CacheConstants.T_PAN_CACHE_NAME, keyGenerator = "userIdKeyGenerator", sync = true)
    @Override
    public TPanUser getById(Serializable id) {
        System.out.println(id);
        return mapper.selectById(id);
    }

    /**
     * 根据 id 更新缓存
     *
     * @param id     id
     * @param entity V
     * @return boolean
     */
    @CachePut(cacheNames = CacheConstants.T_PAN_CACHE_NAME, keyGenerator = "userIdKeyGenerator")
    @Override
    public boolean updateById(Serializable id, TPanUser entity) {
        return mapper.updateById(entity) == 1;
    }

    /**
     * 根据 id 删除缓存
     *
     * @param id id
     * @return boolean
     */
    @CacheEvict(cacheNames = CacheConstants.T_PAN_CACHE_NAME, keyGenerator = "userIdKeyGenerator")
    @Override
    public boolean removeById(Serializable id) {
        return mapper.deleteById(id) == 1;
    }
}
