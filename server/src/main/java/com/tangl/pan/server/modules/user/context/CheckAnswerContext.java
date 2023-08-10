package com.tangl.pan.server.modules.user.context;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author tangl
 * @description 校验密保答案的上下文对象
 * @create 2023-07-31 23:37
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
