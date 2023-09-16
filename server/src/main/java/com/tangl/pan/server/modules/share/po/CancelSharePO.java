package com.tangl.pan.server.modules.share.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author tangl
 * @description 取消分享参数实体
 * @create 2023-09-16 19:04
 */
@ApiModel(value = "取消分享参数实体")
@Data
public class CancelSharePO implements Serializable {

    private static final long serialVersionUID = -397208587103435056L;

    @ApiModelProperty(value = "要取消的分享ID集合，多个使用通用的分隔符分隔", required = true)
    @NotBlank(message = "请选择要取消的分享")
    private String shareIds;
}
