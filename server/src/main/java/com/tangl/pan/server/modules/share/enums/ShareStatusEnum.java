package com.tangl.pan.server.modules.share.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 分享状态枚举类
 */
@AllArgsConstructor
@Getter
public enum ShareStatusEnum {

    NORMAL(0, "正常"),

    FILE_DELETE(1, "文件被删除");

    private final Integer code;

    private final String desc;
}
