package com.tangl.pan.server.modules.file.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangl.pan.core.exception.PanBusinessException;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.lock.core.annotation.Lock;
import com.tangl.pan.server.common.config.PanServerConfig;
import com.tangl.pan.server.modules.file.context.FileChunkSaveContext;
import com.tangl.pan.server.modules.file.converter.FileConverter;
import com.tangl.pan.server.modules.file.entity.TPanFileChunk;
import com.tangl.pan.server.modules.file.enums.MergeFlagEnum;
import com.tangl.pan.server.modules.file.service.IFileChunkService;
import com.tangl.pan.server.modules.file.mapper.PanFileChunkMapper;
import com.tangl.pan.storage.engine.core.StorageEngine;
import com.tangl.pan.storage.engine.core.context.StoreFileChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

/**
 * 文件分片业务层
 */
@Service
public class FileChunkServiceImpl extends ServiceImpl<PanFileChunkMapper, TPanFileChunk>
        implements IFileChunkService {

    @Autowired
    private PanServerConfig config;

    @Autowired
    private FileConverter fileConverter;

    @Autowired
    private StorageEngine storageEngine;

    /**
     * 文件分片保存
     * 1、保存文件分片和记录
     * 2、判断文件分片是否全部完成
     *
     * @param context 上下文实体
     */
    @Lock(name = "saveChunkFileLock", keys = {"#context.userId", "#context.identifier"}, expireSecond = 10L)
    @Override
    public void saveChunkFile(FileChunkSaveContext context) {
        doSaveChunkFile(context);
        doJudgeMergeFile(context);
    }

    /**
     * 判断文件分片是否全部完成
     *
     * @param context 上下文实体
     */
    private void doJudgeMergeFile(FileChunkSaveContext context) {
        QueryWrapper<TPanFileChunk> queryWrapper = Wrappers.query();
        queryWrapper.eq("identifier", context.getIdentifier());
        queryWrapper.eq("create_user", context.getUserId());
        int count = count(queryWrapper);
        if (count == context.getTotalChunks()) {
            context.setMergeFlagEnum(MergeFlagEnum.READY);
        }
    }

    /**
     * 保存文件分片和记录
     * 1、委托文件存储引擎存储文件分片
     * 2、保存文件分片记录
     *
     * @param context 上下文实体
     */
    private void doSaveChunkFile(FileChunkSaveContext context) {
        doStoreFileChunk(context);
        doSaveRecord(context);
    }

    /**
     * 保存文件分片记录
     *
     * @param context 上下文实体
     */
    private void doSaveRecord(FileChunkSaveContext context) {
        TPanFileChunk record = new TPanFileChunk();
        record.setId(IdUtil.get());
        record.setIdentifier(context.getIdentifier());
        record.setRealPath(context.getRealPath());
        record.setChunkNumber(context.getChunkNumber());
        record.setExpirationTime(DateUtil.offsetDay(new Date(), config.getChunkFileExpirationDays()));
        record.setCreateUser(context.getUserId());
        record.setCreateTime(new Date());
        if (!save(record)) {
            throw new PanBusinessException("文件分片上传失败");
        }
    }

    private void doStoreFileChunk(FileChunkSaveContext context) {
        try {
            StoreFileChunkContext storeFileChunkContext = fileConverter.fileChunkSaveContext2StoreFileChunkContext(context);
            storeFileChunkContext.setInputStream(context.getFile().getInputStream());
            storageEngine.storeChunk(storeFileChunkContext);
            context.setRealPath(storeFileChunkContext.getRealPath());
        } catch (IOException e) {
            throw new PanBusinessException("文件分片上传失败");
        }
    }
}




