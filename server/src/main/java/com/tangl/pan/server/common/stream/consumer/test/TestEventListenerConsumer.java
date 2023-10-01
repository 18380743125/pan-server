package com.tangl.pan.server.common.stream.consumer.test;

import com.tangl.pan.server.common.stream.channel.PanChannels;
import com.tangl.pan.server.common.stream.event.test.TestEvent;
import com.tangl.pan.stream.core.AbstractConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * 测试事件处理器
 */
@Component
@Slf4j
public class TestEventListenerConsumer<T> extends AbstractConsumer {

    /**
     * 消费测试消息
     *
     * @param message 测试事件对象
     */
    @StreamListener(PanChannels.TEST_INPUT)
    public void consumeTestMessage(Message<TestEvent> message) {
        printLog(message);
    }
}