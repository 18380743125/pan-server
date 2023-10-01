package com.tangl.pan.server.modules.test;

import com.tangl.pan.core.response.R;
import com.tangl.pan.server.common.annotation.LoginIgnore;
import com.tangl.pan.server.common.event.test.TestEvent;
import com.tangl.pan.server.common.stream.channel.PanChannels;
import com.tangl.pan.stream.core.IStreamProducer;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试模块控制器
 */
@Api(tags = "测试模块")
@RestController
public class TestController implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier(value = "defaultStreamProducer")
    private IStreamProducer producer;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 测试事件发布
     */
    @GetMapping("test")
    @LoginIgnore
    public R<?> test() {
        applicationContext.publishEvent(new TestEvent(this, "test"));
        return R.success();
    }

    /**
     * 测试流事件发布
     */
    @GetMapping("stream/test")
    @LoginIgnore
    public R<?> testStream(String name) {
        com.tangl.pan.server.common.stream.event.test.TestEvent testEvent = new com.tangl.pan.server.common.stream.event.test.TestEvent();
        testEvent.setMessage(name);
        producer.sendMessage(PanChannels.TEST_OUTPUT, testEvent);
        return R.success();
    }
}