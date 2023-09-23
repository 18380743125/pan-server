package com.tangl.pan.storage.engine.core;

import cn.hutool.core.lang.Assert;
import com.tangl.pan.cache.core.constants.CacheConstants;
import com.tangl.pan.core.exception.TPanFrameworkException;
import com.tangl.pan.storage.engine.core.context.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/**
 * @author tangl
 * @description 顶级文件存储引擎的公用父类
 * @create 2023-08-14 21:28
 */
@Component
public abstract class AbstractStorageEngine implements StorageEngine {

    @Autowired
    private CacheManager cacheManager;

    /**
     * 执行保存物理文件的动作
     * 下沉到具体的子类去实现
     *
     * @param context 上下文实体
     */
    protected abstract void doStore(StoreFileContext context) throws IOException;

    /**
     * 执行删除物理文件
     * 下沉到具体子类实现
     *
     * @param context 上下文实体
     */
    protected abstract void doDelete(DeleteFileContext context) throws IOException;

    /**
     * 执行保存文件分片
     * 下沉到具体的子类实现
     *
     * @param context 上下文实体
     */
    protected abstract void doStoreChunk(StoreFileChunkContext context) throws IOException;

    /**
     * 执行文件分片的动作
     * 下沉到具体的子类去实现
     *
     * @param context 上下文实体
     */
    protected abstract void doMergeFile(MergeFileContext context) throws IOException;

    /**
     * 读取文件内容并写入到输出流中
     * 下沉到子类去实现
     *
     * @param context 上下文实体
     */
    protected abstract void doReadFile(ReadFileContext context) throws IOException;

    /**
     * 存储物理文件
     * 1、参数校验
     * 2、执行动作
     *
     * @param context 上下文实体
     */
    @Override
    public void store(StoreFileContext context) throws IOException {
        checkStoreFileContext(context);
        doStore(context);
    }

    /**
     * 删除物理文件
     * 1、参数校验
     * 2、执行动作
     *
     * @param context 删除物理文件的上下文实体
     */
    @Override
    public void delete(DeleteFileContext context) throws IOException {
        checkDeleteFileContext(context);
        doDelete(context);
    }

    /**
     * 存储物理文件的分片
     * 1、参数校验
     * 2、执行动作
     *
     * @param context 上下文实体
     */
    @Override
    public void storeChunk(StoreFileChunkContext context) throws IOException {
        checkStoreFileChunkContext(context);
        doStoreChunk(context);
    }

    /**
     * 1、检查参数
     * 2、执行动作
     *
     * @param context 上下文实体
     */
    @Override
    public void mergeFile(MergeFileContext context) throws IOException {
        checkMergeFileContext(context);
        doMergeFile(context);
    }

    /**
     * 读取文件内容写入到输出流中
     * 1、参数校验
     * 2、执行动作
     *
     * @param context 上下文实体
     */
    @Override
    public void readFile(ReadFileContext context) throws IOException {
        checkReadFileContext(context);
        doReadFile(context);
    }

    /**
     * 文件读取参数校验
     *
     * @param context 上下文实体
     */
    private void checkReadFileContext(ReadFileContext context) {
        Assert.notBlank(context.getReadPath(), "文件真实存储路径不能为空");
        Assert.notNull(context.getOutputStream(), "文件的输出流不能为空");
    }

    /**
     * 检查上下文实体信息
     *
     * @param context 上下文实体
     */
    private void checkMergeFileContext(MergeFileContext context) {
        Assert.notBlank(context.getFilename(), "文件名称不能为空");
        Assert.notBlank(context.getIdentifier(), "文件唯一标识不能为空");
        Assert.notNull(context.getUserId(), "当前登录用户的ID不能为空");
        Assert.notEmpty(context.getRealPathList(), "文件分片列表不能为空");
    }

    /**
     * 校验保存文件分片的参数
     *
     * @param context 上下文实体
     */
    private void checkStoreFileChunkContext(StoreFileChunkContext context) {
        Assert.notBlank(context.getFilename(), "文件名称不能为空");
        Assert.notBlank(context.getIdentifier(), "文件唯一标识不能为空");
        Assert.notNull(context.getTotalSize(), "文件大小不能为空");
        Assert.notNull(context.getInputStream(), "文件分片不能为空");
        Assert.notNull(context.getTotalChunks(), "文件分片总数不能为空");
        Assert.notNull(context.getChunkNumber(), "文件分片下标不能为空");
        Assert.notNull(context.getCurrentChunkSize(), "文件分片的大小不能为空");
        Assert.notNull(context.getUserId(), "当前登录用户的ID不能为空");
    }

    /**
     * 校验删除物理文件的上下文实体
     *
     * @param context 删除物理文件的上下文实体
     */
    private void checkDeleteFileContext(DeleteFileContext context) {
        Assert.notEmpty(context.getRealFilePathList(), "要删除的文件路径列表不能为空");
    }

    /**
     * 校验上传物理文件的上下文信息
     *
     * @param context 上下文实体
     */
    private void checkStoreFileContext(StoreFileContext context) {
        Assert.notBlank(context.getFilename(), "文件名称不能为空");
        Assert.notNull(context.getTotalSize(), "文件的总大小不能为空");
        Assert.notNull(context.getInputStream(), "文件不能为空");
    }

    /**
     * 公用的获取缓存的方法
     *
     * @return Cache
     */
    protected Cache getCache() {
        if (Objects.isNull(cacheManager)) {
            throw new TPanFrameworkException("the cache manager is empty!");
        }
        return cacheManager.getCache(CacheConstants.T_PAN_CACHE_NAME);
    }
}
