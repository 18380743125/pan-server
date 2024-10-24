package com.tangl.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.tangl.pan.core.constants.PanConstants;
import com.tangl.pan.core.exception.PanBusinessException;
import com.tangl.pan.core.utils.FileUtil;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.server.common.stream.channel.PanChannels;
import com.tangl.pan.server.common.stream.event.file.FileDeleteEvent;
import com.tangl.pan.server.common.stream.event.search.UserSearchEvent;
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
import com.tangl.pan.server.modules.file.mapper.PanUserFileMapper;
import com.tangl.pan.server.modules.file.vo.*;
import com.tangl.pan.storage.engine.core.StorageEngine;
import com.tangl.pan.storage.engine.core.context.ReadFileContext;
import com.tangl.pan.stream.core.IStreamProducer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户文件业务层
 */
@Service(value = "userFileService")
public class UserFileServiceImpl extends ServiceImpl<PanUserFileMapper, TPanUserFile> implements IUserFileService {

    @Autowired
    private IFileService fileService;

    @Autowired
    private IFileChunkService fileChunkService;

    @Autowired
    private StorageEngine storageEngine;

    @Autowired
    private FileConverter fileConverter;

    @Autowired
    @Qualifier(value = "defaultStreamProducer")
    private IStreamProducer producer;

    /**
     * 创建文件夹信息
     *
     * @param context context
     * @return 文件夹ID
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
     * @param context 上下文实体
     * @return 用户的文件列表
     */
    @Override
    public List<UserFileVO> getFileList(QueryFileListContext context) {
        return baseMapper.selectFileList(context);
    }

