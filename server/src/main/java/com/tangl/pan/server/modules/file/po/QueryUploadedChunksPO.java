package com.tangl.pan.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@ApiModel("查询用户已上传的文件分片列表参数实体")
public class QueryUploadedChunksPO implements Serializable {

    private static final long serialVersionUID = 4794027706833131087L;

    @ApiModelProperty(value = "文件的唯一标识", required = true)
    @NotBlank(message = "文件唯一标识不能为空")
    private String identifier;
}
