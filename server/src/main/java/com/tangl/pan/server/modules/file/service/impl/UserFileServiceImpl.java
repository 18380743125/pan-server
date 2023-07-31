package com.tangl.pan.server.modules.file.service.impl;

import cn.hutool.core.text.StrBuilder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangl.pan.core.constants.TPanConstants;
import com.tangl.pan.core.exception.TPanBusinessException;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.server.modules.file.constants.FileConstants;
import com.tangl.pan.server.modules.file.context.CreateFolderContext;
import com.tangl.pan.server.modules.file.entity.TPanUserFile;
import com.tangl.pan.server.modules.file.enums.DelFlagEnum;
import com.tangl.pan.server.modules.file.enums.FolderFlagEnum;
import com.tangl.pan.server.modules.file.service.IUserFileService;
import com.tangl.pan.server.modules.file.mapper.TPanUserFileMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author tangl
 * @description 用户文件业务层
 * @createDate 2023-07-23 23:41:43
 */
@Service(value = "userFileService")
public class UserFileServiceImpl extends ServiceImpl<TPanUserFileMapper, TPanUserFile> implements IUserFileService {
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
     * @return
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




