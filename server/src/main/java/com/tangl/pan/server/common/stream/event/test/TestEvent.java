package com.tangl.pan.server.common.stream.event.test;

import lombok.*;

import java.io.Serializable;

/**
 * 测试事件实体
 */
@Data
public class TestEvent implements Serializable {

    private static final long serialVersionUID = -5343165584612886129L;

    private String message;

}

