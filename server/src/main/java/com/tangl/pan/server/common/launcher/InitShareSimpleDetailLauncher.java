package com.tangl.pan.server.common.launcher;

import com.tangl.pan.bloom.filter.core.BloomFilter;
import com.tangl.pan.bloom.filter.core.BloomFilterManager;
import com.tangl.pan.server.modules.share.service.IShareService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author tangl
 * @description 分享简单详情布隆过滤器初始化器
 * @create 2023-09-23 23:23
 */
@Component
@Slf4j
public class InitShareSimpleDetailLauncher implements CommandLineRunner {

    private static final String BLOOM_FILTER_NAME = "SHARE_SIMPLE_DETAIL";

    @Autowired
    private BloomFilterManager manager;

    @Autowired
    private IShareService shareService;

    @Override
    public void run(String... args) throws Exception {
        log.info("start init ShareSimpleDetailBloomFilter...");

        BloomFilter<Long> bloomFilter = manager.getFilter(BLOOM_FILTER_NAME);
        if (Objects.isNull(bloomFilter)) {
            log.info("the bloom named {} is null, give up init...", BLOOM_FILTER_NAME);
            return;
        }
        bloomFilter.clear();

        long startId = 0L;
        long limit = 10000L;
        AtomicLong addCount = new AtomicLong(0);

        List<Long> shareIdList;

        do {
            shareIdList = shareService.rollingQueryShareId(startId, limit);
            if (CollectionUtils.isNotEmpty(shareIdList)) {
                shareIdList.forEach(shareId -> {
                    bloomFilter.put(shareId);
                    addCount.incrementAndGet();
                });
                startId = shareIdList.get(shareIdList.size() - 1);
            }
        } while (CollectionUtils.isNotEmpty(shareIdList));

        log.info("finish init ShareSimpleDetailBloomFilter，total set item count {}...", addCount.get());
    }
}
