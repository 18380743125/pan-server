package com.tangl.pan.server.modules.share.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author tangl
 * @description 分享类型枚举类
 * @create 2023-09-16 15:44
 */
@AllArgsConstructor
@Getter
public enum ShareTypeEnum {

    NEED_SHARE_CODE(0, "有提取码");

    private final Integer code;

    private final String desc;
}
