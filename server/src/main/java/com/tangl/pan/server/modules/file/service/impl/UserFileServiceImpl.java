package com.tangl.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangl.pan.core.constants.TPanConstants;
import com.tangl.pan.core.exception.TPanBusinessException;
import com.tangl.pan.core.utils.FileUtil;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.server.common.event.file.DeleteFileEvent;
import com.tangl.pan.server.common.utils.HttpUtil;
import com.tangl.pan.server.modules.file.constants.FileConstants;
import com.tangl.pan.server.modules.file.context.*;
import com.tangl.pan.server.modules.file.converter.FileConverter;
import com.tangl.pan.server.modules.file.entity.TPanFile;
import com.tangl.pan.server.modules.file.entity.TPanFileChunk;
import com.tangl.pan.server.modules.file.entity.TPanUserFile;
import com.tangl.pan.server.modules.file.enums.DelFlagEnum;
import com.tangl.pan.server.modules.file.enums.FileTypeEnum;
import com.tangl.pan.server.modules.file.enums.FolderFlagEnum;
import com.tangl.pan.server.modules.file.service.IFileChunkService;
import com.tangl.pan.server.modules.file.service.IFileService;
import com.tangl.pan.server.modules.file.service.IUserFileService;
import com.tangl.pan.server.modules.file.mapper.TPanUserFileMapper;
import com.tangl.pan.server.modules.file.vo.FileChunkUploadVO;
import com.tangl.pan.server.modules.file.vo.UploadedChunksVO;
import com.tangl.pan.server.modules.file.vo.UserFileVO;
import com.tangl.pan.storage.engine.core.StorageEngine;
import com.tangl.pan.storage.engine.core.context.ReadFileContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author tangl
 * @description 用户文件业务层
 * @createDate 2023-07-23 23:41:43
 */
@Service(value = "userFileService")
public class UserFileServiceImpl extends ServiceImpl<TPanUserFileMapper, TPanUserFile> implements IUserFileService, ApplicationContextAware {

    @Autowired
    private IFileService fileService;

    @Autowired
    private IFileChunkService fileChunkService;

    @Qualifier("localStorageEngine")
    @Autowired
    private StorageEngine storageEngine;

    @Autowired
    private FileConverter fileConverter;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 创建文件夹信息
     *
     * @param context context
     * @return long
     */
    @Override
    public Long createFolder(CreateFolderContext context) {
        return saveUserFile(context.getParentId(), context.getFolderName(), FolderFlagEnum.YES, null, null, context.getUserId(), null);
    }

    /**
     * 查询用户根文件夹信息
     *
     * @param userId 用户 ID
     * @return TPanUserFile
     */
    @Override
    public TPanUserFile getUserRootFile(Long userId) {
        QueryWrapper<TPanUserFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("parent_id", FileConstants.TOP_PARENT_ID);
        queryWrapper.eq("del_flag", DelFlagEnum.NO.getCode());
        queryWrapper.eq("folder_flag", FolderFlagEnum.YES.getCode());
        return getOne(queryWrapper);
    }

    /**
     * 查询用户的文件列表
     *
     * @param context 查询文件列表上下文实体
     * @return List<UserFileVO>
     */
    @Override
    public List<UserFileVO> getFileList(QueryFileListContext context) {
        return baseMapper.selectFileList(context);
    }

    /**
     * 更新文件名称
     * 1、校验更新文件名称的条件
     * 2、执行更新文件名称的操作
     *
     * @param context 重命名文件名的上下文实体
     */
    @Override
    public void updateFilename(UpdateFilenameContext context) {
        checkUpdateFilenameCondition(context);
        doUpdateFilename(context);
    }

    /**
     * 批量删除用户文件
     * 1、校验删除的条件
     * 2、执行批量删除
     * 3、发布批量删除文件的事件，给其他模块使用
     *
     * @param context 批量删除用户文件的上下文实体
     */
    @Override
    public void deleteFile(DeleteFileContext context) {
        checkFileDeleteCondition(context);
        doDeleteFile(context);
        afterFileDelete(context);
    }

