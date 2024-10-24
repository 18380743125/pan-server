package com.tangl.pan.server.modules.recycle.context;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询用户回收站文件列表的上下文实体
 */
@Data
public class QueryRecycleFileListContext implements Serializable {

    private static final long serialVersionUID = -7710512992737024927L;

    /**
     * 当前登录用户ID
     */
    private Long userId;

}
