package com.tangl.pan.server.modules.file.service;

import com.tangl.pan.server.modules.file.context.FileChunkSaveContext;
import com.tangl.pan.server.modules.file.entity.TPanFileChunk;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author 25050
 * @description t_pan_file_chunk 的数据库操作 Service
 * @createDate 2023-07-23 23:41:43
 */
public interface IFileChunkService extends IService<TPanFileChunk> {

    /**
     * 文件分片保存
     *
     * @param fileChunkSaveContext 文件分片保存上下文实体
     */
    void saveChunkFile(FileChunkSaveContext fileChunkSaveContext);
}