    /**
     * 文件秒传
     * 1、通过文件的唯一标识查询文件实体记录
     * 2、如果没有查到，直接返回秒传失败
     * 3、如果查到记录，直接挂在关联关系，返回秒传成功
     *
     * @param context 秒传上下文实体
     * @return 是否秒传成功
     */
    @Override
    public boolean secUpload(SecUploadContext context) {
        String filename = context.getFilename();

        TPanFile record = getFileByUserIdAndIdentifier(context);
        if (Objects.isNull(record)) {
            return false;
        }

        saveUserFile(context.getParentId(), context.getFilename(), FolderFlagEnum.NO, FileTypeEnum.getFileTypeCode(FileUtil.getFileSuffix(filename)), record.getFileId(), context.getUserId(), record.getFileSizeDesc());
        return true;
    }

    /**
     * 单文件上传
     * 1、上传文件并保存实体文件记录
     * 2、保存用户文件的关系记录
     *
     * @param context 单文件上传的上下文实体
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void upload(FileUploadContext context) {
        saveFile(context);

        saveUserFile(context.getParentId(), context.getFilename(), FolderFlagEnum.NO, FileTypeEnum.getFileTypeCode(FileUtil.getFileSuffix(context.getFilename())), context.getRecord().getFileId(), context.getUserId(), context.getRecord().getFileSizeDesc());
    }

    /**
     * 文件分片上传
     * 1、上传实体文件
     * 2、保存分片文件记录
     * 3、校验是否全部分片上传完成
     *
     * @param context 文件分片上传上下文实体
     * @return FileChunkUploadVO
     */
    @Override
    public FileChunkUploadVO chunkUpload(FileChunkUploadContext context) {
        FileChunkSaveContext fileChunkSaveContext = fileConverter.fileChunkUploadContext2FileChunkSaveContext(context);
        fileChunkService.saveChunkFile(fileChunkSaveContext);
        FileChunkUploadVO vo = new FileChunkUploadVO();
        vo.setMergeFlag(fileChunkSaveContext.getMergeFlagEnum().getCode());
        return vo;
    }

    /**
     * 查询用户已上传的分片列表
     * 1、查询已上传的分片列表
     * 2、封装返回实体
     *
     * @param context 查询用户已上传的分片列表上下文实体
     * @return UploadedChunksVO
     */
    @Override
    public UploadedChunksVO getUploadedChunks(QueryUploadedChunksContext context) {
        QueryWrapper<TPanFileChunk> queryWrapper = Wrappers.query();
        queryWrapper.select("chunk_number");
        queryWrapper.eq("identifier", context.getIdentifier());
        queryWrapper.eq("create_user", context.getUserId());
        queryWrapper.gt("expiration_time", new Date());

        List<Integer> uploadedChunks = fileChunkService.listObjs(queryWrapper, value -> (Integer) value);

        UploadedChunksVO uploadedChunksVO = new UploadedChunksVO();
        uploadedChunksVO.setUploadedChunks(uploadedChunks);
        return uploadedChunksVO;
    }

    /**
     * 文件分片合并
     * 1、文件分片物理合并
     * 2、保存文件实体记录
     * 3、保存文件用户关系映射
     *
     * @param context 文件分片合并的上下文实体
     */
    @Override
    public void mergeFile(FileChunkMergeContext context) {
        mergeFileChunkAndSaveFile(context);
        saveUserFile(context.getParentId(), context.getFilename(), FolderFlagEnum.NO, FileTypeEnum.getFileTypeCode(FileUtil.getFileSuffix(context.getFilename())), context.getRecord().getFileId(), context.getUserId(), context.getRecord().getFileSizeDesc());
    }

    /**
     * 文件下载
     * 1、参数校验：文件是否存在，文件是否属于该用户
     * 2、校验该文件是不是文件夹
     * 3、执行下载的动作
     *
     * @param context 上下文实体
     */
    @Override
    public void download(FileDownloadContext context) {
        TPanUserFile record = getById(context.getFileId());
        checkOperatePermission(record, context.getUserId());
        if (checkIsFolder(record)) {
            throw new TPanBusinessException("文件夹暂不支持下载");
        }
        doDownload(record, context.getResponse());
    }

    /**
     * 文件预览
     * 1、参数校验：文件是否存在，文件是否属于该用户
     * 2、校验该文件是不是文件夹
     * 3、执行文件预览的动作
     *
     * @param context 上下文实体
     */
    @Override
    public void preview(FilePreviewContext context) {
        TPanUserFile record = getById(context.getFileId());
        checkOperatePermission(record, record.getUserId());
        if (checkIsFolder(record)) {
            throw new TPanBusinessException("文件夹暂不支持下载");
        }
        doPreview(record, context.getResponse());
    }

