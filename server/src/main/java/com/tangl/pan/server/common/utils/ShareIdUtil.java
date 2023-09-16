package com.tangl.pan.server.common.utils;

import com.tangl.pan.core.constants.TPanConstants;

import java.util.Objects;

/**
 * @author tangl
 * @description 分享 ID 存储工具类
 * @create 2023-09-16 23:12
 */
public class ShareIdUtil {

    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 当前线程的分享 ID
     *
     * @param shareId userId
     */
    public static void set(Long shareId) {
        threadLocal.set(shareId);
    }

    /**
     * 获取当前线程的分享 ID
     *
     * @return shareId
     */
    public static Long get() {
        Long shareId = threadLocal.get();
        if (Objects.isNull(shareId)) {
            return TPanConstants.ZERO_LONG;
        }
        return shareId;
    }
}
