package com.tangl.pan.server.modules.user.context;

import com.tangl.pan.server.modules.user.entity.TPanUser;
import lombok.Data;

import java.io.Serializable;

/**
 * @author tangl
 * @description 用户在线修改密码的上下文实体
 * @create 2023-08-01 15:36
 */
@Data
public class ChangePasswordContext implements Serializable {

    private static final long serialVersionUID = 7796637164418181887L;

    /**
     * 当前登录的用户 ID
     */
    private Long userId;

    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 当前登录用户的实体信息
     */
    private TPanUser entity;
}
