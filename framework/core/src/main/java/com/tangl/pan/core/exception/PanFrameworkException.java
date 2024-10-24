package com.tangl.pan.core.exception;

/**
 * 技术组件层面上的异常类
 */
public class PanFrameworkException extends RuntimeException {

    private static final long serialVersionUID = -4494770315484452081L;

    public PanFrameworkException(String message) {
        super(message);
    }
}
