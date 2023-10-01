package com.tangl.pan.server.common.stream.event.file;

import com.tangl.pan.server.modules.file.entity.TPanUserFile;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author tangl
 * @description 文件被物理删除的事件实体
 * @create 2023-09-16 0:02
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class PhysicalFileDeleteEvent implements Serializable {

    private static final long serialVersionUID = -6263153256233033738L;

    /**
     * 所有被物理删除的文件记录集合
     */
    private List<TPanUserFile> allRecords;

    public PhysicalFileDeleteEvent(List<TPanUserFile> allRecords) {
        this.allRecords = allRecords;
    }
}
