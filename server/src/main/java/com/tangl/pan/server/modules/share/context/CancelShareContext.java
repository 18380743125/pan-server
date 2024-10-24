package com.tangl.pan.server.modules.share.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 取消分享的上下文实体
 */
@Data
public class CancelShareContext implements Serializable {

    private static final long serialVersionUID = 6633777554940189050L;

    /**
     * 当前要取消分享的ID集合
     */
    private List<Long> shareIdList;

    /**
     * 当前的登录用户ID
     */
    private Long userId;
}
