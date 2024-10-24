package com.tangl.pan.server.modules.share.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tangl.pan.web.serializer.Date2StringSerializer;
import com.tangl.pan.web.serializer.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel("分享链接列表实体")
@Data
public class ShareUrlListVO implements Serializable {

    private static final long serialVersionUID = 5442687676078158664L;

    @ApiModelProperty("分享链接的ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long shareId;

    @ApiModelProperty("分享的名称")
    private String shareName;

    @ApiModelProperty("分享的链接")
    private String shareUrl;

    @ApiModelProperty("分享码")
    private String shareCode;

    @ApiModelProperty("分享的状态")
    private Integer shareStatus;

    @ApiModelProperty("分享的类型")
    private Integer shareType;

    @ApiModelProperty("分享的结束时间")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date shareEndTime;

    @ApiModelProperty("分享的创建时间")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date createTime;
}
