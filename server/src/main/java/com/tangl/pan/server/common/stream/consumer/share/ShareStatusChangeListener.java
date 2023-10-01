package com.tangl.pan.server.common.stream.consumer.share;

import com.tangl.pan.server.common.stream.channel.PanChannels;
import com.tangl.pan.server.common.stream.event.file.FileDeleteEvent;
import com.tangl.pan.server.common.stream.event.file.FileRestoreEvent;
import com.tangl.pan.server.modules.file.entity.TPanUserFile;
import com.tangl.pan.server.modules.file.enums.DelFlagEnum;
import com.tangl.pan.server.modules.file.service.IUserFileService;
import com.tangl.pan.server.modules.share.service.IShareService;
import com.tangl.pan.stream.core.AbstractConsumer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author tangl
 * @description 监听文件状态变更导致分享状态变更的处理器
 * @create 2023-09-17 17:20
 */
@Component
public class ShareStatusChangeListener extends AbstractConsumer {

    @Autowired
    private IUserFileService userFileService;


    @Autowired
    private IShareService shareService;

    /**
     * 监听文件被删除后，刷新所有受影响的分享的状态
     *
     * @param message 文件删除的消息对象
     */
    @StreamListener(PanChannels.FILE_DELETE_INPUT)
    public void changeShare2FileDeleted(Message<FileDeleteEvent> message) {
        if (isEmptyMessage(message)) {
            return;
        }
        printLog(message);
        FileDeleteEvent event = message.getPayload();
        List<Long> fileIdList = event.getFileIdList();
        if (CollectionUtils.isEmpty(fileIdList)) {
            return;
        }
        List<TPanUserFile> allRecords = userFileService.findAllFileRecordsByFileIdList(fileIdList);
        List<Long> allAvailableFileIdList = allRecords.stream()
                .filter(record -> Objects.equals(record.getDelFlag(), DelFlagEnum.NO.getCode()))
                .map(TPanUserFile::getFileId)
                .collect(Collectors.toList());
        allAvailableFileIdList.addAll(fileIdList);
        shareService.refreshShareStatus(allAvailableFileIdList);
    }

    /**
     * 监听文件被还原后，刷新所有受影响的分享的状态
     *
     * @param message 文件恢复的消息对象
     */
    @StreamListener(PanChannels.FILE_RESTORE_INPUT)
    public void changeShare2Normal(Message<FileRestoreEvent> message) {
        if (isEmptyMessage(message)) {
            return;
        }
        printLog(message);
        FileRestoreEvent event = message.getPayload();
        List<Long> fileIdList = event.getFileIdList();
        if (CollectionUtils.isEmpty(fileIdList)) {
            return;
        }
        List<TPanUserFile> allRecords = userFileService.findAllFileRecordsByFileIdList(fileIdList);
        List<Long> allAvailableFileIdList = allRecords.stream()
                .filter(record -> Objects.equals(record.getDelFlag(), DelFlagEnum.NO.getCode()))
                .map(TPanUserFile::getFileId)
                .collect(Collectors.toList());
        allAvailableFileIdList.addAll(fileIdList);
        shareService.refreshShareStatus(allAvailableFileIdList);
    }
}
