package com.tangl.pan.server.modules.file.context;

import com.tangl.pan.server.modules.file.entity.TPanUserFile;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文件复制操作的上下文实体
 */
@Data
public class CopyFileContext implements Serializable {

    private static final long serialVersionUID = -4713152717819437113L;

    /**
     * 要复制的文件 ID 集合
     */
    private List<Long> fileIdList;

    /**
     * 目标文件夹ID
     */
    private Long targetParentId;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 要复制的文件列表
     */

    private List<TPanUserFile> prepareRecords;
}
