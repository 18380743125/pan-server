package com.tangl.pan.server.common.event.test;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

/**
 * @author tangl
 * @description 测试事件实体
 * @create 2023-09-17 18:41
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class TestEvent extends ApplicationEvent {

    private static final long serialVersionUID = -5343165584612886129L;

    private String message;

    public TestEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

}

