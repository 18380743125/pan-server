package com.tangl.pan.lock.core.annotation;

import com.tangl.pan.lock.core.key.KeyGenerator;
import com.tangl.pan.lock.core.key.StandardKeyGenerator;

import java.lang.annotation.*;

/**
 * 自定义锁的注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Lock {

    /**
     * 锁的名称
     */
    String name() default "";

    /**
     * 锁的过期时长
     */
    long expireSecond() default 60L;

    /**
     * 自定义锁的 key，支持 el 表达式
     */
    String[] keys() default {};

    /**
     * 指定锁 key 的生成器
     */
    Class<? extends KeyGenerator> keyGenerator() default StandardKeyGenerator.class;
}
