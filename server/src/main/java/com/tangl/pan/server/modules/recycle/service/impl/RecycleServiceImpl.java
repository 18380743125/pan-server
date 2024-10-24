package com.tangl.pan.server.modules.recycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tangl.pan.core.constants.PanConstants;
import com.tangl.pan.core.exception.PanBusinessException;
import com.tangl.pan.server.common.stream.channel.PanChannels;
import com.tangl.pan.server.common.stream.event.file.FileRestoreEvent;
import com.tangl.pan.server.common.stream.event.file.PhysicalFileDeleteEvent;
import com.tangl.pan.server.modules.file.context.QueryFileListContext;
import com.tangl.pan.server.modules.file.entity.TPanUserFile;
import com.tangl.pan.server.modules.file.enums.DelFlagEnum;
import com.tangl.pan.server.modules.file.service.IUserFileService;
import com.tangl.pan.server.modules.file.vo.UserFileVO;
import com.tangl.pan.server.modules.recycle.context.DeleteContext;
import com.tangl.pan.server.modules.recycle.context.QueryRecycleFileListContext;
import com.tangl.pan.server.modules.recycle.context.RestoreContext;
import com.tangl.pan.server.modules.recycle.service.IRecycleService;
import com.tangl.pan.stream.core.IStreamProducer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 回收站模块业务实现类
 */
@Service
public class RecycleServiceImpl implements IRecycleService {

    @Autowired
    private IUserFileService userFileService;

    @Autowired
    @Qualifier(value = "defaultStreamProducer")
    private IStreamProducer producer;

    @Override
    public List<UserFileVO> recycles(QueryRecycleFileListContext context) {
        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setUserId(context.getUserId());
        queryFileListContext.setDelFlag(DelFlagEnum.YES.getCode());
        return userFileService.getFileList(queryFileListContext);
    }

    /**
     * 文件批量还原
     * 1、检查操作权限
     * 2、检查是否可以还原
     * 3、执行文件还原的操作
     * 4、执行文件还原的后置操作
     *
     * @param context 上下文实体
     */
    @Override
    public void restore(RestoreContext context) {
        checkRestorePermission(context);
        checkRestoreFilename(context);
        doRestore(context);
        afterRestore(context);
    }

    /**
     * 文件彻底删除
     * 1、校验操作权限
     * 2、递归查询所有子文件
     * 3、执行文件删除的动作
     * 4、删除后的后置动作
     *
     * @param context 上下文实体
     */
    @Override
    public void delete(DeleteContext context) {
        checkFileDeletePermission(context);
        queryAllFileRecords(context);
        doDelete(context);
        afterDelete(context);
    }

    /**
     * 删除后的后置动作
     * 1、发送文件删除的事件
     *
     * @param context 上下文实体
     */
    private void afterDelete(DeleteContext context) {
        PhysicalFileDeleteEvent event = new PhysicalFileDeleteEvent(context.getAllRecords());
        producer.sendMessage(PanChannels.PHYSICAL_FILE_DELETE_OUTPUT, event);
    }

    /**
     * 执行文件删除的动作
     *
     * @param context 上下文实体
     */
    private void doDelete(DeleteContext context) {
        List<Long> fileIdList = context.getAllRecords().stream().map(TPanUserFile::getFileId).collect(Collectors.toList());
        userFileService.removeByIds(fileIdList);
    }

    /**
     * 递归查询所有子文件
     *
     * @param context 上下文实体
     */
    private void queryAllFileRecords(DeleteContext context) {
        List<TPanUserFile> records = context.getRecords();
        List<TPanUserFile> allRecords = userFileService.findAllFileRecords(records);
        context.setAllRecords(allRecords);
    }

    /**
     * 校验文件删除的权限
     *
     * @param context 上下文实体
     */
    private void checkFileDeletePermission(DeleteContext context) {
        QueryWrapper<TPanUserFile> queryWrapper = Wrappers.query();
        queryWrapper.eq("user_id", context.getUserId());
        queryWrapper.in("file_id", context.getFileIdList());
        List<TPanUserFile> records = userFileService.list(queryWrapper);
        if (CollectionUtils.isEmpty(records) || records.size() != context.getFileIdList().size()) {
            throw new PanBusinessException("无权限删除该文件");
        }
        context.setRecords(records);
    }

    /**
     * 还原的后置操作
     * 1、发布文件还原事件
     *
     * @param context 上下文实体
     */
    private void afterRestore(RestoreContext context) {
        FileRestoreEvent event = new FileRestoreEvent(context.getFileIdList());
        producer.sendMessage(PanChannels.FILE_RESTORE_OUTPUT, event);
    }

    /**
     * 执行文件还原的动作
     *
     * @param context 上下文实体
     */
    private void doRestore(RestoreContext context) {
        List<TPanUserFile> records = context.getRecords();
        records.forEach(record -> {
            record.setDelFlag(DelFlagEnum.NO.getCode());
            record.setUpdateTime(new Date());
            record.setUpdateUser(context.getUserId());
        });
        boolean updateFlag = userFileService.updateBatchById(records);
        if (!updateFlag) {
            throw new PanBusinessException("文件还原失败");
        }
    }

    /**
     * 检查要还原的文件名称是否被占用
     * 1、要还原的文件列表中有同一个文件夹下相同名称的文件，不允许还原
     * 2、要还原的文件当前的父文件夹下面存在同名文件，不允许还原
     *
     * @param context 上下文实体
     */
    private void checkRestoreFilename(RestoreContext context) {
        List<TPanUserFile> records = context.getRecords();
        Set<String> filenameSet = records.stream().map(record -> record.getFilename() + PanConstants.COMMON_SEPARATOR + record.getParentId()).collect(Collectors.toSet());
        if (filenameSet.size() != records.size()) {
            throw new PanBusinessException("文件还原失败，该还原文件中存在同名文件，请依次还原并重命名");
        }

        for (TPanUserFile record : records) {
            QueryWrapper<TPanUserFile> queryWrapper = Wrappers.query();
            queryWrapper.eq("user_id", context.getUserId());
            queryWrapper.eq("parent_id", record.getParentId());
            queryWrapper.eq("filename", record.getFilename());
            queryWrapper.eq("del_flag", DelFlagEnum.NO.getCode());
            if (userFileService.count(queryWrapper) > 0) {
                throw new PanBusinessException("文件：[" + record.getFilename() + "] 还原失败，该文件夹下已存在同名的文件或者文件夹，请重命名后再执行还原操作");
            }
        }
    }

    /**
     * 检查文件还原的操作权限
     *
     * @param context 上下文实体
     */
    private void checkRestorePermission(RestoreContext context) {
        List<Long> fileIdList = context.getFileIdList();
        List<TPanUserFile> records = userFileService.listByIds(fileIdList);
        if (CollectionUtils.isEmpty(records)) {
            throw new PanBusinessException("文件还原失败");
        }
        Set<Long> userIdSet = records.stream().map(TPanUserFile::getUserId).collect(Collectors.toSet());
        if (userIdSet.size() > 1) {
            throw new PanBusinessException("无权限还原文件");
        }
        if (!userIdSet.contains(context.getUserId())) {
            throw new PanBusinessException("无权限执行文件还原");
        }
        context.setRecords(records);
    }
}
