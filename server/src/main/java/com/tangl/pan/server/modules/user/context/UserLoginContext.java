package com.tangl.pan.server.modules.user.context;

import com.tangl.pan.server.modules.user.entity.PanUser;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录的上下文实体
 */
@Data
public class UserLoginContext implements Serializable {

    private static final long serialVersionUID = 1259292149677863519L;
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;


    /**
     * 用户实体对象
     */
    private PanUser entity;

    /**
     * 登录成功之后的凭证信息
     */
    private String accessToken;
}
