package com.tangl.pan.server.modules.file.context;

import com.tangl.pan.server.modules.file.entity.TPanFile;
import lombok.Data;

import java.io.Serializable;

/**
 * @author tangl
 * @description 文件分片物理合并并保存文件记录上下文实体
 * @create 2023-09-09 9:10
 */
@Data
public class FileChunkMergeAndSaveContext implements Serializable {

    private static final long serialVersionUID = -2710520384419540366L;

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
     * 文件的父文件夹ID
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

    /**
     * 合并分片后物理文件存放路径
     */
    private String realPath;
}
