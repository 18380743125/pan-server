package com.tangl.pan.server.modules.recycle.context;

import com.tangl.pan.server.modules.file.entity.TPanUserFile;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author tangl
 * @description 文件删除的上下文实体
 * @create 2023-09-15 22:42
 */
@Data
public class DeleteContext implements Serializable {

    private static final long serialVersionUID = -6475235111216225269L;

    /**
     * 文件 ID 集合
     */
    private List<Long> fileIdList;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 要被删除的文件记录列表
     */
    private List<TPanUserFile> records;

    /**
     * 所有要被删除的文件记录列表
     */
    private List<TPanUserFile> allRecords;
}
