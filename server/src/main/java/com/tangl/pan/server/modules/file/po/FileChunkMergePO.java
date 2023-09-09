package com.tangl.pan.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author tangl
 * @description 文件分片合并参数对象
 * @create 2023-09-07 21:55
 */
@ApiModel(value = "文件分片合并参数对象")
@Data
public class FileChunkMergePO implements Serializable {

    private static final long serialVersionUID = 1427281811138347320L;

    @ApiModelProperty(value = "文件名称", required = true)
    @NotBlank(message = "文件名称不能为空")
    private String filename;

    @ApiModelProperty(value = "文件唯一标识", required = true)
    @NotBlank(message = "文件唯一标识不能为空")
    private String identifier;

    @ApiModelProperty(value = "文件总大小", required = true)
    @NotNull(message = "文件总大小不能为空")
    private Long totalSize;

    @ApiModelProperty(value = "文件的父文件夹ID", required = true)
    @NotBlank(message = "文件的父文件夹不能为空")
    private String parentId;
}
