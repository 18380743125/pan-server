package com.tangl.pan.server.common.listener;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author tangl
 * @description 项目启动成功日志打印监听器
 * @create 2023-06-23 16:39
 */
@Component
@Log4j2
public class StartedListener implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * 项目启动成功将在控制台打印
     *
     * @param applicationReadyEvent 启动事件对象
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        String port = applicationReadyEvent.getApplicationContext().getEnvironment().getProperty("server.port");
        String url = String.format("http://%s:%s", "127.0.0.1", port);
        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, "t pan server at：" + url));
        if (checkShowServerDoc(applicationReadyEvent.getApplicationContext())) {
            log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, "t pan server's doc started at：", url, "/doc.html"));
        }
        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, "t pan server has started successfully！"));
    }

    /**
     * 校验是否开启了接口文档
     * @param applicationContext 启动事件对象
     * @return boolean
     */
    private boolean checkShowServerDoc(ConfigurableApplicationContext applicationContext) {
        return applicationContext.getEnvironment().getProperty("swagger2.show", Boolean.class, true)
                && applicationContext.containsBean("swagger2Config");
    }
}
