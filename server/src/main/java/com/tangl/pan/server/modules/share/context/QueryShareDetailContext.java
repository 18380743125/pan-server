package com.tangl.pan.server.modules.share.context;

import com.tangl.pan.server.modules.share.entity.PanShare;
import com.tangl.pan.server.modules.share.vo.ShareDetailVO;
import lombok.Data;

import java.io.Serializable;

/**
 * 查询分享详情的上下文实体
 */
@Data
public class QueryShareDetailContext implements Serializable {

    private static final long serialVersionUID = -6792589844938639802L;

    /**
     * 分享 ID
     */
    private Long shareId;

    /**
     * 分享实体
     */
    private PanShare record;

    /**
     * 分享详情的 VO
     */
    private ShareDetailVO vo;
}
