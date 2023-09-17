package com.tangl.pan.server.modules.share.context;

import com.tangl.pan.server.modules.share.entity.TPanShare;
import com.tangl.pan.server.modules.share.vo.ShareDetailVO;
import lombok.Data;

import java.io.Serializable;

/**
 * @author tangl
 * @description 查询分享详情的上下文实体
 * @create 2023-09-17 10:18
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
    private TPanShare record;

    /**
     * 分享详情的 VO
     */
    private ShareDetailVO vo;
}
