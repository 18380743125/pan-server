package com.tangl.pan.server.modules.recycle.context;

import com.tangl.pan.server.modules.file.entity.TPanUserFile;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author tangl
 * @description 文件还原的上下文实体
 * @create 2023-09-15 22:42
 */
@Data
public class RestoreContext implements Serializable {

    private static final long serialVersionUID = 1796424258049863089L;

    /**
     * 文件ID集合
     */
    private List<Long> fileIdList;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 要被还原的文件记录列表
     */
    private List<TPanUserFile> records;
}
