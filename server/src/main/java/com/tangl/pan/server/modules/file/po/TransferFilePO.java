package com.tangl.pan.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author tangl
 * @description 文件转移参数实体对象
 * @create 2023-09-09 22:17
 */
@ApiModel("文件转移参数实体对象")
@Data
public class TransferFilePO implements Serializable {

    private static final long serialVersionUID = 3388482698210978764L;

    @ApiModelProperty(value = "要转移的文件ID集合, 多个使用通用分隔符分隔", required = true)
    @NotBlank(message = "请选择要转移的文件")
    private String fileIds;

    @ApiModelProperty(value = "要转移到的目标文件夹ID", required = true)
    @NotBlank(message = "请选择要转移到哪个文件夹下")
    private String targetParentId;
}
