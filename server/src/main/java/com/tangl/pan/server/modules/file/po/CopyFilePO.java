package com.tangl.pan.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@ApiModel("文件复制参数实体对象")
@Data
public class CopyFilePO implements Serializable {

    private static final long serialVersionUID = 8666690054978779412L;

    @ApiModelProperty(value = "要复制的文件ID集合, 多个使用通用分隔符分隔", required = true)
    @NotBlank(message = "请选择要复制的文件")
    private String fileIds;

    @ApiModelProperty(value = "要复制到的目标文件夹ID", required = true)
    @NotBlank(message = "请选择要复制到哪个文件夹下")
    private String targetParentId;
}
