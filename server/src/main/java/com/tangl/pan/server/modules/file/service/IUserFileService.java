package com.tangl.pan.server.modules.file.service;

import com.tangl.pan.server.modules.file.context.CreateFolderContext;
import com.tangl.pan.server.modules.file.entity.TPanUserFile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author tangl
 * @description 用户文件业务层
 * @createDate 2023-07-23 23:41:43
 */
public interface IUserFileService extends IService<TPanUserFile> {
    /**
     * 创建文件夹信息
     *
     * @param context context
     * @return long
     */
    Long createFolder(CreateFolderContext context);

    /**
     * 获取用户根文件夹信息
     *
     * @param userId 用户 ID
     * @return TPanUserFile
     */
    TPanUserFile getUserRootFile(Long userId);
}
