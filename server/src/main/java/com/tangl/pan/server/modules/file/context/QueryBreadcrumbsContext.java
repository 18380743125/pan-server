package com.tangl.pan.server.modules.file.context;

import lombok.Data;

import java.io.Serializable;

/**
 * 搜索文件面包屑列表的上下文实体
 */
@Data
public class QueryBreadcrumbsContext implements Serializable {

    private static final long serialVersionUID = 8622914951817146748L;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 当前登录用户ID
     */
    private Long userId;
}
