package com.tangl.pan.server.modules.share.service;

import com.tangl.pan.server.modules.share.context.SaveShareFilesContext;
import com.tangl.pan.server.modules.share.entity.PanShareFile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 文件分享业务层
 */
public interface IShareFileService extends IService<PanShareFile> {

    /**
     * 保存分享文件的关联关系
     *
     * @param context 上下文实体
     */
    void saveShareFiles(SaveShareFilesContext context);
}
