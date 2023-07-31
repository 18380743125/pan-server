package com.tangl.pan.schedule;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author tangl
 * @description 定时模块配置类 - 配置定时器执行器
 * @create 2023-07-25 21:04
 */
@SpringBootConfiguration
public class ScheduleConfig {
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}
