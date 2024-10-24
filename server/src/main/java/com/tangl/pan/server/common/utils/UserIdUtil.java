package com.tangl.pan.server.common.utils;

import com.tangl.pan.core.constants.PanConstants;

import java.util.Objects;

/**
 * 用户 ID 存储工具类
 */
public class UserIdUtil {

    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 当前线程的用户 ID
     *
     * @param userId userId
     */
    public static void set(Long userId) {
        threadLocal.set(userId);
    }

    /**
     * 获取当前线程的用户 ID
     *
     * @return userId
     */
    public static Long get() {
        Long userId = threadLocal.get();
        if (Objects.isNull(userId)) {
            return PanConstants.ZERO_LONG;
        }
        return userId;
    }
}
