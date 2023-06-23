package com.tangl.pan.swagger2;

import com.tangl.pan.core.constants.TPanConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author tangl
 * @description swagger 配置属性实体
 * @create 2023-06-22 22:47
 */
@Data
@Component
@ConfigurationProperties(prefix = "swagger2")
public class Swagger2ConfigProperties {
    private boolean show = true;

    private String groupName = "t-pan";

    private String basePackage = TPanConstants.BASE_COMPONENT_SCAN_PATH;

    private String title = "t-pan-server";

    private String description = "t-pan-server";

    private String termsOfServiceURL = "http://127.0.0.1:${server.port}";

    private String contactName = "tangl";

    private String contactUrl = "t-bright.top";

    private String contactEmail = "tl-bright@163.com";

    private String version = "1.0";
}
