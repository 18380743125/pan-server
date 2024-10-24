package com.tangl.pan.server.common.schedule.launcher;

import com.tangl.pan.schedule.ScheduleManager;
import com.tangl.pan.server.common.schedule.task.CleanExpireFileChunkTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 定时清理过期的文件分片任务触发器
 */
@Component
@Slf4j
public class CleanExpireFileChunkTaskLauncher implements CommandLineRunner {
    private static final String CRON = "1 0 0 * * ? ";

    @Autowired
    private CleanExpireFileChunkTask task;

    @Autowired
    private ScheduleManager scheduleManager;

    @Override
    public void run(String... args) throws Exception {
        scheduleManager.startTask(task, CRON);
    }
}
