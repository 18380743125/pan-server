package com.tangl.pan.server.modules.user.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author tangl
 * @description 校验用户名称 PO 对象
 * @create 2023-07-31 23:04
 */
@ApiModel(value = "用户忘记密码-校验用户名参数")
@Data
public class CheckUsernamePO implements Serializable {
    private static final long serialVersionUID = -5983489854679236643L;

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名称不能为空")
    @Pattern(regexp = "^[0-9A-Za-z]{6,16}$", message = "请输入6-16位只包含数字字母的用户名")
    private String username;
}
