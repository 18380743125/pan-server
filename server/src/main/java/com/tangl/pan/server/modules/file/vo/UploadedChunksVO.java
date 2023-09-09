package com.tangl.pan.server.modules.file.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author tangl
 * @description 查询用户已上传的分片列表响应实体
 * @create 2023-09-07 11:30
 */
@ApiModel("查询用户已上传的分片列表响应实体")
@Data
public class UploadedChunksVO implements Serializable {

    private static final long serialVersionUID = -6740426768645275106L;

    @ApiModelProperty("已上传的分片编号列表")
    private List<Integer> uploadedChunks;
}
