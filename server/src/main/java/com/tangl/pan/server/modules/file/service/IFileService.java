package com.tangl.pan.server.modules.file.service;

import com.tangl.pan.server.modules.file.context.FileChunkMergeAndSaveContext;
import com.tangl.pan.server.modules.file.context.FileChunkSaveContext;
import com.tangl.pan.server.modules.file.context.FileSaveContext;
import com.tangl.pan.server.modules.file.context.FileUploadContext;
import com.tangl.pan.server.modules.file.entity.TPanFile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author 25050
 * @description t_pan_file 的数据库操作 Service
 * @createDate 2023-07-23 23:41:43
 */
public interface IFileService extends IService<TPanFile> {

    /**
     * 文件保存的上下文实体
     *
     * @param context 单文件上传的上下文实体
     */
    void saveFile(FileSaveContext context);

    /**
     * 文件物理合并并保存文件实体记录
     *
     * @param context 文件物理合并并保存文件实体记录的上下文实体
     */
    void mergeFileChunkAndSaveFile(FileChunkMergeAndSaveContext context);
}
