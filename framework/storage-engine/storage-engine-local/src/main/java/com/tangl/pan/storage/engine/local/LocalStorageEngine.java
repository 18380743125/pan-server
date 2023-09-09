package com.tangl.pan.storage.engine.local;

import com.tangl.pan.core.utils.FileUtil;
import com.tangl.pan.storage.engine.core.AbstractStorageEngine;
import com.tangl.pan.storage.engine.core.context.DeleteFileContext;
import com.tangl.pan.storage.engine.core.context.MergeFileContext;
import com.tangl.pan.storage.engine.core.context.StoreFileChunkContext;
import com.tangl.pan.storage.engine.core.context.StoreFileContext;
import com.tangl.pan.storage.engine.local.config.LocalStorageEngineConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author tangl
 * @description 本地文件存储引擎实现类
 * @create 2023-08-14 21:36
 */
@Component
public class LocalStorageEngine extends AbstractStorageEngine {

    @Autowired
    private LocalStorageEngineConfig config;

    /**
     * 保存物理文件的动作
     *
     * @param context 存储物理文件的上下文实体
     */
    @Override
    protected void doStore(StoreFileContext context) throws IOException {
        String basePath = config.getRootFilePath();
        String realFilePath = FileUtil.generateStoreFileRealPath(basePath, context.getFilename());
        FileUtil.writeStream2File(context.getInputStream(), new File(realFilePath), context.getTotalSize());
        context.setRealPath(realFilePath);
    }

    /**
     * 删除物理文件
     *
     * @param context 删除物理文件的上下文实体
     */
    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {
        FileUtil.deleteFiles(context.getRealFilePathList());
    }

    /**
     * 执行保存文件分片
     *
     * @param context 存储物理文件分片的上下文实体
     */
    @Override
    protected void doStoreChunk(StoreFileChunkContext context) throws IOException {
        String basePath = config.getRootFileChunkPath();
        String realFilePath = FileUtil.generateStoreFileChunkRealPath(basePath, context.getIdentifier(), context.getChunkNumber());
        FileUtil.writeStream2File(context.getInputStream(), new File(realFilePath), context.getTotalSize());
        context.setRealPath(realFilePath);
    }

    /**
     * 合并文件分片
     *
     * @param context 上下文实体
     */
    @Override
    protected void doMergeFile(MergeFileContext context) throws IOException {
        String basePath = config.getRootFilePath();
        String realFilePath = FileUtil.generateStoreFileRealPath(basePath, context.getFilename());
        FileUtil.createFile(new File(realFilePath));
        List<String> chunkPathList = context.getRealPathList();
        for (String chunkPath : chunkPathList) {
            FileUtil.appendWrite(Paths.get(realFilePath), new File(chunkPath).toPath());
        }
        FileUtil.deleteFiles(chunkPathList);
        context.setRealPath(realFilePath);
    }
}