    /**
     * 执行文件预览的动作
     * 1、查询文件的存储路径
     * 2、添加跨域公共响应头
     * 3、委托文件存储引擎读取文件内容到输出流中
     *
     * @param record   文件实体
     * @param response 响应实体
     */
    private void doPreview(TPanUserFile record, HttpServletResponse response) {
        Long realFileId = record.getRealFileId();
        TPanFile realFileRecord = fileService.getById(realFileId);
        if (Objects.isNull(realFileRecord)) {
            throw new TPanBusinessException("当前的文件记录不存在");
        }
        addCommonResponseHeader(response, realFileRecord.getFilePreviewContentType());
        readFile2OutputStream(realFileRecord.getRealPath(), response);
    }

    /**
     * 执行下载的动作
     * 1、查询文件的存储路径
     * 2、添加跨域公共响应头
     * 3、拼装下载文件的名称、长度等响应信息
     * 4、委托文件存储引擎读取文件内容到输出流中
     *
     * @param record   文件实体
     * @param response 响应实体
     */
    private void doDownload(TPanUserFile record, HttpServletResponse response) {
        Long realFileId = record.getRealFileId();
        TPanFile realFileRecord = fileService.getById(realFileId);
        if (Objects.isNull(realFileRecord)) {
            throw new TPanBusinessException("当前的文件记录不存在");
        }
        addCommonResponseHeader(response, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        addDownloadAttribute(response, record, realFileRecord);
        readFile2OutputStream(realFileRecord.getRealPath(), response);
    }

    /**
     * 委托文件存储引擎读取文件内容到输出流中
     *
     * @param realPath 文件物理存放路径
     * @param response 响应头
     */
    private void readFile2OutputStream(String realPath, HttpServletResponse response) {
        try {
            ReadFileContext readFileContext = new ReadFileContext();
            readFileContext.setReadPath(realPath);
            readFileContext.setOutputStream(response.getOutputStream());
            storageEngine.readFile(readFileContext);
        } catch (IOException e) {
            throw new TPanBusinessException("文件下载失败");
        }
    }

    /**
     * 添加文件下载的属性信息
     *
     * @param response       响应头
     * @param record         文件实体记录
     * @param realFileRecord 文件物理存放路径
     */
    private void addDownloadAttribute(HttpServletResponse response, TPanUserFile record, TPanFile realFileRecord) {
        try {
            response.setHeader(FileConstants.CONTENT_DISPOSITION_STR, FileConstants.CONTENT_DISPOSITION_VALUE_PREFIX_STR + new String(record.getFilename().getBytes(FileConstants.GB2312_STR), FileConstants.IOS_8859_1_STR));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new TPanBusinessException("文件下载失败");
        }
        response.setContentLengthLong(Long.parseLong(realFileRecord.getFileSize()));
    }

    /**
     * 添加公共的文件读取响应头
     *
     * @param response         响应对象
     * @param contentTypeValue contentTypeValue
     */
    private void addCommonResponseHeader(HttpServletResponse response, String contentTypeValue) {
        response.reset();
        HttpUtil.addCorsResponseHeaders(response);
        response.addHeader(FileConstants.CONTENT_TYPE_STR, contentTypeValue);
        response.setContentType(contentTypeValue);
    }

    /**
     * 监测当前文件记录是否是文件夹
     *
     * @param record 文件实体
     * @return boolean
     */
    private boolean checkIsFolder(TPanUserFile record) {
        if (Objects.isNull(record)) {
            throw new TPanBusinessException("当前文件记录不存在");
        }
        return Objects.equals(record.getFolderFlag(), FolderFlagEnum.YES.getCode());
    }


    /**
     * 校验用户权限
     * 1、文件存在
     * 2、文件记录属于该登录用户
     *
     * @param record 用户文件记录
     * @param userId 用户ID
     */
    private void checkOperatePermission(TPanUserFile record, Long userId) {
        if (Objects.isNull(record)) {
            throw new TPanBusinessException("当前文件记录不存在");
        }
        if (!Objects.equals(record.getUserId(), userId)) {
            throw new TPanBusinessException("你没有该文件的操作权限");
        }
    }

    /**
     * 文件分片物理合并并保存文件记录
     *
     * @param context 文件分片合并的上下文实体
     */
    private void mergeFileChunkAndSaveFile(FileChunkMergeContext context) {
        FileChunkMergeAndSaveContext fileChunkMergeAndSaveContext = fileConverter.fileChunkMergeContext2FileChunkMergeAndSaveContext(context);
        fileService.mergeFileChunkAndSaveFile(fileChunkMergeAndSaveContext);
        context.setRecord(fileChunkMergeAndSaveContext.getRecord());
    }

    /**
     * 上传文件并保存实体文件记录
     * 委托给实体文件的 service 完成该操作
     *
     * @param context 单文件上传的上下文实体
     */
    private void saveFile(FileUploadContext context) {
        FileSaveContext fileSaveContext = fileConverter.fileUploadContext2FileSaveContext(context);
        fileService.saveFile(fileSaveContext);
        context.setRecord(fileSaveContext.getRecord());
    }

    private TPanFile getFileByUserIdAndIdentifier(SecUploadContext context) {
        Long userId = context.getUserId();
        String identifier = context.getIdentifier();
        QueryWrapper<TPanFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("create_user", userId);
        queryWrapper.eq("identifier", identifier);
        List<TPanFile> records = fileService.list(queryWrapper);
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }
        return records.get(TPanConstants.ZERO_INT);
    }

