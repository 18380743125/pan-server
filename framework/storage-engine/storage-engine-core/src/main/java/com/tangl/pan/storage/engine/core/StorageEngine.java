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
     * @param context 存储物理文件的上下文实体
     * @throws IOException 抛出 IO 异常，业务层去捕获
     */
    void store(StoreFileContext context) throws IOException;

    /**
     * 删除物理文件
     *
     * @param context 删除物理文件的上下文实体
     * @throws IOException 抛出 IO 异常，业务层去捕获
     */
    void delete(DeleteFileContext context) throws IOException;

    /**
     * 存储物理文件的分片
     *
     * @param context 存储物理文件分片的上下文实体
     * @throws IOException 抛出 IO 异常，业务层去捕获
     */
    void storeChunk(StoreFileChunkContext context) throws IOException;

    /**
     * 合并文件分片
     *
     * @param context 上下文实体
     * @throws IOException 抛出 IO 异常，业务层去捕获
     */
    void mergeFile(MergeFileContext context) throws IOException;

    /**
     * 读取文件内容写入到输出流中
     *
     * @param context 上下文实体
     * @throws IOException 抛出 IO 异常，业务层去捕获
     */
    void readFile(ReadFileContext context) throws IOException;
}
