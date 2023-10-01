package com.tangl.pan.server;

import com.tangl.pan.core.constants.TPanConstants;
import com.tangl.pan.server.common.stream.channel.PanChannels;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author tangl
 * @description 服务启动类
 * @create 2023-06-22 18:42
 */
@SpringBootApplication(scanBasePackages = TPanConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = TPanConstants.BASE_COMPONENT_SCAN_PATH)
@MapperScan(basePackages = TPanConstants.BASE_COMPONENT_SCAN_PATH + ".server.modules.**.mapper")
@EnableTransactionManagement
@EnableAsync
@EnableBinding(PanChannels.class)
public class TPanServerLauncher {
    public static void main(String[] args) {
        SpringApplication.run(TPanServerLauncher.class);
    }
}
