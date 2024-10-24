package com.tangl.pan.server.modules.share.context;

import com.tangl.pan.server.modules.share.entity.PanShare;
import lombok.Data;

import java.io.Serializable;

/**
 * 校验分享码的上下文实体
 */
@Data
public class CheckShareCodeContext implements Serializable {

    private static final long serialVersionUID = -4991684217842506672L;

    /**
     * 分享ID
     */
    private Long shareId;

    /**
     * 分享码
     */
    private String shareCode;

    /**
     * 分享的实体记录
     */
    private PanShare record;
}
