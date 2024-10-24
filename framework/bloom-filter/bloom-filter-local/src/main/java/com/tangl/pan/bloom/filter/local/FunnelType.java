package com.tangl.pan.bloom.filter.local;

import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.charset.StandardCharsets;

/**
 * 数据类型通道枚举类
 */
@AllArgsConstructor
@Getter
public enum FunnelType {

    /**
     * long 类型的数据通道
     */
    LONG(Funnels.longFunnel()),

    /**
     * int 类型的数据通道
     */
    INTEGER(Funnels.integerFunnel()),

    /**
     * string 类型的数据通道
     */
    STRING(Funnels.stringFunnel(StandardCharsets.UTF_8));

    private Funnel funnel;
}
