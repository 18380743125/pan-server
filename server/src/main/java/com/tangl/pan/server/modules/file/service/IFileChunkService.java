package com.tangl.pan.server.modules.file.service;

import com.tangl.pan.server.modules.file.context.FileChunkSaveContext;
import com.tangl.pan.server.modules.file.entity.TPanFileChunk;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 文件分片的业务层
 */
public interface IFileChunkService extends IService<TPanFileChunk> {

    /**
     * 文件分片保存
     *
     * @param fileChunkSaveContext 上下文实体
     */
    void saveChunkFile(FileChunkSaveContext fileChunkSaveContext);
}
