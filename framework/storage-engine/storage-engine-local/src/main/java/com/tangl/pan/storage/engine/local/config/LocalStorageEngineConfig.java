package com.tangl.pan.storage.engine.local.config;

import com.tangl.pan.core.utils.FileUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 本地文件存储引擎的配置类4
 */
@Component
@ConfigurationProperties("com.tangl.pan.storage.engine.local")
@Data
public class LocalStorageEngineConfig {

    /**
     * 文件物理存放路径的前缀
     */
    private String rootFilePath = FileUtil.generateDefaultStoreFileRealPath();

    /**
     * 分片文件物理存放路径的前缀
     */
    private String rootFileChunkPath = FileUtil.generateDefaultStoreFileChunkRealPath();
}
