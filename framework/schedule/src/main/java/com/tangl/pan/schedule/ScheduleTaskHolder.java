package com.tangl.pan.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.concurrent.ScheduledFuture;

/**
 * @author tangl
 * @description 定时任务和定时任务结果的缓存对象
 * @create 2023-07-25 21:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleTaskHolder implements Serializable {

    private static final long serialVersionUID = -6261180275360645319L;

    /**
     * 执行任务实体
     */
    private ScheduleTask scheduleTask;

    /**
     * 执行任务的结果实体
     */
    private ScheduledFuture<?> scheduledFuture;
}
