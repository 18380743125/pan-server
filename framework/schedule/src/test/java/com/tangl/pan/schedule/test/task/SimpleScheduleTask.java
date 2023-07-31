package com.tangl.pan.schedule.test.task;

import com.tangl.pan.schedule.ScheduleTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author tangl
 * @description
 * @create 2023-07-25 21:43
 */
@Component
@Slf4j
public class SimpleScheduleTask implements ScheduleTask {

    @Override
    public String getName() {
        return "测试定时任务";
    }

    @Override
    public void run() {
        log.info(getName() + "正在执行...");
    }
}
