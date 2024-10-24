package com.tangl.pan.server.modules.user.context;

import lombok.Data;

import java.io.Serializable;

/**
 * 重置用户密码的上下文信息实体
 */
@Data
public class ResetPasswordContext implements Serializable {

    private static final long serialVersionUID = -8764619012201484865L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 提交重置密码的token
     */
    private String token;
}
