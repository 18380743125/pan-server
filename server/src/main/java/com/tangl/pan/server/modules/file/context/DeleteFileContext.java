package com.tangl.pan.server.modules.file.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量删除上下文实体对象
 */
@Data
public class DeleteFileContext implements Serializable {
    private static final long serialVersionUID = -7373609668209946736L;

    /**
     * 删除的文件 ID 集合
     */
    private List<Long> fileIdList;

    /**
     * 当前登录用户的 ID
     */
    private Long userId;
}
