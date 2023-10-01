package com.tangl.pan.server.common.stream.event.log;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author tangl
 * @description 错误日志事件
 * @create 2023-09-04 21:49
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class ErrorLogEvent implements Serializable {

    private static final long serialVersionUID = 4814882850368805113L;

    /**
     * 错误日志的内容
     */
    private String errorMsg;

    /**
     * 当前的登录用户ID
     */
    private Long userId;

    public ErrorLogEvent(String errorMsg, Long userId) {
        this.errorMsg = errorMsg;
        this.userId = userId;
    }
}
