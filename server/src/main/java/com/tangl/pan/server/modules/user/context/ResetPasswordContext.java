package com.tangl.pan.server.modules.user.context;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author tangl
 * @description 重置用户密码的上下文信息实体
 * @create 2023-08-01 11:19
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
