package com.tangl.pan.server.modules.file.context;

import com.tangl.pan.server.modules.file.entity.TPanUserFile;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author tangl
 * @description 文件转移操作的上下文实体
 * @create 2023-09-09 22:22
 */
@Data
public class TransferFileContext implements Serializable {

    private static final long serialVersionUID = -4974956990611155671L;

    /**
     * 要转移的文件 ID 集合
     */
    private List<Long> fileIdList;

    /**
     * 目标文件夹ID
     */
    private Long targetParentId;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 要转移的文件列表
     */

    private List<TPanUserFile> prepareRecords;
}
