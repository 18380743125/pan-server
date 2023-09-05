package com.tangl.pan.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author tangl
 * @description 文件秒传参数实体
 * @create 2023-08-13 21:52
 */
@ApiModel("文件秒传参数实体")
@Data
public class SecUploadPO implements Serializable {

    private static final long serialVersionUID = 2539125201857072608L;

    @ApiModelProperty(value = "文件夹ID", required = true)
    @NotBlank(message = "父文件夹ID不能为空")
    private String parentId;

    @ApiModelProperty(value = "文件名称", required = true)
    @NotBlank(message = "文件名称不能为空")
    private String filename;

    @ApiModelProperty(value = "文件唯一标识", required = true)
    @NotBlank(message = "唯一标识不能为空")
    private String identifier;
}
