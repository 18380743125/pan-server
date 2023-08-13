package com.tangl.pan.server.modules.file.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author tangl
 * @description 批量删除上下文实体对象
 * @create 2023-08-13 16:47
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
