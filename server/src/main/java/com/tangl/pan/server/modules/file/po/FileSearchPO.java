package com.tangl.pan.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@ApiModel("文件搜索参数实体对象")
@Data
public class FileSearchPO implements Serializable {

    private static final long serialVersionUID = 326366883443197987L;

    @ApiModelProperty(value = "搜索的关键字", required = true)
    @NotBlank(message = "搜索关键字不能为空")
    private String keyword;

    @ApiModelProperty(value = "搜索的文件类型，多个类型使用通用分隔符分隔")
    private String fileTypes;
}
