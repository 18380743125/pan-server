package com.tangl.pan.schedule.test;

import com.tangl.pan.schedule.ScheduleManager;
import com.tangl.pan.schedule.test.config.ScheduleTestConfig;
import com.tangl.pan.schedule.test.task.SimpleScheduleTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author tangl
 * @description 定时任务的单元测试
 * @create 2023-07-25 21:45
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ScheduleTestConfig.class)
public class ScheduleTaskTest {
    @Autowired
    private ScheduleManager manager;

    @Autowired
    SimpleScheduleTask scheduleTask;

    @Test
    public void testRunSchedule() throws InterruptedException {
        String cron = "0/5 * * * * ? ";
        String key = manager.startTask(scheduleTask, cron);
        Thread.sleep(10000);

        cron = "0/1 * * * * ? ";
        key = manager.changeSchedule(key, cron);
        Thread.sleep(10000);
        manager.stopTask(key);
    }
}
