package com.tangl.pan.server.modules.user.constants;

/**
 * 用户模块的常量类
 */
public interface UserConstants {

    /**
     * 生成登录 token 的 key
     */
    String LOGIN_USER_ID = "LOGIN_USER_ID";

    /**
     * 用户登录缓存前缀
     */
    String USER_LOGIN_PREFIX = "USER_LOGIN_";

    /**
     * 用户忘记密码-重置密码临时 token 的 key
     */
    String FORGET_USERNAME = "FORGET_USERNAME";

    /**
     * 一天的 ms
     */
    Long ONE_DAY_LONG = 24L * 60 * 60 * 1000;

    /**
     * 5分钟的 ms
     */
    Long FIVE_MINUTES_LONG = 5L * 60 * 1000;
}
