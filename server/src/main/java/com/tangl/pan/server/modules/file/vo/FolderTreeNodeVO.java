package com.tangl.pan.server.modules.file.vo;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tangl.pan.web.serializer.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("文件夹树节点实体")
@Data
public class FolderTreeNodeVO implements Serializable {


    private static final long serialVersionUID = 3816020987221460695L;

    @ApiModelProperty("文件夹名称")
    private String label;

    @ApiModelProperty("文件ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long id;

    @ApiModelProperty("父文件ID")
    private Long parentId;

    @ApiModelProperty("子节点集合")
    private List<FolderTreeNodeVO> children;

    public void print() {
        System.out.println(JSON.toJSONString(this));
    }
}
