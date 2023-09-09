package com.tangl.pan.server.modules.file.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author tangl
 * @description
 * @create 2023-09-06 18:02
 */
@ApiModel("文件分片上传的响应实体")
@Data
public class FileChunkUploadVO implements Serializable {

    private static final long serialVersionUID = -4236853976912097487L;

    @ApiModelProperty("是否需要合并文件 0 不需要 1 需要")
    private Integer mergeFlag;
}
