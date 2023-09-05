package com.tangl.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.tangl.pan.core.exception.TPanBusinessException;
import com.tangl.pan.core.utils.FileUtil;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.server.common.event.file.ErrorLogEvent;
import com.tangl.pan.server.modules.file.context.FileSaveContext;
import com.tangl.pan.server.modules.file.entity.TPanFile;
import com.tangl.pan.server.modules.file.service.IFileService;
import com.tangl.pan.server.modules.file.mapper.TPanFileMapper;
import com.tangl.pan.storage.engine.core.StorageEngine;
import com.tangl.pan.storage.engine.core.context.DeleteFileContext;
import com.tangl.pan.storage.engine.core.context.StoreFileContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

/**
 * @author 25050
 * @description 针对表【t_pan_file(物理文件信息表)】的数据库操作Service实现
 * @createDate 2023-07-23 23:41:43
 */
@Service
public class FileServiceImpl extends ServiceImpl<TPanFileMapper, TPanFile> implements IFileService, ApplicationContextAware {

    @Qualifier("localStorageEngine")
    @Autowired
    private StorageEngine storageEngine;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 上传单文件并保存实体记录
     * 1、上传单文件
     * 2、保存文件实体记录
     *
     * @param context 文件保存的上下文实体
     */
    @Override
    public void saveFile(FileSaveContext context) {
        storeMultipartFile(context);

        TPanFile record = doSaveFile(context.getFilename(),
                context.getRealPath(),
                context.getTotalSize(),
                context.getIdentifier(),
                context.getUserId());
        context.setRecord(record);
    }

    /**
     * 保存文件实体记录
     *
     * @param filename   文件名称
     * @param realPath   文件的物理路径
     * @param totalSize  文件大小
     * @param identifier 文件的唯一标识
     * @param userId     登录的用户ID
     * @return record
     */
    private TPanFile doSaveFile(String filename, String realPath, Long totalSize, String identifier, Long userId) {
        TPanFile record = assembleTPanFile(filename, realPath, totalSize, identifier, userId);
        if (!save(record)) {
            try {
                DeleteFileContext deleteFileContext = new DeleteFileContext();
                deleteFileContext.setRealFilePathList(Lists.newArrayList(realPath));
                storageEngine.delete(deleteFileContext);
            } catch (IOException e) {
                e.printStackTrace();
                ErrorLogEvent errorLogEvent = new ErrorLogEvent(this, "文件物理删除失败，请手动删除！文件路径：" + realPath, userId);
                applicationContext.publishEvent(errorLogEvent);
            }
        }
        return record;
    }

    /**
     * 拼装文件实体对象
     *
     * @param filename   文件名称
     * @param realPath   文件的物理路径
     * @param totalSize  文件大小
     * @param identifier 文件的唯一标识
     * @param userId     登录的用户ID
     * @return record
     */
    private TPanFile assembleTPanFile(String filename, String realPath, Long totalSize, String identifier, Long userId) {
        TPanFile record = new TPanFile();
        record.setFileId(IdUtil.get());
        record.setFilename(filename);
        record.setRealPath(realPath);
        record.setFileSize(String.valueOf(totalSize));
        record.setFileSizeDesc(FileUtil.byteCountToDisplaySize(totalSize));
        record.setFileSuffix(FileUtil.getFileSuffix(filename));
        record.setIdentifier(identifier);
        record.setCreateUser(userId);
        record.setCreateTime(new Date());
        return record;
    }

    /**
     * 上传单文件
     * 该方法委托文件存储引擎实现
     *
     * @param context 文件保存的上下文实体
     */
    private void storeMultipartFile(FileSaveContext context) {
        try {
            StoreFileContext storeFileContext = new StoreFileContext();
            storeFileContext.setInputStream(context.getFile().getInputStream());
            storeFileContext.setFilename(context.getFilename());
            storeFileContext.setTotalSize(context.getTotalSize());
            storageEngine.store(storeFileContext);
            context.setRealPath(storeFileContext.getRealPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new TPanBusinessException("文件上传失败");
        }
    }
}




