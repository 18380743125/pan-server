package com.tangl.pan.server.modules.share.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tangl.pan.web.serializer.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author tangl
 * @description 分享者信息的响应实体
 * @create 2023-09-17 10:26
 */
@ApiModel(value = "分享者信息的响应实体")
@Data
public class ShareUserInfoVO implements Serializable {

    private static final long serialVersionUID = -211008797869920024L;

    @ApiModelProperty(value = "分享者的ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long userId;

    @ApiModelProperty(value = "分享者的用户名")
    private String username;
}
