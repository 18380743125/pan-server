package com.tangl.pan.storage.engine.core.context;

import lombok.Data;

import java.io.InputStream;
import java.io.Serializable;

@Data
public class StoreFileChunkContext implements Serializable {

    private static final long serialVersionUID = 3260028984696882176L;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件的唯一标识
     */
    private String identifier;

    /**
     * 文件的总大小
     */
    private Long totalSize;

    /**
     * 文件输入流
     */
    private InputStream inputStream;

    /**
     * 文件的真实存储路径
     */
    private String realPath;

    /**
     * 文件的总分片数
     */
    private Integer totalChunks;

    /**
     * 当前分片的下标
     */
    private Integer chunkNumber;

    /*
     * 当前分片的大小
     */
    private Long currentChunkSize;

    /**
     * 当前登录用户的ID
     */
    private Long userId;
}
