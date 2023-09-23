package com.tangl.pan.server.common.schedule.launcher;

import com.tangl.pan.schedule.ScheduleManager;
import com.tangl.pan.server.common.schedule.task.RebuildShareSimpleDetailBloomFilterTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author tangl
 * @description 定时重建分享简单详情布隆过滤器任务触发器
 * @create 2023-09-24 0:06
 */
@Component
@Slf4j
public class RebuildShareSimpleDetailBloomFilterTaskLauncher implements CommandLineRunner {
    private static final String CRON = "1 0 0 * * ? ";

    @Autowired
    private RebuildShareSimpleDetailBloomFilterTask task;

    @Autowired
    private ScheduleManager scheduleManager;

    @Override
    public void run(String... args) throws Exception {
        scheduleManager.startTask(task, CRON);
    }
}
