package com.tangl.pan.storage.engine.local.config;

import com.tangl.pan.core.utils.FileUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author tangl
 * @description
 * @create 2023-08-15 22:04
 */
@Component
@ConfigurationProperties("com.tangl.pan.storage.engine.local")
@Data
public class LocalStorageEngineConfig {

    /**
     * 实际存放路径的前缀
     */
    private String rootFilePath = FileUtil.generateDefaultStoreFileRealPath();
}
