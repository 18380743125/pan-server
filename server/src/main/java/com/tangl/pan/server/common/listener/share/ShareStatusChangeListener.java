//package com.tangl.pan.server.common.listener.share;
//
//import com.tangl.pan.server.common.event.file.FileDeleteEvent;
//import com.tangl.pan.server.common.event.file.FileRestoreEvent;
//import com.tangl.pan.server.modules.file.entity.TPanUserFile;
//import com.tangl.pan.server.modules.file.enums.DelFlagEnum;
//import com.tangl.pan.server.modules.file.service.IUserFileService;
//import com.tangl.pan.server.modules.share.service.IShareService;
//import org.apache.commons.collections.CollectionUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.event.EventListener;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
///**
// * @author tangl
// * @description 监听文件状态变更导致分享状态变更的处理器
// * @create 2023-09-17 17:20
// */
//@Component
//public class ShareStatusChangeListener {
//
//    @Autowired
//    private IUserFileService userFileService;
//
//
//    @Autowired
//    private IShareService shareService;
//
//    /**
//     * 监听文件被删除后，刷新所有受影响的分享的状态
//     *
//     * @param event FileDeleteEvent
//     */
//    @EventListener(FileDeleteEvent.class)
//    @Async(value = "eventListenerTaskExecutor")
//    public void changeShare2FileDeleted(FileDeleteEvent event) {
//        List<Long> fileIdList = event.getFileIdList();
//        if (CollectionUtils.isEmpty(fileIdList)) {
//            return;
//        }
//        List<TPanUserFile> allRecords = userFileService.findAllFileRecordsByFileIdList(fileIdList);
//        List<Long> allAvailableFileIdList = allRecords.stream()
//                .filter(record -> Objects.equals(record.getDelFlag(), DelFlagEnum.NO.getCode()))
//                .map(TPanUserFile::getFileId)
//                .collect(Collectors.toList());
//        allAvailableFileIdList.addAll(fileIdList);
//        shareService.refreshShareStatus(allAvailableFileIdList);
//    }
//
//    /**
//     * 监听文件被还原后，刷新所有受影响的分享的状态
//     *
//     * @param event FileRestoreEvent
//     */
//    @EventListener(FileRestoreEvent.class)
//    @Async(value = "eventListenerTaskExecutor")
//    public void changeShare2Normal(FileRestoreEvent event) {
//        List<Long> fileIdList = event.getFileIdList();
//        if (CollectionUtils.isEmpty(fileIdList)) {
//            return;
//        }
//        List<TPanUserFile> allRecords = userFileService.findAllFileRecordsByFileIdList(fileIdList);
//        List<Long> allAvailableFileIdList = allRecords.stream()
//                .filter(record -> Objects.equals(record.getDelFlag(), DelFlagEnum.NO.getCode()))
//                .map(TPanUserFile::getFileId)
//                .collect(Collectors.toList());
//        allAvailableFileIdList.addAll(fileIdList);
//        shareService.refreshShareStatus(allAvailableFileIdList);
//    }
//}
