package com.tangl.pan.server.modules.user.context;

import lombok.Data;

import java.io.Serializable;

/**
 * 校验密保答案的上下文对象
 */
@Data
public class CheckAnswerContext implements Serializable {

    private static final long serialVersionUID = -6740912203444332708L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密保问题
     */
    private String question;

    /**
     * 密保答案
     */
    private String answer;
}
