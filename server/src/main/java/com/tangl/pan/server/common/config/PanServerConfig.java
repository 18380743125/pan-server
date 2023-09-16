package com.tangl.pan.server.common.config;

import com.tangl.pan.core.constants.TPanConstants;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${server.port}")
    private Integer serverPort;

    /**
     * 文件分片的过期天数
     */
    private Integer chunkFileExpirationDays = TPanConstants.ONE_INT;

    /**
     * 分享链接的前缀
     */
    private String sharePrefix = "http://127.0.0.1:" + serverPort + "/share/";
}
