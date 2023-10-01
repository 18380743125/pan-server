package com.tangl.pan.server.common.stream.event.test;

import lombok.*;

import java.io.Serializable;

/**
 * @author tangl
 * @description 测试事件实体
 * @create 2023-09-17 18:41
 */
@Data
public class TestEvent implements Serializable {

    private static final long serialVersionUID = -5343165584612886129L;

    private String message;

}

