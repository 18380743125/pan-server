package com.tangl.pan.server.common.listener.test;

import com.tangl.pan.server.common.event.test.TestEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 测试事件处理器
 */
@Component
@Slf4j
public class TestEventListener {

    /**
     * 监听测试事件
     *
     * @param event 测试事件对象
     */
    @EventListener(TestEvent.class)
    @Async(value = "eventListenerTaskExecutor")
    public void test(TestEvent event) throws InterruptedException {
        Thread.sleep(2000);
        log.info("TestEventListenerConsumer start process, th thread name is {}", Thread.currentThread().getName());
    }
}