package com.tangl.pan.server.common.event.file;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @author tangl
 * @description 文件删除事件
 * @create 2023-08-13 17:48
 */
@Getter
@Setter
@ToString
public class DeleteFileEvent extends ApplicationEvent {

    private static final long serialVersionUID = -1922543273278238212L;
    private List<Long> fileIdList;

    public DeleteFileEvent(Object source, List<Long> fileIdList) {
        super(source);
        this.fileIdList = fileIdList;
    }
}