package com.tangl.pan.server.common.event.file;

import com.tangl.pan.server.modules.file.entity.TPanUserFile;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

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
public class PhysicalFileDeleteEvent extends ApplicationEvent {

    private static final long serialVersionUID = -6263153256233033738L;

    /**
     * 所有被物理删除的文件记录集合
     */
    private List<TPanUserFile> allRecords;

    public PhysicalFileDeleteEvent(Object source, List<TPanUserFile> allRecords) {
        super(source);
        this.allRecords = allRecords;
    }
}
