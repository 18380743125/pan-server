package com.tangl.pan.server;

import com.tangl.pan.core.constants.TPanConstants;
import com.tangl.pan.core.response.R;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tangl
 * @description 启动类
 * @create 2023-06-22 18:42
 */
@SpringBootApplication(scanBasePackages = TPanConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = TPanConstants.BASE_COMPONENT_SCAN_PATH)
@RestController
public class TPanServerLauncher {
    public static void main(String[] args) {
        SpringApplication.run(TPanServerLauncher.class);
    }

    @GetMapping("/hello")
    public R<String> hello() {
        return R.success("hello world");
    }
}
