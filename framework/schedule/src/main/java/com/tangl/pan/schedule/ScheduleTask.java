package com.tangl.pan.schedule;

/**
 * 定时任务的任务接口
 */
public interface ScheduleTask extends Runnable {
    /**
     * 获取定时任务的名称
     *
     * @return 任务名称
     */
    String getName();
}
