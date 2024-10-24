package com.tangl.pan.server.modules.file.context;

import com.tangl.pan.server.modules.file.entity.TPanFile;
import lombok.Data;

import java.io.Serializable;

/**
 * 文件分片合并的上下文实体
 */
@Data
public class FileChunkMergeContext implements Serializable {

    private static final long serialVersionUID = -5772470955693209786L;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 文件总大小
     */
    private Long totalSize;

    /**
     * 父文件夹ID
     */
    private Long parentId;

    /**
     * 当前的登录用户ID
     */
    private Long userId;

    /**
     * 文件实体记录
     */
    private TPanFile record;
}
