package com.tangl.pan.server.modules.file.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author tangl
 * @description 文件合并标识枚举类
 * @create 2023-09-07 9:49
 */
@Getter
@AllArgsConstructor
public enum MergeFlagEnum {

    /**
     * 不需要合并
     */
    NOT_READY(0),

    /**
     * 需要合并
     */
    READY(1);

    private final Integer code;
}
