package com.tangl.pan.schedule;

import com.tangl.pan.core.exception.PanFrameworkException;
import com.tangl.pan.core.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 定时任务管理器
 * <p>
 * 创建并启动一个定时任务
 * 停止一个定时任务
 * 更新一个定时任务
 */
@Component
@Slf4j
public class ScheduleManager {

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    /**
     * 内部正在执行的定时任务缓存
     */
    private final Map<String, ScheduleTaskHolder> cache = new ConcurrentHashMap<>();

    /**
     * 启动一个定时任务
     *
     * @param scheduleTask 定时任务的实现类
     * @param cron         cron 表达式
     * @return 定时任务的唯一标识
     */
    public String startTask(ScheduleTask scheduleTask, String cron) {
        ScheduledFuture<?> schedule = taskScheduler.schedule(scheduleTask, new CronTrigger(cron));
        String key = UUIDUtil.getUUID();

        ScheduleTaskHolder scheduleTaskHolder = new ScheduleTaskHolder(scheduleTask, schedule);
        cache.put(key, scheduleTaskHolder);

        log.info("{} 启动成功！唯一标识为：{}", scheduleTask.getName(), key);
        return key;
    }

    /**
     * 停止一个定时任务
     *
     * @param key 定时任务的唯一标识
     */
    public void stopTask(String key) {
        if (StringUtils.isBlank(key)) return;

        ScheduleTaskHolder holder = cache.get(key);
        if (Objects.isNull(holder)) return;

        ScheduledFuture<?> future = holder.getScheduledFuture();
        boolean cancel = future.cancel(true);
        cache.remove(key);
        if (cancel) {
            log.info("{} 停止成功！唯一标识为：{}", holder.getScheduleTask().getName(), key);
        } else {
            log.error("{} 停止失败！唯一标识为：{}", holder.getScheduleTask().getName(), key);
        }
    }

    /**
     * 更新一个定时任务的执行时间
     *
     * @param key  定时任务的唯一标识
     * @param cron 新的 cron 表达式
     * @return 定时任务的唯一标识
     */
    public String changeSchedule(String key, String cron) {
        if (StringUtils.isAnyBlank(key, cron)) {
            throw new PanFrameworkException("定时任务的唯一标识和新的执行表达式不能为空");
        }

        ScheduleTaskHolder holder = cache.get(key);
        if (Objects.isNull(holder)) {
            throw new PanFrameworkException(key + "唯一标识不存在");
        }
        stopTask(key);
        return startTask(holder.getScheduleTask(), cron);
    }
}
