package com.tangl.pan.core.exception;

import com.tangl.pan.core.response.ResponseCode;
import lombok.Getter;

/**
 * 自定义全局业务异常类
 */
@Getter
public class PanBusinessException extends RuntimeException {

    private static final long serialVersionUID = 6989859219046497129L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;

    public PanBusinessException(ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getDesc();
    }

    public PanBusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public PanBusinessException(String message) {
        this.code = ResponseCode.ERROR_PARAM.getCode();
        this.message = message;
    }

    public PanBusinessException() {
        this.code = ResponseCode.ERROR_PARAM.getCode();
        this.message = ResponseCode.ERROR_PARAM.getDesc();
    }

}
