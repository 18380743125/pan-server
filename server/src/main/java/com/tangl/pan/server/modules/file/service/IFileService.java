package com.tangl.pan.server.modules.file.service;

import com.tangl.pan.server.modules.file.context.FileSaveContext;
import com.tangl.pan.server.modules.file.context.FileUploadContext;
import com.tangl.pan.server.modules.file.entity.TPanFile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author 25050
 * @description 针对表【t_pan_file(物理文件信息表)】的数据库操作Service
 * @createDate 2023-07-23 23:41:43
 */
public interface IFileService extends IService<TPanFile> {

    /**
     * 文件保存的上下文实体
     *
     * @param context 单文件上传的上下文实体
     */
    void saveFile(FileSaveContext context);
}
