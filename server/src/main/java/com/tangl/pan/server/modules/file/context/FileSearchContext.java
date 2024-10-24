package com.tangl.pan.server.modules.file.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文件搜索上下文实体
 */
@Data
public class FileSearchContext implements Serializable {

    private static final long serialVersionUID = -122878690919847847L;

    /**
     * 搜索的关键字
     */
    private String keyword;

    /**
     * 搜索的文件类型，多个使用通用的分隔符分隔
     */
    private List<Integer> fileTypesArray;

    /**
     * 当前的登录用户ID
     */
    private Long userId;
}
