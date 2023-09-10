package com.tangl.pan.server.modules.file.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tangl.pan.server.modules.file.entity.TPanUserFile;
import com.tangl.pan.web.serializer.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author tangl
 * @description
 * @create 2023-09-10 11:21
 */
@ApiModel("面包屑列表展示实体")
@Data
public class BreadcrumbsVO implements Serializable {

    private static final long serialVersionUID = -1308094213684850860L;

    @ApiModelProperty("文件ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long id;

    @ApiModelProperty("父文件夹ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    @ApiModelProperty("文件夹名称")
    private String name;

    /**
     * 实体转换
     *
     * @param record TPanUserFile
     * @return BreadcrumbsVO
     */
    public static BreadcrumbsVO transfer(TPanUserFile record) {
        BreadcrumbsVO vo = new BreadcrumbsVO();
        if (Objects.nonNull(record)) {
            vo.setId(record.getFileId());
            vo.setParentId(record.getParentId());
            vo.setName(record.getFilename());
        }
        return vo;
    }
}
