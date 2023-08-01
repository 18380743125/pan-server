package com.tangl.pan.server.modules.user.context;

import com.tangl.pan.server.modules.user.entity.TPanUser;
import lombok.Data;

import java.io.Serializable;

/**
 * @author tangl
 * @description 用户登录业务的上下文实体对象
 * @create 2023-07-28 21:41
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
    private TPanUser entity;

    /**
     * 登录成功之后的凭证信息
     */
    private String accessToken;
}
