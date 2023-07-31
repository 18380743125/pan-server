package com.tangl.pan.schedule.test.config;

import com.tangl.pan.core.constants.TPanConstants;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author tangl
 * @description 单元测试配置类
 * @create 2023-07-25 21:40
 */
@SpringBootConfiguration
@ComponentScan(TPanConstants.BASE_COMPONENT_SCAN_PATH + ".schedule")
public class ScheduleTestConfig {
}
