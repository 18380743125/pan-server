//package com.tangl.pan.server.common.event.file;
//
//import lombok.*;
//import org.springframework.context.ApplicationEvent;
//
//import java.util.List;
//
///**
// * @author tangl
// * @description 文件还原事件实体
// * @create 2023-09-15 23:22
// */
//@EqualsAndHashCode(callSuper = false)
//@Getter
//@Setter
//public class FileRestoreEvent extends ApplicationEvent {
//
//    private static final long serialVersionUID = 1373379595228122981L;
//
//    /**
//     * 被成功还原的文件记录 ID 集合
//     */
//    private List<Long> fileIdList;
//
//    public FileRestoreEvent(Object source, List<Long> fileIdList) {
//        super(source);
//        this.fileIdList = fileIdList;
//    }
//}
