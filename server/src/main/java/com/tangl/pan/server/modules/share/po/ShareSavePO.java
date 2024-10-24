package com.tangl.pan.server.modules.share.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@ApiModel(value = "保存到我的网盘参数实体")
@Data
public class ShareSavePO implements Serializable {

    private static final long serialVersionUID = 1373580074554829294L;

    @ApiModelProperty(value = "要转存的ID集合，多个使用公用的分隔符分隔", required = true)
    @NotBlank(message = "请选择要保存的文件")
    private String fileIds;

    @ApiModelProperty(value = "要转存文件夹ID")
    @NotBlank(message = "请选择要转存的文件夹")
    private String targetParentId;
}
