package com.tangl.pan.server.modules.file.service;

import com.tangl.pan.server.modules.file.context.*;
import com.tangl.pan.server.modules.file.entity.TPanUserFile;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tangl.pan.server.modules.file.vo.UserFileVO;

import java.util.List;

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

    /**
     * 查询用户的文件列表
     *
     * @param context 查询文件列表上下文实体
     * @return 文件列表
     */
    List<UserFileVO> getFileList(QueryFileListContext context);

    /**
     * 更新文件名称
     *
     * @param context 更新文件名的上下文实体
     */
    void updateFilename(UpdateFilenameContext context);

    /**
     * 批量删除用户文件
     *
     * @param context 批量删除用户文件的上下文实体
     */
    void deleteFile(DeleteFileContext context);

    /**
     * 文件秒传
     *
     * @param context 秒传上下文实体
     */
    boolean secUpload(SecUploadContext context);

    /**
     * 单文件上传
     *
     * @param context 单文件上传的上下文实体
     */
    void upload(FileUploadContext context);
}
