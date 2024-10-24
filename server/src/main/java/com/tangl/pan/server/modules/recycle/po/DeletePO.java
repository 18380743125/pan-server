package com.tangl.pan.server.modules.recycle.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 文件删除参数实体
 */
@ApiModel("文件删除参数实体")
@Data
public class DeletePO implements Serializable {

    private static final long serialVersionUID = 6997062028544388564L;

    @ApiModelProperty(value = "要删除的文件ID集合，多个使用通用分隔符分隔", required = true)
    @NotBlank(message = "请选择要删除的文件")
    private String fileIds;
}
