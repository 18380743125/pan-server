package com.tangl.pan.stream.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;

import java.util.Objects;

/**
 * @author tangl
 * @description 消费者的公用父类，抽离公用逻辑
 * @create 2023-09-24 18:11
 */
@Slf4j
public abstract class AbstractConsumer {

    /**
     * 公用的消息打印日志
     *
     * @param message 消息
     */
    protected void printLog(Message<?> message) {
        log.info("{} start consumer the message，the message is {}", this.getClass().getSimpleName(), message);
    }

    protected boolean isEmptyMessage(Message<?> message) {
        return Objects.isNull(message);
    }

}
