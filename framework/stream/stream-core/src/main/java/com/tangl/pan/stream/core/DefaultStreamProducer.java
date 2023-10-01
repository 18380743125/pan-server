package com.tangl.pan.stream.core;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * @author tangl
 * @description 默认的消息发送实体
 * @create 2023-09-24 18:08
 */
@Component(value = "defaultStreamProducer")
public class DefaultStreamProducer extends AbstractStreamProducer {

    /**
     * 发送消息的前置钩子函数
     *
     * @param message message
     */
    @Override
    protected void preSend(Message<Object> message) {

    }

    /**
     * 发送消息的后置钩子函数
     *
     * @param message message
     * @param result  result
     */
    @Override
    protected void afterSend(Message<Object> message, boolean result) {

    }

}
