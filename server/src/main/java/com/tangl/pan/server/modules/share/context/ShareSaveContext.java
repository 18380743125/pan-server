package com.tangl.pan.server.modules.share.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 保存到我的网盘上下文实体
 */
@Data
public class ShareSaveContext implements Serializable {

    private static final long serialVersionUID = -3998925396399240484L;

    /**
     * 要保存的文件 ID 列表
     */
    private List<Long> fileIdList;

    /**
     * 目标文件夹 ID
     */
    private Long targetParentId;

    /**
     * 当前登录的用户 ID
     */
    private Long userId;

    /**
     * 分享的 ID
     */
    private Long shareId;
}
