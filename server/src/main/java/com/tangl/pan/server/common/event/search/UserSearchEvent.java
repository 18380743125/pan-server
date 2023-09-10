package com.tangl.pan.server.common.event.search;

import lombok.*;
import org.springframework.context.ApplicationEvent;

/**
 * @author tangl
 * @description 用户搜索文件事件
 * @create 2023-09-10 10:25
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class UserSearchEvent extends ApplicationEvent {

    private static final long serialVersionUID = -8123654195020656180L;

    private String keyword;

    private Long userId;

    public UserSearchEvent(Object source, String keyword, Long userId) {
        super(source);
        this.keyword = keyword;
        this.userId = userId;
    }
}
