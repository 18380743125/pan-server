package com.tangl.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangl.pan.core.constants.TPanConstants;
import com.tangl.pan.core.exception.TPanBusinessException;
import com.tangl.pan.core.utils.FileUtil;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.server.common.event.file.DeleteFileEvent;
import com.tangl.pan.server.modules.file.constants.FileConstants;
import com.tangl.pan.server.modules.file.context.*;
import com.tangl.pan.server.modules.file.entity.TPanFile;
import com.tangl.pan.server.modules.file.entity.TPanUserFile;
import com.tangl.pan.server.modules.file.enums.DelFlagEnum;
import com.tangl.pan.server.modules.file.enums.FileTypeEnum;
import com.tangl.pan.server.modules.file.enums.FolderFlagEnum;
import com.tangl.pan.server.modules.file.service.IFileService;
import com.tangl.pan.server.modules.file.service.IUserFileService;
import com.tangl.pan.server.modules.file.mapper.TPanUserFileMapper;
import com.tangl.pan.server.modules.file.vo.UserFileVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

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
        return saveUserFile(context.getParentId(),
                context.getFolderName(),
                FolderFlagEnum.YES,
                null,
                null,
                context.getUserId(),
                null);
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
        saveUserFile(context.getParentId(),
                context.getFilename(),
                FolderFlagEnum.NO,
                FileTypeEnum.getFileTypeCode(FileUtil.getFileSuffix(filename)),
                record.getFileId(),
                context.getUserId(),
                record.getFileSizeDesc()
        );
        return true;
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
    private Long saveUserFile(Long parentId,
                              String filename,
                              FolderFlagEnum folderFlagEnum,
                              Integer fileType,
                              Long realFileId,
                              Long userId,
                              String fileSizeDesc) {
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
        return newFilenameWithoutSuffix +
                FileConstants.CN_LEFT_PARENTHESES_STR +
                count +
                FileConstants.CN_RIGHT_PARENTHESES_STR +
                newFilenameSuffix;
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




