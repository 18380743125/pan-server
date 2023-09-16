package com.tangl.pan.server.modules.share.enums;

import com.tangl.pan.core.constants.TPanConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author tangl
 * @description 分享日期枚举类
 * @create 2023-09-16 15:48
 */
@AllArgsConstructor
@Getter
public enum ShareDayTypeEnum {

    PERMANENT_VALIDITY(0, 0, "永久有效"),

    SEVEN_DAYS_VALIDITY(1, 7, "七天有效"),

    THIRTY_DAYS_VALIDITY(2, 30, "三十天有效");

    private final Integer code;

    private final Integer days;

    private final String desc;

    /**
     * 根据 code 获取有效天数
     *
     * @param code 天数类型编号
     * @return days
     */
    public static Integer getShareDaysByCode(Integer code) {
        if (Objects.isNull(code)) {
            return TPanConstants.MINUS_ONE_INT;
        }
        for (ShareDayTypeEnum value : values()) {
            if (Objects.equals(value.getCode(), code)) {
                return value.getDays();
            }
        }
        return TPanConstants.MINUS_ONE_INT;
    }
}
