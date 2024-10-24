package com.tangl.pan.server.modules.share.context;

import com.tangl.pan.server.modules.share.entity.PanShare;
import lombok.Data;

import java.io.Serializable;

/**
 * 获取下一级文件列表的上下文实体
 */
@Data
public class QueryChildFileListContext implements Serializable {

    private static final long serialVersionUID = -4549128096455295557L;

    /**
     * 分享的ID
     */
    private Long shareId;

    /**
     * 父文件夹的ID
     */
    private Long parentId;

    /**
     * 分享的实体
     */
    private PanShare record;
}
