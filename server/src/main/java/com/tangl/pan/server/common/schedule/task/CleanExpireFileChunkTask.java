package com.tangl.pan.server.common.schedule.task;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tangl.pan.core.constants.TPanConstants;
import com.tangl.pan.schedule.ScheduleTask;
import com.tangl.pan.server.common.event.log.ErrorLogEvent;
import com.tangl.pan.server.modules.file.entity.TPanFileChunk;
import com.tangl.pan.server.modules.file.service.IFileChunkService;
import com.tangl.pan.storage.engine.core.StorageEngine;
import com.tangl.pan.storage.engine.core.context.DeleteFileContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tangl
 * @description 清理文件分片过期的任务
 * @create 2023-09-14 21:45
 */
@Component
@Slf4j
public class CleanExpireFileChunkTask implements ScheduleTask, ApplicationContextAware {

    private static final Long BATCH_SIZE = 500L;

    @Autowired
    IFileChunkService fileChunkService;

    @Autowired
    private StorageEngine storageEngine;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public String getName() {
        return "CleanExpireFileChunkTask";
    }

    /**
     * 执行清理任务
     * 1、滚动查询过期的文件分片
     * 2、删除物理文件（委托文件存储引擎去实现）
     * 3、删除过期文件分片的记录信息
     * 4、重置上次查询的最大文件分片记录ID，继续滚动查询
     */
    @Override
    public void run() {
        log.info("{} start clean expire chunk file...", getName());
        List<TPanFileChunk> expireFileChunkRecords;
        Long scrollPointer = 1L;
        do {
            expireFileChunkRecords = scrollQueryExpireFileChunkRecords(scrollPointer);
            if (CollectionUtils.isNotEmpty(expireFileChunkRecords)) {
                deleteRealChunkFiles(expireFileChunkRecords);
                List<Long> idList = deleteChunkFileRecords(expireFileChunkRecords);
                scrollPointer = Collections.max(idList);
            }
        } while (CollectionUtils.isNotEmpty(expireFileChunkRecords));

        log.info("{} finish clean expire chunk file...", getName());
    }

    /**
     * @param expireFileChunkRecords 已过期的文件分片记录
     * @return 已删除的分片记录 ID 列表
     */
    private List<Long> deleteChunkFileRecords(List<TPanFileChunk> expireFileChunkRecords) {
        List<Long> idList = expireFileChunkRecords.stream().map(TPanFileChunk::getId).collect(Collectors.toList());
        fileChunkService.removeByIds(idList);
        return idList;
    }

    /**
     * 删除物理过期分片文件
     *
     * @param expireFileChunkRecords 已过期的文件分片记录
     */
    private void deleteRealChunkFiles(List<TPanFileChunk> expireFileChunkRecords) {
        List<String> realFilePathList = expireFileChunkRecords.stream().map(TPanFileChunk::getRealPath).collect(Collectors.toList());
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        deleteFileContext.setRealFilePathList(realFilePathList);
        try {
            storageEngine.delete(deleteFileContext);
        } catch (IOException e) {
            saveErrorLog(realFilePathList);
        }
    }

    /**
     * 保存错误日志
     *
     * @param realFilePathList 文件物理路径列表
     */
    private void saveErrorLog(List<String> realFilePathList) {
        ErrorLogEvent errorLogEvent = new ErrorLogEvent(this, "文件物理删除失败，请手动执行文件删除！文件路径为：" + JSON.toJSONString(realFilePathList), TPanConstants.ZERO_LONG);
        applicationContext.publishEvent(errorLogEvent);
    }

    /**
     * 滚动查询文件分片过期的记录
     *
     * @param scrollPointer 滚动指针
     * @return 已过期的文件分片记录
     */
    private List<TPanFileChunk> scrollQueryExpireFileChunkRecords(Long scrollPointer) {
        QueryWrapper<TPanFileChunk> queryWrapper = Wrappers.query();
        queryWrapper.le("expiration_time", new Date());
        queryWrapper.ge("id", scrollPointer);
        queryWrapper.last(" limit " + BATCH_SIZE);
        return fileChunkService.list(queryWrapper);
    }
}
