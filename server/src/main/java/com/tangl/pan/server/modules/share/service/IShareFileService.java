package com.tangl.pan.server.modules.share.service;

import com.tangl.pan.server.modules.share.context.SaveShareFilesContext;
import com.tangl.pan.server.modules.share.entity.TPanShareFile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author 25050
 * @description 针对表【t_pan_share_file(用户分享文件表)】的数据库操作Service
 * @createDate 2023-07-23 23:42:53
 */
public interface IShareFileService extends IService<TPanShareFile> {

    /**
     * 保存分享文件的关联关系
     *
     * @param context 上下文实体
     */
    void saveShareFiles(SaveShareFilesContext context);
}
