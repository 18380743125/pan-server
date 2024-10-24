package com.tangl.pan.storage.engine.local.initializer;

import com.tangl.pan.storage.engine.local.config.LocalStorageEngineConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 初始化文件上传根目录和文件分片根目录的初始化容器
 */
@Component
@Slf4j
public class UploadFolderAndChunkFolderInitializer implements CommandLineRunner {

    @Autowired
    private LocalStorageEngineConfig config;

    @Override
    public void run(String... args) throws Exception {
        FileUtils.forceMkdir(new File(config.getRootFilePath()));
        log.info("the root file path has been created!");
        FileUtils.forceMkdir(new File(config.getRootFileChunkPath()));
        log.info("the root file chunk path has been created!");
    }
}
