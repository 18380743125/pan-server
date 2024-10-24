package com.tangl.pan.swagger2;

import com.tangl.pan.core.constants.PanConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * swagger2 配置属性类
 */
@Data
@Component
@ConfigurationProperties(value = "swagger2")
public class Swagger2ConfigProperties {

    private boolean show = true;

    private String groupName = "t-pan";

    private String basePackage = PanConstants.BASE_COMPONENT_SCAN_PATH;

    private String title = "t-pan-server";

    private String description = "t-pan-server";

    private String termsOfServiceURL = "http://127.0.0.1:${server.port}";

    private String contactName = "tangl";

    private String contactUrl = "t-bright.top";

    private String contactEmail = "tl-bright@163.com";

    private String version = "1.0";
}
