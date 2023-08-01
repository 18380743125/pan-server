package com.tangl.pan.server.common.utils;

import com.tangl.pan.core.constants.TPanConstants;

import java.util.Objects;

/**
 * @author tangl
 * @description 用户 ID 存储工具类
 * @create 2023-07-31 14:41
 */
public class UserIdUtil {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的用户 ID
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
            return TPanConstants.ZERO_LONG;
        }
        return userId;
    }
}
