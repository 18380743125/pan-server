package com.tangl.pan.server.common.interceptor;

import com.tangl.pan.bloom.filter.core.BloomFilter;
import com.tangl.pan.bloom.filter.core.BloomFilterManager;
import com.tangl.pan.core.exception.PanBusinessException;
import com.tangl.pan.core.response.ResponseCode;
import com.tangl.pan.core.utils.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 查询分享简单详情布隆过滤器拦截器
 */
@Component
@Slf4j
public class ShareSimpleDetailBloomFilterInterceptor implements BloomFilterInterceptor {

    private static final String BLOOM_FILTER_NAME = "SHARE_SIMPLE_DETAIL";

    @Autowired
    private BloomFilterManager manager;

    @Override
    public String getName() {
        return "ShareSimpleDetailBloomFilterInterceptor";
    }

    @Override
    public String[] getPathPatterns() {
        return ArrayUtils.toArray("/share/simple");
    }

    @Override
    public String[] getExcludePatterns() {
        return new String[0];
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String encShareId = request.getParameter("shareId");
        if (StringUtils.isBlank(encShareId)) {
            throw new PanBusinessException("分享ID不能为空");
        }
        BloomFilter<Long> bloomFilter = manager.getFilter(BLOOM_FILTER_NAME);
        if (Objects.isNull(bloomFilter)) {
            log.info("the bloomFilter name {} is null， give up existence judgment...", BLOOM_FILTER_NAME);
            return true;
        }
        Long shareId = IdUtil.decrypt(URLDecoder.decode(encShareId, StandardCharsets.UTF_8.toString()));
        boolean mightContain = bloomFilter.mightContain(shareId);
        if (mightContain) {
            log.info("the bloomFilter name {} judge shareId {} mightContain pass...", BLOOM_FILTER_NAME, shareId);
            return true;
        }
        log.info("the bloomFilter name {} judge shareId {} mightContain fail...", BLOOM_FILTER_NAME, shareId);
        throw new PanBusinessException(ResponseCode.SHARE_CANCELLED);
    }
}
