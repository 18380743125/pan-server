package com.tangl.pan.server.modules.file.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author tangl
 * @description 文件夹枚举类
 * @create 2023-07-29 18:05
 */
@AllArgsConstructor
@Getter
public enum FolderFlagEnum {
    /**
     * 非文件夹
     */
    NO(0),

    /**
     * 是文件夹
     */
    YES(1);

    private Integer code;
}
