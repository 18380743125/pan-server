package com.tangl.pan.server.modules.share.context;

import lombok.Data;

import java.io.Serializable;

/**
 * @author tangl
 * @description 查询用户已有的分享列表上下文
 * @create 2023-09-16 18:42
 */
@Data
public class QueryShareListContext implements Serializable {

    private static final long serialVersionUID = -8268884894828239588L;

    /**
     * 当前登录的用户 ID
     */
    private Long userId;
}
