package com.tangl.pan.server.modules.file.service;

import com.tangl.pan.server.modules.file.context.*;
import com.tangl.pan.server.modules.file.entity.TPanUserFile;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tangl.pan.server.modules.file.vo.*;

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

    /**
     * 文件分片上传
     *
     * @param context 文件分片上传上下文实体
     * @return FileChunkUploadVO
     */
    FileChunkUploadVO chunkUpload(FileChunkUploadContext context);

    /**
     * 查询用户已上传的分片列表
     *
     * @param context 查询用户已上传的分片列表上下文实体
     * @return UploadedChunksVO
     */
    UploadedChunksVO getUploadedChunks(QueryUploadedChunksContext context);

    /**
     * 文件分片合并
     *
     * @param context 文件分片合并的上下文实体
     */
    void mergeFile(FileChunkMergeContext context);

    /**
     * 文件下载
     *
     * @param context 上下文实体
     */
    void download(FileDownloadContext context);

    /**
     * 分享文件下载 - 不校验用户是上传用户
     *
     * @param context 上下文实体
     */
    void downloadWithoutCheckUser(FileDownloadContext context);

    /**
     * 文件预览
     *
     * @param context 上下文实体
     */
    void preview(FilePreviewContext context);

    /**
     * 查询用户的文件夹树
     *
     * @param context 上下文实体
     * @return List<FolderTreeNodeVO>
     */
    List<FolderTreeNodeVO> getFolderTree(QueryFolderTreeContext context);

    /**
     * 文件转移
     *
     * @param context 上下文实体
     */
    void transferFile(TransferFileContext context);

    /**
     * 文件复制
     *
     * @param context 上下文实体
     */
    void copyFile(CopyFileContext context);

    /**
     * 文件搜索
     *
     * @param context 上下文实体
     * @return List<FileSearchResultVO>
     */
    List<FileSearchResultVO> search(FileSearchContext context);

    /**
     * 查询面包屑列表
     *
     * @param context 上下文实体
     * @return List<BreadcrumbsVO>
     */
    List<BreadcrumbsVO> getBreadcrumbs(QueryBreadcrumbsContext context);

    /**
     * 递归查询所有的子文件信息
     *
     * @param records 实体记录列表
     * @return List<TPanUserFile> 包含子文件实体
     */
    List<TPanUserFile> findAllFileRecords(List<TPanUserFile> records);

    /**
     * 递归查询所有的子文件信息
     *
     * @param fileIdList 文件 ID 集合
     * @return List<TPanUserFile> 包含子文件实体
     */
    List<TPanUserFile> findAllFileRecordsByFileIdList(List<Long> fileIdList);

    /**
     * 实体转换
     *
     * @param records TPanUserFile 实体列表
     * @return List<UserFileVO>
     */
    List<UserFileVO> transferVOList(List<TPanUserFile> records);
}
