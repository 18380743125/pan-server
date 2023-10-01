package com.tangl.pan.server.common.stream.event.file;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author tangl
 * @description 文件还原事件实体
 * @create 2023-09-15 23:22
 */
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public class FileRestoreEvent implements Serializable {

    private static final long serialVersionUID = 1373379595228122981L;

    /**
     * 被成功还原的文件记录 ID 集合
     */
    private List<Long> fileIdList;

    public FileRestoreEvent(List<Long> fileIdList) {
        this.fileIdList = fileIdList;
    }
}
