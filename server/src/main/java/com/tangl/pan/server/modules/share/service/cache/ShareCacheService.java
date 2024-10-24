package com.tangl.pan.server.modules.share.service.cache;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangl.pan.server.common.cache.AbstractManualCacheService;
import com.tangl.pan.server.modules.share.entity.PanShare;
import com.tangl.pan.server.modules.share.mapper.PanShareMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 手动缓存实现分享业务的查询等操作
 */
@Component(value = "shareManualCacheService")
public class ShareCacheService extends AbstractManualCacheService<PanShare> {

    @Autowired
    private PanShareMapper mapper;

    @Override
    protected BaseMapper<PanShare> getBaseMapper() {
        return mapper;
    }

    @Override
    public String getKeyFormat() {
        return "SHARE:ID:%s";
    }
}
