package com.tangl.pan.server.common.annotation;

import java.lang.annotation.*;

/**
 * 标注该注解的方法会自动屏蔽统一的登录拦截校验逻辑
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface LoginIgnore {

}
