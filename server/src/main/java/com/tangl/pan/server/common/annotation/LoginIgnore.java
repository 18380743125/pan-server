package com.tangl.pan.server.common.annotation;

import java.lang.annotation.*;

/**
 * @author tangl
 * @description 标注该注解的方法会自动屏蔽统一的登录拦截校验逻辑
 * @create 2023-07-31 15:20
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface LoginIgnore {
}
