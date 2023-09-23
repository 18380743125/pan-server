package com.tangl.pan.storage.engine.core.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author tangl
 * @description 合并文件分片的上下文实体
 * @create 2023-09-09 9:35
 */
@Data
public class MergeFileContext implements Serializable {

    private static final long serialVersionUID = 8049512816864320970L;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 当前登录用户ID
     */
    private Long userId;

    /**
     * 文件分片的真实存储路径集合
     */
    private List<String> realPathList;

    /**
     * 文件合并后的真实存储路径
     */
    private String realPath;
}
