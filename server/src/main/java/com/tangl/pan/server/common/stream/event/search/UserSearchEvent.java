package com.tangl.pan.server.common.stream.event.search;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 用户搜索文件事件
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class UserSearchEvent implements Serializable {

    private static final long serialVersionUID = -8123654195020656180L;

    private String keyword;

    private Long userId;

    public UserSearchEvent(String keyword, Long userId) {
        this.keyword = keyword;
        this.userId = userId;
    }
}
