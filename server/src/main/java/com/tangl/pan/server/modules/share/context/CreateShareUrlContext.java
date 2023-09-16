package com.tangl.pan.server.modules.share.context;

import com.tangl.pan.server.modules.share.entity.TPanShare;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author tangl
 * @description 创建分享链接的上下文实体
 * @create 2023-09-16 16:19
 */
@Data
public class CreateShareUrlContext implements Serializable {

    private static final long serialVersionUID = 1809265932905500801L;

    /**
     * 分享的名称
     */
    private String shareName;

    /**
     * 分享类型
     */
    private Integer shareType;

    /**
     * 分享天数类型
     */
    private Integer shareDayType;

    /**
     * 分享的文件 ID 集合
     */
    private List<Long> shareFileIdList;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 分享实体记录
     */
    private TPanShare record;
}