    /**
     * 发布批量删除文件的事件，给其他模块使用
     *
     * @param context 批量删除用户文件的上下文实体
     */
    private void afterFileDelete(DeleteFileContext context) {
        List<Long> fileIdList = context.getFileIdList();
        DeleteFileEvent deleteFileEvent = new DeleteFileEvent(this, fileIdList);
        applicationContext.publishEvent(deleteFileEvent);
    }

    /**
     * 执行批量删除
     *
     * @param context 批量删除用户文件的上下文实体
     */
    private void doDeleteFile(DeleteFileContext context) {
        List<Long> fileIdList = context.getFileIdList();
        UpdateWrapper<TPanUserFile> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("file_id", fileIdList);
        updateWrapper.set("del_flag", DelFlagEnum.YES.getCode());
        updateWrapper.set("update_time", new Date());
        if (!update(updateWrapper)) {
            throw new TPanBusinessException("文件删除失败");
        }
    }

    /**
     * 删除文件前的前置校验
     * 1、文件 ID 合法校验
     * 2、用户拥有删除该文件的权限
     *
     * @param context 批量删除用户文件的上下文实体
     */
    private void checkFileDeleteCondition(DeleteFileContext context) {
        List<Long> fileIdList = context.getFileIdList();
        Long userId = context.getUserId();

        List<TPanUserFile> userFiles = listByIds(fileIdList);
        if (userFiles.size() != fileIdList.size()) {
            throw new TPanBusinessException("存在不合法的文件ID");
        }

        Set<Long> fileIdSet = userFiles.stream().map(TPanUserFile::getFileId).collect(Collectors.toSet());
        int oldSize = fileIdSet.size();
        fileIdSet.addAll(fileIdList);
        int newSize = fileIdSet.size();
        if (oldSize != newSize) {
            throw new TPanBusinessException("存在不合法的文件ID");
        }

        Set<Long> userIdSet = userFiles.stream().map(TPanUserFile::getUserId).collect(Collectors.toSet());
        if (userIdSet.size() != 1) {
            throw new TPanBusinessException("存在不合法的文件ID");
        }

        Long dbUserId = userIdSet.stream().findFirst().get();

        if (!Objects.equals(dbUserId, userId)) {
            throw new TPanBusinessException("该用户没有删除该文件的权限");
        }
    }

    /**
     * 执行更新文件名称
     *
     * @param context 重命名文件名的上下文实体
     */
    private void doUpdateFilename(UpdateFilenameContext context) {
        TPanUserFile entity = context.getEntity();
        entity.setFilename(context.getNewFilename());
        entity.setUpdateUser(context.getUserId());
        entity.setUpdateTime(new Date());
        if (!updateById(entity)) {
            throw new TPanBusinessException("文件重命名失败");
        }
    }

    /**
     * 更新文件名称的条件校验
     * 1、文件ID是有效的
     * 2、用户有权限更新该文件的文件名称
     * 3、新旧文件名称不能一样
     * 4、不能使用当前文件夹下面的子文件的名称
     *
     * @param context 重命名文件名的上下文实体
     */
    private void checkUpdateFilenameCondition(UpdateFilenameContext context) {
        Long fileId = context.getFileId();
        TPanUserFile entity = getById(fileId);

        if (Objects.isNull(entity)) {
            throw new TPanBusinessException("该文件ID无效");
        }

        if (!Objects.equals(entity.getUserId(), context.getUserId())) {
            throw new TPanBusinessException("当前登录用户没有修改该文件的权限");
        }

        if (Objects.equals(entity.getFilename(), context.getNewFilename())) {
            throw new TPanBusinessException("新旧文件名称不能一致");
        }

        QueryWrapper<TPanUserFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", entity.getParentId());
        queryWrapper.eq("filename", context.getNewFilename());
        int count = count(queryWrapper);
        if (count > 0) {
            throw new TPanBusinessException("该文件名称已存在");
        }

        context.setEntity(entity);
    }

