package com.tangl.pan.server.modules.share.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel(value = "创建分享链接的参数对象")
@Data
public class CreateShareUrlPO implements Serializable {

    private static final long serialVersionUID = 6065403586604801991L;

    @ApiModelProperty(value = "分享的名称", required = true)
    @NotBlank(message = "分享名称不能为空")
    private String shareName;

    @ApiModelProperty(value = "分享类型", required = true)
    @NotNull(message = "分享类型不能为空")
    private Integer shareType;

    @ApiModelProperty(value = "分享天数类型", required = true)
    @NotNull(message = "分享天数类型不能为空")
    private Integer shareDayType;

    @ApiModelProperty(value = "分享的文件 ID 集合，多个使用通用分隔符分隔", required = true)
    @NotBlank(message = "分享的文件 ID 集合不能为空")
    private String shareFileIds;
}
