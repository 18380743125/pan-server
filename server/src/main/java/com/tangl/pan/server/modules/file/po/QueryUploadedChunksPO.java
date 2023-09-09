package com.tangl.pan.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author tangl
 * @description 查询用户已上传的分片列表参数实体
 * @create 2023-09-07 11:25
 */
@ApiModel("查询用户已上传的分片列表参数实体")
public class QueryUploadedChunksPO implements Serializable {

    private static final long serialVersionUID = 4794027706833131087L;

    @ApiModelProperty(value = "文件的唯一标识", required = true)
    @NotBlank(message = "文件唯一标识不能为空")
    private String identifier;
}
