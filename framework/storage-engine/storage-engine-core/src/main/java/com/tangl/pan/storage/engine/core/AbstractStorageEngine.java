package com.tangl.pan.storage.engine.core;

import cn.hutool.core.lang.Assert;
import com.tangl.pan.cache.core.constants.CacheConstants;
import com.tangl.pan.core.exception.TPanFrameworkException;
import com.tangl.pan.storage.engine.core.context.DeleteFileContext;
import com.tangl.pan.storage.engine.core.context.StoreFileContext;
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
     * 公用的获取缓存的方法
     *
     * @return Cache
     */
    private Cache getCache() {
        if (Objects.isNull(cacheManager)) {
            throw new TPanFrameworkException("the cache manager is empty!");
        }
        return cacheManager.getCache(CacheConstants.T_PAN_CACHE_NAME);
    }

    /**
     * 存储物理文件
     * 1、参数校验
     * 2、执行动作
     *
     * @param context 存储物理文件的上下文实体
     * @throws IOException IO 异常
     */
    @Override
    public void store(StoreFileContext context) throws IOException {
        checkStoreFileContext(context);
        doStore(context);
    }

    /**
     * 执行保存物理文件的动作
     * 下沉到具体的子类去实现
     *
     * @param context 存储物理文件的上下文实体
     */
    protected abstract void doStore(StoreFileContext context) throws IOException;

    /**
     * 校验上传物理文件的上下文信息
     *
     * @param context 存储物理文件的上下文实体
     */
    private void checkStoreFileContext(StoreFileContext context) {
        Assert.notBlank(context.getFilename(), "文件名称不能为空");
        Assert.notNull(context.getTotalSize(), "文件的总大小不能为空");
        Assert.notNull(context.getInputStream(), "文件不能为空");
    }

    /**
     * 删除物理文件
     * 1、参数校验
     * 2、执行动作
     *
     * @param context 删除物理文件的上下文实体
     * @throws IOException IO 异常
     */
    @Override
    public void delete(DeleteFileContext context) throws IOException {
        checkDeleteFileContext(context);
        doDelete(context);
    }

    /**
     * 执行删除物理文件
     * 下沉到具体子类实现
     *
     * @param context 删除物理文件的上下文实体
     */
    protected abstract void doDelete(DeleteFileContext context) throws IOException;

    /**
     * 校验删除物理文件的上下文实体
     *
     * @param context 删除物理文件的上下文实体
     */
    private void checkDeleteFileContext(DeleteFileContext context) {
        Assert.notEmpty(context.getRealFilePathList(), "要删除的文件路径列表不能为空");
    }
}
