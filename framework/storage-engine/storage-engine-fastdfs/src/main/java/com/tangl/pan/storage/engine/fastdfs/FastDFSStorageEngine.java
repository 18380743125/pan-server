package com.tangl.pan.storage.engine.fastdfs;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tangl.pan.core.constants.PanConstants;
import com.tangl.pan.core.exception.PanFrameworkException;
import com.tangl.pan.core.utils.FileUtil;
import com.tangl.pan.storage.engine.core.AbstractStorageEngine;
import com.tangl.pan.storage.engine.core.context.*;
import com.tangl.pan.storage.engine.fastdfs.config.FastDFSStorageEngineConfig;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * 基于 FastDFS 实现的文件存储引擎实现类
 */
@Component
public class FastDFSStorageEngine extends AbstractStorageEngine {

    @Autowired
    private FastFileStorageClient client;

    @Autowired
    private FastDFSStorageEngineConfig config;

    @Override
    protected void doStore(StoreFileContext context) throws IOException {
        StorePath storePath = client.uploadFile(config.getGroup(),
                context.getInputStream(),
                context.getTotalSize(),
                FileUtil.getFileSuffix(context.getFilename(), false));
        context.setRealPath(storePath.getFullPath());
    }

    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {
        List<String> realFilePathList = context.getRealFilePathList();
        if (CollectionUtils.isNotEmpty(realFilePathList)) {
            realFilePathList.forEach(client::deleteFile);
        }
    }

    @Override
    protected void doStoreChunk(StoreFileChunkContext context) {
        throw new PanFrameworkException("FastDFS不支持文件分片上传的操作");
    }

    @Override
    protected void doMergeFile(MergeFileContext context) throws IOException {
        throw new PanFrameworkException("FastDFS不支持文件分片上传的操作");
    }

    @Override
    protected void doReadFile(ReadFileContext context) throws IOException {
        String readPath = context.getReadPath();
        String group = readPath.substring(PanConstants.ZERO_INT, readPath.indexOf(PanConstants.SLASH_STR));
        String path = readPath.substring(readPath.indexOf(PanConstants.SLASH_STR) + PanConstants.ONE_INT);
        DownloadByteArray downloadByteArray = new DownloadByteArray();
        byte[] bytes = client.downloadFile(group, path, downloadByteArray);
        OutputStream outputStream = context.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }
}
