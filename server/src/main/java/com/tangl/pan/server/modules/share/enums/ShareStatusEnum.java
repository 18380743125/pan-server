package com.tangl.pan.server.modules.share.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author tangl
 * @description 分享状态枚举类
 * @create 2023-09-16 15:44
 */
@AllArgsConstructor
@Getter
public enum ShareStatusEnum {

    NORMAL(0, "正常"),

    FILE_DELETE(1, "文件被删除");

    private final Integer code;

    private final String desc;
}
