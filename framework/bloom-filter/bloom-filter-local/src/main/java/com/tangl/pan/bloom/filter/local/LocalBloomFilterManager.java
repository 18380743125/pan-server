package com.tangl.pan.bloom.filter.local;

import com.google.common.collect.Maps;
import com.tangl.pan.bloom.filter.core.BloomFilter;
import com.tangl.pan.bloom.filter.core.BloomFilterManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 本地布隆过滤器的管理器
 */
@Slf4j
@Component
public class LocalBloomFilterManager<T> implements BloomFilterManager, InitializingBean {

    @Resource
    private LocalBloomFilterConfig config;

    /**
     * 容器
     */
    private final Map<String, BloomFilter<T>> bloomFilterContainer = Maps.newConcurrentMap();

    /**
     * 根据名称获取对应的过滤器
     *
     * @param name 名称
     * @return 过滤器
     */
    @Override
    public BloomFilter<T> getFilter(String name) {
        return bloomFilterContainer.get(name);
    }

    /**
     * 获取目前管理器中布隆过滤器名称列表
     *
     * @return 过滤器列表
     */
    @Override
    public Collection<String> getFilterNames() {
        return bloomFilterContainer.keySet();
    }

    @Override
    public void afterPropertiesSet() {
        List<LocalBloomFilterConfigItem> items = config.getItems();
        if (CollectionUtils.isEmpty(items)) {
            return;
        }
        items.forEach(item -> {
            String funnelTypeName = item.getFunnelTypeName();
            try {
                FunnelType funnelType = FunnelType.valueOf(funnelTypeName);
                bloomFilterContainer.putIfAbsent(
                        item.getName(),
                        new LocalBloomFilter<T>(
                                funnelType.getFunnel(),
                                item.getExpectedInsertions(),
                                item.getFpp()
                        )
                );
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
    }
}
