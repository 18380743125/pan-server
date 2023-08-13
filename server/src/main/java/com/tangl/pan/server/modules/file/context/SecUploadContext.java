package com.tangl.pan.server.modules.file.context;

import lombok.Data;

import java.io.Serializable;

/**
 * @author tangl
 * @description
 * @create 2023-08-13 21:52
 */
@Data
public class SecUploadContext implements Serializable {
    private static final long serialVersionUID = -6021192871589810009L;

    /**
     * 文件夹 ID
     */
    private Long parentId;

    /**
     *  文件名称
     */
    private String filename;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 当前登录用户 ID
     */
    private Long userId;
}