    /**
     * 保存用户文件的映射记录
     *
     * @param parentId       父级目录ID
     * @param filename       文件名称
     * @param folderFlagEnum 是否是文件夹
     * @param fileType       文件类型
     * @param realFileId     真实文件ID
     * @param userId         用户ID
     * @param fileSizeDesc   文件大小描述
     * @return fileId
     */
    private Long saveUserFile(Long parentId, String filename, FolderFlagEnum folderFlagEnum, Integer fileType, Long realFileId, Long userId, String fileSizeDesc) {
        TPanUserFile entity = assembleTPanUserFile(parentId, filename, folderFlagEnum, fileType, realFileId, userId, fileSizeDesc);
        if (!save(entity)) {
            throw new TPanBusinessException("保存文件信息失败");
        }
        return entity.getFileId();
    }

    /**
     * 用户文件映射关系实体转化
     * 1、构建并填充实体
     * 2、处理文件名一致的问题
     *
     * @param parentId       父级目录ID
     * @param filename       文件名称
     * @param folderFlagEnum 是否是文件夹
     * @param fileType       文件类型
     * @param realFileId     真实文件ID
     * @param userId         用户ID
     * @param fileSizeDesc   文件大小描述
     * @return TPanUserFile
     */
    private TPanUserFile assembleTPanUserFile(Long parentId, String filename, FolderFlagEnum folderFlagEnum, Integer fileType, Long realFileId, Long userId, String fileSizeDesc) {
        TPanUserFile entity = new TPanUserFile();
        entity.setFileId(IdUtil.get());
        entity.setUserId(userId);
        entity.setCreateUser(userId);
        entity.setUpdateUser(userId);
        entity.setParentId(parentId);
        entity.setRealFileId(realFileId);
        entity.setFolderFlag(folderFlagEnum.getCode());
        entity.setFileType(fileType);
        entity.setFileSizeDesc(fileSizeDesc);
        entity.setDelFlag(DelFlagEnum.NO.getCode());
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        entity.setFilename(filename);

        // 处理重复的文件名称
        handleDuplicateFilename(entity);

        return entity;
    }

    /**
     * 处理用户重复名称
     * 如果同一文件夹下面有文件名称重复
     * 按照系统规则重命名文件
     *
     * @param entity userFile
     */
    private void handleDuplicateFilename(TPanUserFile entity) {
        String filename = entity.getFilename();
        String newFilenameWithoutSuffix = "";
        String newFilenameSuffix = "";
        int lastPointPosition = filename.lastIndexOf(TPanConstants.POINT_STR);
        if (lastPointPosition == TPanConstants.MINUS_ONE_INT) {
            newFilenameWithoutSuffix = filename;
            newFilenameSuffix = TPanConstants.Empty_STR;
        } else {
            newFilenameWithoutSuffix = filename.substring(0, lastPointPosition);
            newFilenameSuffix = filename.replace(newFilenameWithoutSuffix, TPanConstants.Empty_STR);
        }

        int count = getDuplicateFilename(entity, newFilenameWithoutSuffix);
        if (count == 0) return;

        String newFilename = assembleNewFilename(newFilenameSuffix, newFilenameWithoutSuffix, count);
        entity.setFilename(newFilename);
    }

    private String assembleNewFilename(String newFilenameSuffix, String newFilenameWithoutSuffix, int count) {
        return newFilenameWithoutSuffix + FileConstants.CN_LEFT_PARENTHESES_STR + count + FileConstants.CN_RIGHT_PARENTHESES_STR + newFilenameSuffix;
    }

    private int getDuplicateFilename(TPanUserFile entity, String newFilenameWithoutSuffix) {
        QueryWrapper<TPanUserFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", entity.getParentId());
        queryWrapper.eq("folder_flag", entity.getFolderFlag());
        queryWrapper.eq("user_id", entity.getUserId());
        queryWrapper.eq("del_flag", DelFlagEnum.NO.getCode());
        queryWrapper.likeLeft("filename", newFilenameWithoutSuffix);
        return count(queryWrapper);
    }
}
