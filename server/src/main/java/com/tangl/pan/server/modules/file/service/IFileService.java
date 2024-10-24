package com.tangl.pan.server.modules.file.service;

import com.tangl.pan.server.modules.file.context.FileChunkMergeAndSaveContext;
import com.tangl.pan.server.modules.file.context.FileChunkSaveContext;
import com.tangl.pan.server.modules.file.context.FileSaveContext;
import com.tangl.pan.server.modules.file.context.FileUploadContext;
import com.tangl.pan.server.modules.file.entity.TPanFile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 文件业务层
 */
public interface IFileService extends IService<TPanFile> {

    /**
     * 上传文件并保存实体文件记录
     *
     * @param context 上下文实体
     */
    void saveFile(FileSaveContext context);

    /**
     * 文件物理合并并保存文件实体记录
     *
     * @param context 上下文实体
     */
    void mergeFileChunkAndSaveFile(FileChunkMergeAndSaveContext context);
}
