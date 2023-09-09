package com.tangl.pan.server.common.config;

import com.tangl.pan.core.constants.TPanConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author tangl
 * @description
 * @create 2023-09-07 10:05
 */
@Component
@Data
@ConfigurationProperties(value = "com.tangl.pan.server")
public class PanServerConfig {

    /**
     * 文件分片的过期天数
     */
    private Integer chunkFileExpirationDays = TPanConstants.ONE_INT;
}