    /**
     * 更新文件名称
     * 1、校验更新文件名称的条件
     * 2、执行更新文件名称的动作
     *
     * @param context 上下文实体
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
     * 3、如果查到记录，直接挂载关联关系，返回秒传成功
     *
     * @param context 上下文实体
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
                FileTypeEnum.getFileTypeCode(FileUtil.getFileSuffix(filename, true)),
                record.getFileId(),
                context.getUserId(),
                record.getFileSizeDesc());
        return true;
    }

    /**
     * 单文件上传
     * 1、上传文件并保存实体文件记录
     * 2、保存用户文件的关系记录
     *
     * @param context 上下文实体
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void upload(FileUploadContext context) {
        saveFile(context);

        saveUserFile(context.getParentId(),
                context.getFilename(),
                FolderFlagEnum.NO,
                FileTypeEnum.getFileTypeCode(FileUtil.getFileSuffix(context.getFilename(), true)),
                context.getRecord().getFileId(),
                context.getUserId(),
                context.getRecord().getFileSizeDesc());
    }

    /**
     * 文件分片上传
     * 1、上传物理分片文件
     * 2、保存分片文件记录
     * 3、校验是否全部分片上传完成
     *
     * @param context 上下文实体
     * @return 是否需要分片合并的实体
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
     * @param context 上下文实体
     * @return 已上传的文件分片编号列表
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
     * @param context 上下文实体
     */
    @Override
    public void mergeFile(FileChunkMergeContext context) {
        mergeFileChunkAndSaveFile(context);
        saveUserFile(context.getParentId(),
                context.getFilename(),
                FolderFlagEnum.NO,
                FileTypeEnum.getFileTypeCode(FileUtil.getFileSuffix(context.getFilename(), true)),
                context.getRecord().getFileId(),
                context.getUserId(),
                context.getRecord().getFileSizeDesc());
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
            throw new PanBusinessException("文件夹暂不支持下载");
        }
        doDownload(record, context.getResponse());
    }

    /**
     * 文件下载
     * 1、参数校验：文件是否存在
     * 2、校验该文件是不是文件夹
     * 3、执行下载的动作
     *
     * @param context 上下文实体
     */
    @Override
    public void downloadWithoutCheckUser(FileDownloadContext context) {
        TPanUserFile record = getById(context.getFileId());
        if (Objects.isNull(record)) {
            throw new PanBusinessException("当前文件记录不存在");
        }
        if (checkIsFolder(record)) {
            throw new PanBusinessException("文件夹暂不支持下载");
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
            throw new PanBusinessException("文件夹暂不支持下载");
        }
        doPreview(record, context.getResponse());
    }

    /**
     * 1、查询该用户所有文件夹列表
     * 2、在内存中拼装文件夹树
     *
     * @param context 上下文实体
     * @return List<FolderTreeNodeVO>
     */
    @Override
    public List<FolderTreeNodeVO> getFolderTree(QueryFolderTreeContext context) {
        List<TPanUserFile> folderRecords = queryFolderRecords(context.getUserId());
        return assembleFolderTreeNodeVOList(folderRecords);
    }

    /**
     * 文件转移
     * 1、权限校验
     * 2、执行动作
     *
     * @param context 上下文实体
     */
    @Override
    public void transferFile(TransferFileContext context) {
        checkTransferCondition(context);
        doTransferFile(context);
    }

    /**
     * 文件复制
     * 1、权限校验
     * 2、执行动作
     *
     * @param context 上下文实体
     */
    @Override
    public void copyFile(CopyFileContext context) {
        checkCopyCondition(context);
        doCopyFile(context);
    }

    /**
     * 文件搜索
     * 1、执行文件搜索
     * 2、拼装文件的父文件夹名称
     * 3、执行文件搜索后的后置动作
     *
     * @param context 上下文实体
     * @return List<FileSearchResultVO>
     */
    @Override
    public List<FileSearchResultVO> search(FileSearchContext context) {
        List<FileSearchResultVO> result = doSearch(context);
        fillParentFilename(result);
        afterSearch(context);
        return result;
    }

    /**
     * 查询面包屑列表
     * 1、获取用户的所有文件夹信息
     * 2、拼装需要用到的面包屑列表
     *
     * @param context 上下文实体
     * @return List<BreadcrumbsVO>
     */
    @Override
    public List<BreadcrumbsVO> getBreadcrumbs(QueryBreadcrumbsContext context) {
        List<TPanUserFile> folderRecords = queryFolderRecords(context.getUserId());
        Map<Long, BreadcrumbsVO> prepareBreadcrumbsVOMap = folderRecords.stream().map(BreadcrumbsVO::transfer).collect(Collectors.toMap(BreadcrumbsVO::getId, a -> a));

        BreadcrumbsVO currentNode;
        Long fileId = context.getFileId();

        List<BreadcrumbsVO> result = Lists.newLinkedList();

        do {
            currentNode = prepareBreadcrumbsVOMap.get(fileId);
            if (Objects.nonNull(currentNode)) {
                result.add(0, currentNode);
                fileId = currentNode.getParentId();
            }
        } while (Objects.nonNull(currentNode));

        return result;
    }

    /**
     * 递归查询所有的子文件信息
     *
     * @param records 实体记录列表
     * @return List<TPanUserFile> 包含子文件实体
     */
    @Override
    public List<TPanUserFile> findAllFileRecords(List<TPanUserFile> records) {
        List<TPanUserFile> result = Lists.newArrayList(records);
        if (CollectionUtils.isEmpty(result)) {
            return result;
        }
        long folderCount = result.stream().filter(record -> Objects.equals(record.getFolderFlag(), FolderFlagEnum.YES.getCode())).count();
        if (folderCount == 0) {
            return result;
        }
        records.forEach(record -> doFindAllChildRecords(result, record));
        return result;
    }

    /**
     * 递归查询所有的子文件信息
     *
     * @param fileIdList 文件 ID 集合
     * @return List<TPanUserFile>
     */
    @Override
    public List<TPanUserFile> findAllFileRecordsByFileIdList(List<Long> fileIdList) {
        if (CollectionUtils.isEmpty(fileIdList)) {
            return Lists.newArrayList();
        }
        List<TPanUserFile> records = listByIds(fileIdList);
        if (CollectionUtils.isEmpty(records)) {
            return Lists.newArrayList();
        }
        return findAllFileRecords(records);
    }

    @Override
    public List<UserFileVO> transferVOList(List<TPanUserFile> records) {
        if (CollectionUtils.isEmpty(records)) {
            return Lists.newArrayList();
        }
        return records.stream().map(record -> fileConverter.tPanUserFile2UserFileVO(record)).collect(Collectors.toList());
    }

    /**
     * 递归查询所有的子文件列表
     * 忽略是否删除标识
     *
     * @param result 结果
     * @param record 文件记录
     */
    private void doFindAllChildRecords(List<TPanUserFile> result, TPanUserFile record) {
        if (Objects.isNull(record) || !checkIsFolder(record)) {
            return;
        }
        List<TPanUserFile> childRecords = findChildRecordsIgnoreDelFlag(record.getFileId());
        if (CollectionUtils.isEmpty(childRecords)) {
            return;
        }
        result.addAll(childRecords);
        childRecords.stream()
                .filter(childRecord -> Objects.equals(childRecord.getFolderFlag(), FolderFlagEnum.YES.getCode()))
                .forEach(childRecord -> doFindAllChildRecords(result, childRecord));
    }

    /**
     * 查询文件夹下面的文件记录，忽略是否删除标识
     *
     * @param fileId 文件ID
     * @return List<TPanUserFile>
     */

    private List<TPanUserFile> findChildRecordsIgnoreDelFlag(Long fileId) {
        QueryWrapper<TPanUserFile> queryWrapper = Wrappers.query();
        queryWrapper.eq("parent_id", fileId);
        return list(queryWrapper);
    }

    /**
     * 搜索的后置操作
     * 1、发布文件搜索的事件
     *
     * @param context 上下文实体
     */
    private void afterSearch(FileSearchContext context) {
        UserSearchEvent userSearchEvent = new UserSearchEvent(context.getKeyword(), context.getUserId());
        producer.sendMessage(PanChannels.USER_SEARCH_OUTPUT, userSearchEvent);
    }

    /**
     * 填充文件列表的父文件名称
     *
     * @param result List<FileSearchResultVO>
     */
    private void fillParentFilename(List<FileSearchResultVO> result) {
        if (CollectionUtils.isEmpty(result)) {
            return;
        }
        List<Long> parentIdList = result.stream().map(FileSearchResultVO::getParentId).collect(Collectors.toList());
        List<TPanUserFile> parentRecords = listByIds(parentIdList);
        Map<Long, String> fileId2FilenameMap = parentRecords.stream().collect(Collectors.toMap(TPanUserFile::getFileId, TPanUserFile::getFilename));
        result.forEach(vo -> vo.setParentFilename(fileId2FilenameMap.get(vo.getParentId())));
    }

    /**
     * 搜索文件
     *
     * @param context 上下文实体
     * @return List<FileSearchResultVO>
     */
    private List<FileSearchResultVO> doSearch(FileSearchContext context) {
        return baseMapper.searchFile(context);
    }

    /**
     * 执行文件复制的动作
     *
     * @param context 上下文实体
     */
    private void doCopyFile(CopyFileContext context) {
        List<TPanUserFile> prepareRecords = context.getPrepareRecords();
        if (CollectionUtils.isEmpty(prepareRecords)) {
            throw new PanBusinessException("选中的文件列表不能为空");
        }

        List<TPanUserFile> allRecords = Lists.newArrayList();
        prepareRecords.forEach(record -> assembleCopyChildRecord(allRecords, record, context.getTargetParentId(), context.getUserId()));
        if (!saveBatch(allRecords)) {
            throw new PanBusinessException("文件复制失败");
        }
    }

    /**
     * 拼装当前文件记录以及所有的子文件夹记录
     *
     * @param allRecords     copy 的文件记录
     * @param record         要 copy 的文件记录
     * @param targetParentId 拷贝到的目标文件夹
     * @param userId         用户ID
     */
    private void assembleCopyChildRecord(List<TPanUserFile> allRecords, TPanUserFile record, Long targetParentId, Long userId) {
        Long newFileId = IdUtil.get();
        Long oldFileId = record.getFileId();

        record.setParentId(targetParentId);
        record.setFileId(newFileId);
        record.setUserId(userId);
        record.setCreateUser(userId);
        record.setCreateTime(new Date());
        record.setUpdateUser(userId);
        record.setUpdateTime(new Date());
        handleDuplicateFilename(record); // 处理重名
        allRecords.add(record);

        if (checkIsFolder(record)) {
            List<TPanUserFile> childRecords = findChildRecords(oldFileId);
            if (CollectionUtils.isEmpty(childRecords)) {
                return;
            }
            childRecords.forEach(childRecord -> assembleCopyChildRecord(allRecords, childRecord, newFileId, userId));
        }
    }

    /**
     * 查找下一级的文件记录
     *
     * @param fileId 文件ID
     * @return List<TPanUserFile>
     */
    private List<TPanUserFile> findChildRecords(Long fileId) {
        QueryWrapper<TPanUserFile> queryWrapper = Wrappers.query();
        queryWrapper.eq("parent_id", fileId);
        queryWrapper.eq("del_flag", DelFlagEnum.NO.getCode());
        return list(queryWrapper);
    }

    /**
     * 文件复制的条件校验
     * 1、目标文件夹必须是文件夹
     * 2、选中要复制的文件列表中不能含有目标文件夹以及其子文件夹中
     *
     * @param context 上下文实体
     */
    private void checkCopyCondition(CopyFileContext context) {
        Long targetParentId = context.getTargetParentId();
        if (!checkIsFolder(getById(targetParentId))) {
            throw new PanBusinessException("目标文件夹不是一个文件夹");
        }

        List<Long> fileIdList = context.getFileIdList();

        List<TPanUserFile> prepareRecords = listByIds(fileIdList);
        if (checkIsChildFolder(prepareRecords, targetParentId, context.getUserId())) {
            throw new PanBusinessException("目标文件夹不能是要复制文件夹及其子文件夹");
        }
        context.setPrepareRecords(prepareRecords);
    }

    /**
     * 执行文件转移的动作
     *
     * @param context 上下文实体
     */
    private void doTransferFile(TransferFileContext context) {
        List<TPanUserFile> prepareRecords = context.getPrepareRecords();
        prepareRecords.forEach(record -> {
            record.setParentId(context.getTargetParentId());
            record.setUserId(context.getUserId());
            record.setCreateUser(context.getUserId());
            record.setCreateTime(new Date());
            record.setUpdateUser(context.getUserId());
            record.setUpdateTime(new Date());
            handleDuplicateFilename(record);
        });
        if (!updateBatchById(prepareRecords)) {
            throw new PanBusinessException("文件转移失败");
        }
    }

    /**
     * 文件转移的条件校验
     * 1、目标文件必须是文件夹
     * 2、选中要转移的文件列表中不能含有目标文件夹以及其子文件夹中
     *
     * @param context 上下文实体
     */
    private void checkTransferCondition(TransferFileContext context) {
        Long targetParentId = context.getTargetParentId();
        if (!checkIsFolder(getById(targetParentId))) {
            throw new PanBusinessException("目标文件夹不是一个文件夹");
        }

        List<Long> fileIdList = context.getFileIdList();

        List<TPanUserFile> prepareRecords = listByIds(fileIdList);
        if (checkIsChildFolder(prepareRecords, targetParentId, context.getUserId())) {
            throw new PanBusinessException("目标文件夹不能是要转移文件夹及其子文件夹");
        }
        context.setPrepareRecords(prepareRecords);
    }

    /**
     * 校验选中的文件列表当中是否含有目标文件夹 ID 以及其子文件夹 ID
     *
     * @param prepareRecords 要转移的文件列表
     * @param targetParentId 目标文件夹ID
     * @param userId         用户ID
     * @return boolean
     */
    private boolean checkIsChildFolder(List<TPanUserFile> prepareRecords, Long targetParentId, Long userId) {
        prepareRecords = prepareRecords.stream().filter(record -> Objects.equals(record.getFolderFlag(), FolderFlagEnum.YES.getCode())).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(prepareRecords)) {
            return false;
        }

        List<TPanUserFile> folderRecords = queryFolderRecords(userId);

        Map<Long, List<TPanUserFile>> folderRecordMap = folderRecords.stream().collect(Collectors.groupingBy(TPanUserFile::getParentId));

        List<TPanUserFile> unavailableFolderRecords = Lists.newArrayList();

        unavailableFolderRecords.addAll(prepareRecords);

        prepareRecords.forEach(record -> findAllChildFolderRecords(unavailableFolderRecords, folderRecordMap, record));

        List<Long> unavailableFolderRecordIds = unavailableFolderRecords.stream().map(TPanUserFile::getFileId).collect(Collectors.toList());

        return unavailableFolderRecordIds.contains(targetParentId);
    }

    /**
     * 查找文件夹下所有子文件夹记录
     *
     * @param unavailableFolderRecords 不可用的文件夹记录
     * @param folderRecordMap          父子文件夹映射
     * @param record                   要转移的文件实体
     */
    private void findAllChildFolderRecords(List<TPanUserFile> unavailableFolderRecords, Map<Long, List<TPanUserFile>> folderRecordMap, TPanUserFile record) {
        if (Objects.isNull(record)) {
            return;
        }

        List<TPanUserFile> childFolderRecords = folderRecordMap.get(record.getFileId());
        if (CollectionUtils.isEmpty(childFolderRecords)) {
            return;
        }

        unavailableFolderRecords.addAll(childFolderRecords);
        childFolderRecords.forEach(childRecord -> findAllChildFolderRecords(unavailableFolderRecords, folderRecordMap, childRecord));
    }

    /**
     * 拼装文件夹树列表
     *
     * @return List<FolderTreeNodeVO>
     */
    private List<FolderTreeNodeVO> assembleFolderTreeNodeVOList(List<TPanUserFile> folderRecords) {
        if (CollectionUtils.isEmpty(folderRecords)) {
            return Lists.newArrayList();
        }

        List<FolderTreeNodeVO> mappedFolderTreeNodeVOList = folderRecords.stream().map(fileConverter::tPanUserFile2FolderTreeNodeVO).collect(Collectors.toList());

        Map<Long, List<FolderTreeNodeVO>> mappedFolderTreeNodeVOMap = mappedFolderTreeNodeVOList.stream().collect(Collectors.groupingBy(FolderTreeNodeVO::getParentId));

        for (FolderTreeNodeVO node : mappedFolderTreeNodeVOList) {
            List<FolderTreeNodeVO> children = mappedFolderTreeNodeVOMap.get(node.getId());
            if (CollectionUtils.isNotEmpty(children)) {
                node.setChildren(children);
            }
        }

        return mappedFolderTreeNodeVOList.stream().filter(node -> Objects.equals(node.getParentId(), FileConstants.TOP_PARENT_ID)).collect(Collectors.toList());
    }

    /**
     * 查询用户所有有效的文件夹信息
     *
     * @return List<TPanUserFile>
     */
    private List<TPanUserFile> queryFolderRecords(Long userId) {
        QueryWrapper<TPanUserFile> queryWrapper = Wrappers.query();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("folder_flag", FolderFlagEnum.YES.getCode());
        queryWrapper.eq("del_flag", DelFlagEnum.NO.getCode());
        return list(queryWrapper);
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
            throw new PanBusinessException("当前的文件记录不存在");
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
            throw new PanBusinessException("当前的文件记录不存在");
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
            throw new PanBusinessException("文件下载失败");
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
            throw new PanBusinessException("文件下载失败");
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
            throw new PanBusinessException("当前文件记录不存在");
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
            throw new PanBusinessException("当前文件记录不存在");
        }
        if (!Objects.equals(record.getUserId(), userId)) {
            throw new PanBusinessException("你没有该文件的操作权限");
        }
    }

    /**
     * 文件分片物理合并并保存文件记录
     *
     * @param context 上下文实体
     */
    private void mergeFileChunkAndSaveFile(FileChunkMergeContext context) {
        FileChunkMergeAndSaveContext fileChunkMergeAndSaveContext = fileConverter.fileChunkMergeContext2FileChunkMergeAndSaveContext(context);
        fileService.mergeFileChunkAndSaveFile(fileChunkMergeAndSaveContext);
        context.setRecord(fileChunkMergeAndSaveContext.getRecord());
    }

    /**
     * 上传文件并保存实体文件记录
     * 委托给文件的 service 完成该操作
     *
     * @param context 上下文实体
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
        return records.get(PanConstants.ZERO_INT);
    }

    /**
     * 发布批量删除文件的事件，给其他模块使用
     *
     * @param context 上下文实体
     */
    private void afterFileDelete(DeleteFileContext context) {
        List<Long> fileIdList = context.getFileIdList();
        FileDeleteEvent deleteFileEvent = new FileDeleteEvent(fileIdList);
        producer.sendMessage(PanChannels.FILE_DELETE_OUTPUT, deleteFileEvent);
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
            throw new PanBusinessException("文件删除失败");
        }
    }

    /**
     * 删除文件前的前置校验
     * 1、文件 ID 合法校验
     * 2、用户拥有删除该文件的权限
     *
     * @param context 上下文实体
     */
    private void checkFileDeleteCondition(DeleteFileContext context) {
        List<Long> fileIdList = context.getFileIdList();
        Long userId = context.getUserId();

        List<TPanUserFile> userFiles = listByIds(fileIdList);
        if (userFiles.size() != fileIdList.size()) {
            throw new PanBusinessException("存在不合法的文件ID");
        }

        Set<Long> fileIdSet = userFiles.stream().map(TPanUserFile::getFileId).collect(Collectors.toSet());
        int oldSize = fileIdSet.size();
        fileIdSet.addAll(fileIdList);
        int newSize = fileIdSet.size();
        if (oldSize != newSize) {
            throw new PanBusinessException("存在不合法的文件ID");
        }

        Set<Long> userIdSet = userFiles.stream().map(TPanUserFile::getUserId).collect(Collectors.toSet());
        if (userIdSet.size() != 1) {
            throw new PanBusinessException("存在不合法的文件ID");
        }

        Long dbUserId = userIdSet.stream().findFirst().get();

        if (!Objects.equals(dbUserId, userId)) {
            throw new PanBusinessException("该用户没有删除该文件的权限");
        }
    }

    /**
     * 执行更新文件名称
     *
     * @param context 上下文实体
     */
    private void doUpdateFilename(UpdateFilenameContext context) {
        TPanUserFile entity = context.getEntity();
        entity.setFilename(context.getNewFilename());
        entity.setUpdateUser(context.getUserId());
        entity.setUpdateTime(new Date());
        if (!updateById(entity)) {
            throw new PanBusinessException("文件重命名失败");
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
            throw new PanBusinessException("该文件ID无效");
        }

        if (!Objects.equals(entity.getUserId(), context.getUserId())) {
            throw new PanBusinessException("当前登录用户没有修改该文件的权限");
        }

        if (Objects.equals(entity.getFilename(), context.getNewFilename())) {
            throw new PanBusinessException("新旧文件名称不能一致");
        }

        QueryWrapper<TPanUserFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", entity.getParentId());
        queryWrapper.eq("filename", context.getNewFilename());
        int count = count(queryWrapper);
        if (count > 0) {
            throw new PanBusinessException("该文件名称已存在");
        }

        context.setEntity(entity);
    }

    /**
     * 保存用户文件的映射记录
     *
     * @param parentId       父文件夹ID
     * @param filename       文件名称
     * @param folderFlagEnum 是否是文件夹
     * @param fileType       文件类型
     * @param realFileId     真实文件ID
     * @param userId         用户ID
     * @param fileSizeDesc   文件大小描述
     * @return 文件ID
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
            throw new PanBusinessException("保存文件信息失败");
        }
        return entity.getFileId();
    }

    /**
     * 用户文件映射关系实体转化
     * 1、构建并填充实体
     * 2、处理文件名一致的问题
     *
     * @param parentId       父文件夹ID
     * @param filename       文件名称
     * @param folderFlagEnum 是否是文件夹
     * @param fileType       文件类型
     * @param realFileId     真实文件ID
     * @param userId         用户ID
     * @param fileSizeDesc   文件大小描述
     * @return TPanUserFile
     */
    private TPanUserFile assembleTPanUserFile(Long parentId,
                                              String filename,
                                              FolderFlagEnum folderFlagEnum,
                                              Integer fileType,
                                              Long realFileId,
                                              Long userId,
                                              String fileSizeDesc) {
        TPanUserFile entity = new TPanUserFile();
        entity.setFileId(IdUtil.get());
        entity.setUserId(userId);
        entity.setParentId(parentId);
        entity.setRealFileId(realFileId);
        entity.setFilename(filename);
        entity.setFolderFlag(folderFlagEnum.getCode());
        entity.setFileSizeDesc(fileSizeDesc);
        entity.setFileType(fileType);
        entity.setDelFlag(DelFlagEnum.NO.getCode());
        entity.setCreateUser(userId);
        entity.setUpdateUser(userId);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());

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
        int lastPointPosition = filename.lastIndexOf(PanConstants.POINT_STR);
        if (lastPointPosition == PanConstants.MINUS_ONE_INT) {
            newFilenameWithoutSuffix = filename;
            newFilenameSuffix = PanConstants.EMPTY_STR;
        } else {
            newFilenameWithoutSuffix = filename.substring(0, lastPointPosition);
            newFilenameSuffix = filename.replace(newFilenameWithoutSuffix, PanConstants.EMPTY_STR);
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
