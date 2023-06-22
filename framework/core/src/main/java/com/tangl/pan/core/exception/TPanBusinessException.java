package com.tangl.pan.core.exception;

import com.tangl.pan.core.response.ResponseCode;

/**
 * @author tangl
 * @description 自定义全局业务异常类
 * @create 2023-06-22 15:07
 */
public class TPanBusinessException extends RuntimeException {
    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String message;

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
