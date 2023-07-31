package com.tangl.pan.server.modules.file.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author tangl
 * @description 文件删除标识枚举类
 * @create 2023-07-29 22:47
 */
@AllArgsConstructor
@Getter
public enum DelFlagEnum {
    /**
     * 未删除
     */
    NO(0),
    /**
     * 已删除
     */
    YES(1);
    private Integer code;
}
