package com.tangl.pan.storage.engine.fastdfs;

import com.tangl.pan.storage.engine.core.AbstractStorageEngine;
import com.tangl.pan.storage.engine.core.context.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author tangl
 * @description 基于 FastDFS 实现的文件存储引擎实现类
 * @create 2023-08-14 21:44
 */
@Component
public class FastDFSStorageEngine extends AbstractStorageEngine {
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
