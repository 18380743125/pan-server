package com.tangl.pan.core.exception;

import com.tangl.pan.core.response.ResponseCode;
import lombok.Getter;

/**
 * @author tangl
 * @description 自定义全局业务异常类
 * @create 2023-06-22 15:07
 */
@Getter
public class TPanBusinessException extends RuntimeException {

    private static final long serialVersionUID = 6989859219046497129L;
    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;

    public TPanBusinessException(ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getDesc();
    }

    public TPanBusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public TPanBusinessException(String message) {
        this.code = ResponseCode.ERROR_PARAM.getCode();
        this.message = message;
    }

    public TPanBusinessException() {
        this.code = ResponseCode.ERROR_PARAM.getCode();
        this.message = ResponseCode.ERROR_PARAM.getDesc();
    }

}
