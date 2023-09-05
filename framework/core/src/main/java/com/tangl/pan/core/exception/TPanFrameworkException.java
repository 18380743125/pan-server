package com.tangl.pan.core.exception;

/**
 * @author tangl
 * @description 技术组件层面上的异常类
 * @create 2023-07-25 21:32
 */
public class TPanFrameworkException extends RuntimeException {

    private static final long serialVersionUID = -4494770315484452081L;

    public TPanFrameworkException(String message) {
        super(message);
    }
}
