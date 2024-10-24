package com.tangl.pan.server.modules.share.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tangl.pan.web.serializer.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "查询分享简单详情的响应实体")
@Data
public class ShareSimpleDetailVO implements Serializable {

    private static final long serialVersionUID = -1726744011660845775L;

    @ApiModelProperty(value = "分享的ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long shareId;

    @ApiModelProperty(value = "分享的名称")
    private String shareName;

    @ApiModelProperty(value = "分享者信息")
    private ShareUserInfoVO shareUserInfoVO;
}
