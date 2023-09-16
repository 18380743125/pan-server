package com.tangl.pan.server.common.annotation;

import java.lang.annotation.*;

/**
 * @author tangl
 * @description 该注解主要影响需要分享码校验的接口
 * @create 2023-09-16 23:18
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NeedShareCode {

}
