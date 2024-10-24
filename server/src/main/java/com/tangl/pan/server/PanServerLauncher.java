package com.tangl.pan.server;

import com.tangl.pan.core.constants.PanConstants;
import com.tangl.pan.server.common.stream.channel.PanChannels;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 服务启动类
 */
@SpringBootApplication(scanBasePackages = PanConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = PanConstants.BASE_COMPONENT_SCAN_PATH)
@MapperScan(basePackages = PanConstants.BASE_COMPONENT_SCAN_PATH + ".server.modules.**.mapper")
@EnableTransactionManagement
@EnableAsync
@EnableBinding(PanChannels.class)
public class PanServerLauncher {
    public static void main(String[] args) {
        SpringApplication.run(PanServerLauncher.class);
    }
}
