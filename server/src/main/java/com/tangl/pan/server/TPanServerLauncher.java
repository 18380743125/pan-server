package com.tangl.pan.server;

import com.tangl.pan.core.constants.TPanConstants;
import com.tangl.pan.core.response.R;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

/**
 * @author tangl
 * @description 启动类
 * @create 2023-06-22 18:42
 */
@SpringBootApplication(scanBasePackages = TPanConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = TPanConstants.BASE_COMPONENT_SCAN_PATH)
@RestController
@Validated
public class TPanServerLauncher {
    public static void main(String[] args) {
        SpringApplication.run(TPanServerLauncher.class);
    }

    @GetMapping("/a")
    public R<String> a(@NotBlank(message = "name 不能为空") String name) {
        return R.success(name + "hahahssssa");
    }
}
