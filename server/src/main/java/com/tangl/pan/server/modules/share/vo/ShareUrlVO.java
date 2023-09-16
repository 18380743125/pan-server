package com.tangl.pan.server.modules.share.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tangl.pan.web.serializer.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author tangl
 * @description 创建分享链接的返回实体
 * @create 2023-09-16 16:07
 */
@ApiModel(value = "创建分享链接的返回实体")
@Data
public class ShareUrlVO implements Serializable {

    private static final long serialVersionUID = 7079719297507459103L;

    @ApiModelProperty("分享链接的ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long shareId;

    @ApiModelProperty("分享的名称")
    private String shareName;

    @ApiModelProperty("分享链接")
    private String shareUrl;

    @ApiModelProperty("分享码")
    private String shareCode;

    @ApiModelProperty("分享状态")
    private Integer shareStatus;
}
