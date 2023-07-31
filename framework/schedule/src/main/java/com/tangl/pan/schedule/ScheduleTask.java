package com.tangl.pan.schedule;

/**
 * @author tangl
 * @description 定时任务的任务接口
 * @create 2023-07-25 21:08
 */
public interface ScheduleTask extends Runnable {
    /**
     * 获取定时任务的名称
     * @return 任务名称
     */
    String getName();
}
