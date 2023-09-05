package com.tangl.pan.server.modules.file.context;

import com.tangl.pan.server.modules.file.entity.TPanUserFile;
import lombok.Data;

import java.io.Serializable;

/**
 * @author tangl
 * @description
 * @create 2023-08-13 11:53
 */
@Data
public class UpdateFilenameContext implements Serializable {

    private static final long serialVersionUID = -9000735818025178913L;

    /**
     * 更新的文件ID
     */
    private Long fileId;

    /**
     * 更新的文件名称
     */
    private String newFilename;

    /**
     * 当前的登录用户ID
     */
    private Long userId;

    /**
     * 更新的文件记录实体
     */
    private TPanUserFile entity;
}
