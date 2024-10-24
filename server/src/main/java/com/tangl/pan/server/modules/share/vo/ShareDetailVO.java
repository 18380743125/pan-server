package com.tangl.pan.server.modules.share.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tangl.pan.server.modules.file.vo.UserFileVO;
import com.tangl.pan.web.serializer.Date2StringSerializer;
import com.tangl.pan.web.serializer.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ApiModel(value = "查询分享详情的响应实体")
@Data
public class ShareDetailVO implements Serializable {

    private static final long serialVersionUID = 5074889081158358343L;

    @ApiModelProperty(value = "分享的ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long shareId;

    @ApiModelProperty(value = "分享的名称")
    private String shareName;

    @ApiModelProperty(value = "分享的创建时间")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date createTime;

    @ApiModelProperty(value = "分享的天数")
    private Integer shareDay;

    @ApiModelProperty(value = "分享的截至时间")
    private Date shareEndTime;

    @ApiModelProperty(value = "分享的文件列表")
    private List<UserFileVO> userFileVOList;

    @ApiModelProperty(value = "分享者的用户信息")
    private ShareUserInfoVO shareUserInfoVO;
}
