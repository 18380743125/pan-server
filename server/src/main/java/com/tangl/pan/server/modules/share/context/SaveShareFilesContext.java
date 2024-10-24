package com.tangl.pan.server.modules.share.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 保存文件和分享的关联关系上下文实体
 */
@Data
public class SaveShareFilesContext implements Serializable {

    private static final long serialVersionUID = -5452565354810017860L;

    /**
     * 分享的 ID
     */
    private Long shareId;

    /**
     * 分享文件的 ID 集合
     */
    private List<Long> shareFileIdList;

    /**
     * 当前的登录用户 ID
     */
    private Long userId;
}
