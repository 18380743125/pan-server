package com.tangl.pan.server.modules.share.service.cache;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangl.pan.server.common.cache.AbstractManualCacheService;
import com.tangl.pan.server.modules.share.entity.TPanShare;
import com.tangl.pan.server.modules.share.mapper.TPanShareMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author tangl
 * @description 手动缓存实现分享业务的查询等操作
 * @create 2023-09-23 12:20
 */
@Component(value = "shareManualCacheService")
public class ShareCacheService extends AbstractManualCacheService<TPanShare> {

    @Autowired
    private TPanShareMapper mapper;

    @Override
    protected BaseMapper<TPanShare> getBaseMapper() {
        return mapper;
    }

    @Override
    public String getKeyFormat() {
        return "SHARE:ID:%s";
    }
}
