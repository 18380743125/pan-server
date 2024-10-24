package com.tangl.pan.server.common.stream.consumer.log;

import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.server.common.stream.channel.PanChannels;
import com.tangl.pan.server.common.stream.event.log.ErrorLogEvent;
import com.tangl.pan.server.modules.log.entity.PanErrorLog;
import com.tangl.pan.server.modules.log.service.IErrorLogService;
import com.tangl.pan.stream.core.AbstractConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 系统错误日志监听器
 */
@Component
public class ErrorLogEventConsumer extends AbstractConsumer {

    @Autowired
    private IErrorLogService iErrorLogService;

    /**
     * 监听系统错误日志事件，并保存到数据库中
     *
     * @param message 错误日志消息对象
     */
    @StreamListener(PanChannels.ERROR_LOG_INPUT)
    @Async(value = "eventListenerTaskExecutor")
    public void saveErrorLog(Message<ErrorLogEvent> message) {
        if (isEmptyMessage(message)) {
            return;
        }
        printLog(message);
        ErrorLogEvent event = message.getPayload();
        PanErrorLog record = new PanErrorLog();
        record.setId(IdUtil.get());
        record.setLogContent(event.getErrorMsg());
        record.setLogStatus(0);
        record.setCreateUser(event.getUserId());
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        iErrorLogService.save(record);
    }
}
