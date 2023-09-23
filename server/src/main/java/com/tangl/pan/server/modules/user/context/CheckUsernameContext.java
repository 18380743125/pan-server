package com.tangl.pan.server.modules.user.context;

import lombok.Data;

import java.io.Serializable;

/**
 * @author tangl
 * @description 用户忘记密码-校验用户名的上下文实体
 * @create 2023-07-31 23:09
 */
@Data
public class CheckUsernameContext implements Serializable {

    private static final long serialVersionUID = 364700909418572357L;

    /**
     * 用户名
     */
    private String username;
}
