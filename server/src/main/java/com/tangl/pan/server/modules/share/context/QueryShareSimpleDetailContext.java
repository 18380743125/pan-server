package com.tangl.pan.server.modules.share.context;

import com.tangl.pan.server.modules.share.entity.TPanShare;
import com.tangl.pan.server.modules.share.vo.ShareSimpleDetailVO;
import lombok.Data;

import java.io.Serializable;

/**
 * @author tangl
 * @description 查询分享的简单详情的上下文实体
 * @create 2023-09-17 11:27
 */
@Data
public class QueryShareSimpleDetailContext implements Serializable {

    private static final long serialVersionUID = 3263011156183483224L;

    /**
     * 分享的ID
     */
    private Long shareId;

    /**
     * 分享的实体信息
     */
    private TPanShare record;

    /**
     * 分享简单详情的 VO
     */
    public ShareSimpleDetailVO vo;
}
