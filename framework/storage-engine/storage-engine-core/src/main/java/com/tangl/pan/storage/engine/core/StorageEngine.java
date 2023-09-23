package com.tangl.pan.storage.engine.core;

import com.tangl.pan.storage.engine.core.context.*;

import java.io.IOException;

/**
 * @author tangl
 * @description 文件存储引擎的顶级接口
 * @create 2023-08-14 21:27
 */
public interface StorageEngine {

    /**
     * 存储物理文件
     *
     * @param context 上下文实体
     */
    void store(StoreFileContext context) throws IOException;

    /**
     * 删除物理文件
     *
     * @param context 上下文实体
     */
    void delete(DeleteFileContext context) throws IOException;

    /**
     * 存储物理文件的分片
     *
     * @param context 上下文实体
     */
    void storeChunk(StoreFileChunkContext context) throws IOException;

    /**
     * 合并文件分片
     *
     * @param context 上下文实体
     */
    void mergeFile(MergeFileContext context) throws IOException;

    /**
     * 读取文件内容写入到输出流中
     *
     * @param context 上下文实体
     */
    void readFile(ReadFileContext context) throws IOException;
}
