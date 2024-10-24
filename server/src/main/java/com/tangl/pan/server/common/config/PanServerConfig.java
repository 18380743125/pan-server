package com.tangl.pan.server.common.config;

import com.tangl.pan.core.constants.PanConstants;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(value = "com.tangl.pan.server")
public class PanServerConfig {

    @Value("${server.port}")
    private Integer serverPort;

    /**
     * 文件分片的过期天数
     */
    private Integer chunkFileExpirationDays = PanConstants.ONE_INT;

    /**
     * 分享链接的前缀
     */
    private String sharePrefix = "http://127.0.0.1:8080" + "/share/";
}
