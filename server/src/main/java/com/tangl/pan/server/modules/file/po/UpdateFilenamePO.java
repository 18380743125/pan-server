package com.tangl.pan.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author tangl
 * @description 文件重命名参数对象
 * @create 2023-08-13 11:53
 */
@ApiModel("文件重命名参数对象")
@Data
public class UpdateFilenamePO implements Serializable {
    private static final long serialVersionUID = 3996294553564918206L;

    @ApiModelProperty(value = "更新的文件ID", required = true)
    @NotBlank(message = "更新的文件ID不能为空")
    private String fileId;

    @ApiModelProperty(value = "更新的文件名称", required = true)
    @NotBlank(message = "更新的文件名称不能为空")
    private String newFilename;
}
