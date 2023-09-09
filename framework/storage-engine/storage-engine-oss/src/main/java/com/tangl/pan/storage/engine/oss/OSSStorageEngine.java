package com.tangl.pan.storage.engine.oss;

import com.tangl.pan.storage.engine.core.AbstractStorageEngine;
import com.tangl.pan.storage.engine.core.context.*;

import java.io.IOException;

/**
 * @author tangl
 * @description 基于 OSS 的文件存储引擎实现类
 * @create 2023-08-14 21:46
 */
public class OSSStorageEngine extends AbstractStorageEngine {
    @Override
    protected void doStore(StoreFileContext context) throws IOException {

    }

    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {

    }

    @Override
    protected void doStoreChunk(StoreFileChunkContext context) {

    }

    @Override
    protected void doMergeFile(MergeFileContext context) throws IOException {

    }

    @Override
    protected void doReadFile(ReadFileContext context) throws IOException {

    }
}
