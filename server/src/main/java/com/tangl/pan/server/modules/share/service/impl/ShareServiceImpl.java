package com.tangl.pan.server.modules.share.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.tangl.pan.bloom.filter.core.BloomFilter;
import com.tangl.pan.bloom.filter.core.BloomFilterManager;
import com.tangl.pan.core.constants.PanConstants;
import com.tangl.pan.core.exception.PanBusinessException;
import com.tangl.pan.core.response.ResponseCode;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.core.utils.JwtUtil;
import com.tangl.pan.core.utils.UUIDUtil;
import com.tangl.pan.server.common.cache.ManualCacheService;
import com.tangl.pan.server.common.config.PanServerConfig;
import com.tangl.pan.server.common.stream.channel.PanChannels;
import com.tangl.pan.server.common.stream.event.log.ErrorLogEvent;
import com.tangl.pan.server.modules.file.constants.FileConstants;
import com.tangl.pan.server.modules.file.context.CopyFileContext;
import com.tangl.pan.server.modules.file.context.FileDownloadContext;
import com.tangl.pan.server.modules.file.context.QueryFileListContext;
import com.tangl.pan.server.modules.file.entity.TPanUserFile;
import com.tangl.pan.server.modules.file.enums.DelFlagEnum;
import com.tangl.pan.server.modules.file.service.IUserFileService;
import com.tangl.pan.server.modules.file.vo.UserFileVO;
import com.tangl.pan.server.modules.share.constants.ShareConstants;
import com.tangl.pan.server.modules.share.context.*;
import com.tangl.pan.server.modules.share.entity.PanShare;
import com.tangl.pan.server.modules.share.entity.PanShareFile;
import com.tangl.pan.server.modules.share.enums.ShareDayTypeEnum;
import com.tangl.pan.server.modules.share.enums.ShareStatusEnum;
import com.tangl.pan.server.modules.share.service.IShareFileService;
import com.tangl.pan.server.modules.share.service.IShareService;
import com.tangl.pan.server.modules.share.mapper.PanShareMapper;
import com.tangl.pan.server.modules.share.vo.*;
import com.tangl.pan.server.modules.user.entity.PanUser;
import com.tangl.pan.server.modules.user.service.IUserService;
import com.tangl.pan.stream.core.IStreamProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 分享业务层
 */
@Service
@Slf4j
public class ShareServiceImpl extends ServiceImpl<PanShareMapper, PanShare> implements IShareService {

    private static final String BLOOM_FILTER_NAME = "SHARE_SIMPLE_DETAIL";

    @Autowired
    private PanServerConfig config;

    @Autowired
    private IShareFileService shareFileService;

    @Autowired
    private IUserFileService userFileService;

    @Autowired
    private IUserService userService;

    @Autowired
    @Qualifier(value = "shareManualCacheService")
    private ManualCacheService<PanShare> cacheService;

    @Autowired
    private BloomFilterManager manager;

    @Autowired
    @Qualifier(value = "defaultStreamProducer")
    private IStreamProducer producer;

    /**
     * 创建分享链接
     * 1、拼装分享实体，保存到数据库
     * 2、保存分享和对应文件的关联关系
     * 3、拼装返回实体并返回
     *
     * @param context 上下文实体
     * @return ShareUrlVO
     */
    @Transactional(rollbackFor = PanBusinessException.class)
    @Override
    public ShareUrlVO create(CreateShareUrlContext context) {
        saveShare(context);
        saveShareFiles(context);
        ShareUrlVO shareUrlVO = assembleShareVO(context);
        afterCreate(context);
        return shareUrlVO;
    }

    @Override
    public List<ShareUrlListVO> getShares(QueryShareListContext context) {
        return baseMapper.selectShareVOListByUserId(context.getUserId());
    }

    /**
     * 取消分享
     * 1、校验用户操作权限
     * 2、删除对应的分享记录
     * 3、删除对应的文件分享关联关系记录
     *
     * @param context 上下文实体
     */
    @Transactional(rollbackFor = PanBusinessException.class)
    @Override
    public void cancelShare(CancelShareContext context) {
        checkUserCancelSharePermission(context);
        doCancelShare(context);
        doCancelShareFiles(context);
    }

    /**
     * 校验分享码
     * 1、校验分享的状态是否正常
     * 2、校验分享码是否正确
     * 3、生成一个短时间的分享 token
     *
     * @param context 上下文实体
     * @return token
     */
    @Override
    public String checkShareCode(CheckShareCodeContext context) {
        PanShare record = checkShareStatus(context.getShareId());
        context.setRecord(record);
        doCheckShareCode(context);
        return generateShareToken(context);
    }

    /**
     * 查询分享详情
     * 1、校验分享的状态
     * 2、初始化分享实体
     * 3、查询分享的主体信息
     * 4、查询分享的文件列表
     * 5、查询分享者的信息
     *
     * @param context 上下文实体
     * @return ShareDetailVO
     */
    @Override
    public ShareDetailVO detail(QueryShareDetailContext context) {
        PanShare record = checkShareStatus(context.getShareId());
        context.setRecord(record);
        initShareVO(context);
        assembleMainShareInfo(context);
        assembleShareFilesInfo(context);
        assembleShareUserInfo(context);
        return context.getVo();
    }

    /**
     * 查询分享详情
     * 1、校验分享的状态
     * 2、初始化简单分享实体
     * 3、查询分享的主体信息
     * 4、查询分享者的信息
     *
     * @param context 上下文实体
     * @return ShareSimpleDetailVO
     */
    @Override
    public ShareSimpleDetailVO simpleDetail(QueryShareSimpleDetailContext context) {
        PanShare record = checkShareStatus(context.getShareId());
        context.setRecord(record);
        initShareSimpleVO(context);
        assembleMainShareSimpleInfo(context);
        assembleShareSimpleUserInfo(context);
        return context.getVo();
    }

    /**
     * 获取下一级的文件列表
     * 1、校验分享的状态
     * 2、校验文件的 ID 在分享列表中
     * 3、查询对应目录的子文件列表并返回
     *
     * @param context 上下文实体
     * @return List<UserFileVO>
     */
    @Override
    public List<UserFileVO> fileList(QueryChildFileListContext context) {
        PanShare record = checkShareStatus(context.getShareId());
        context.setRecord(record);
        List<UserFileVO> allShareUserFileRecords = checkFileIdOnShareStatusAndGetAllShareUserFiles(context.getShareId(), Lists.newArrayList(context.getParentId()));
        Map<Long, List<UserFileVO>> parentIdFileListMap = allShareUserFileRecords.stream().collect(Collectors.groupingBy(UserFileVO::getParentId));
        List<UserFileVO> userFileVOList = parentIdFileListMap.get(context.getParentId());
        if (CollectionUtils.isEmpty(userFileVOList)) {
            return Lists.newArrayList();
        }
        return userFileVOList;
    }

    /**
     * 转存到我的网盘
     * 1、校验分享状态
     * 2、校验文件 ID 是否合法
     * 3、委托文件模块做文件拷贝的操作
     *
     * @param context 上下文实体
     */
    @Override
    public void saveFiles(ShareSaveContext context) {
        checkShareStatus(context.getShareId());
        checkFileIdOnShareStatus(context.getShareId(), context.getFileIdList());
        doSaveFiles(context);
    }

    /**
     * 分享的文件下载
     * 1、校验分享状态
     * 2、校验文件 ID 的合法性
     * 3、执行分享文件下载的动作
     *
     * @param context 上下文实体
     */
    @Override
    public void download(ShareFileDownloadContext context) {
        checkShareStatus(context.getShareId());
        checkFileIdOnShareStatus(context.getShareId(), Lists.newArrayList(context.getFileId()));
        doDownload(context);
    }

    /**
     * 刷新受影响的对应分享的状态
     * 1、查询所有受影响的分享 ID 集合
     * 2、判断每一个分享对应的文件以及所有的父文件信息均为正常，该种情况，把分享的状态变为正常
     * 3、如果有分享的文件或者其父文件信息被删除，变更该分享的状态为有文件被删除
     *
     * @param allAvailableFileIdList fileIdList
     */
    @Override
    public void refreshShareStatus(List<Long> allAvailableFileIdList) {
        List<Long> shareIdList = getShareIdListByFileIdList(allAvailableFileIdList);
        if (CollectionUtils.isEmpty(shareIdList)) {
            return;
        }
        Set<Long> shareIdSet = new HashSet<>(shareIdList);
        shareIdSet.forEach(this::refreshOneShareStatus);
    }

    /**
     * 滚动查询已存在的分享 ID
     *
     * @param startId 开始
     * @param limit   条数
     * @return 分享 ID 列表
     */
    @Override
    public List<Long> rollingQueryShareId(long startId, long limit) {
        return baseMapper.rollingQueryShareId(startId, limit);
    }

    @Override
    public boolean removeById(Serializable id) {
        return cacheService.removeById(id);
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        return cacheService.removeByIds(idList);
    }

    @Override
    public boolean updateById(PanShare entity) {
        return cacheService.updateById(entity.getShareId(), entity);
    }


    @Override
    public boolean updateBatchById(Collection<PanShare> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return true;
        }
        Map<Long, PanShare> entityMap = entityList.stream().collect(Collectors.toMap(PanShare::getShareId, e -> e));
        return cacheService.updateByIds(entityMap);
    }

    @Override
    public PanShare getById(Serializable id) {
        return cacheService.getById(id);
    }

    @Override
    public List<PanShare> listByIds(Collection<? extends Serializable> idList) {
        return cacheService.getByIds(idList);
    }

    /**
     * 刷新一个分享的状态
     * 1、查询对应的分享信息，判断有效
     * 2、判断分享对应的文件以及所有的父文件信息均为正常，该种情况，把分享的状态变为正常
     * 3、如果有分享的文件或者其父文件信息被删除，变更该分享的状态为有文件被删除
     *
     * @param shareId 分享 ID
     */
    private void refreshOneShareStatus(Long shareId) {
        PanShare record = getById(shareId);
        if (Objects.isNull(record)) {
            return;
        }

        ShareStatusEnum shareStatus = ShareStatusEnum.NORMAL;
        if (!checkShareFileAvailable(shareId)) {
            shareStatus = ShareStatusEnum.FILE_DELETE;
        }

        if (Objects.equals(record.getShareStatus(), shareStatus.getCode())) {
            return;
        }

        doChangeShareStatus(record, shareStatus);
    }

    /**
     * 执行刷新分享状态的动作
     *
     * @param record      分享的实体记录
     * @param shareStatus 分享变更的状态
     */
    private void doChangeShareStatus(PanShare record, ShareStatusEnum shareStatus) {
        record.setShareStatus(shareStatus.getCode());
        if (!updateById(record)) {
            ErrorLogEvent errorLogEvent = new ErrorLogEvent("更新分享状态失败，请手动更改状态，分享的ID为：" +
                    record.getShareId() + "，分享状态改为：" + shareStatus.getCode(), PanConstants.ZERO_LONG);
            producer.sendMessage(PanChannels.ERROR_LOG_OUTPUT, errorLogEvent);
        }
    }

    /**
     * 检查该分享所有的文件以及所有的父文件均为正常状态
     *
     * @param shareId 分享的 ID
     */
    private boolean checkShareFileAvailable(Long shareId) {
        List<Long> shareFileIdList = getShareFileIdList(shareId);
        for (Long fileId : shareFileIdList) {
            if (!checkUpFileAvailable(fileId)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查该文件以及所有的父文件信息均为正常状态
     *
     * @param fileId 文件 ID
     * @return boolean
     */
    private boolean checkUpFileAvailable(Long fileId) {
        TPanUserFile record = userFileService.getById(fileId);
        if (Objects.isNull(record)) {
            return false;
        }

        if (Objects.equals(record.getDelFlag(), DelFlagEnum.YES.getCode())) {
            return false;
        }
        if (Objects.equals(record.getParentId(), FileConstants.TOP_PARENT_ID)) {
            return true;
        }

        return checkUpFileAvailable(record.getParentId());
    }

    /**
     * 通过文件 ID 列表查询所有受影响的分享 ID 集合
     *
     * @param allAvailableFileIdList fileIdList
     * @return 受影响的分享的 ID 列表
     */
    private List<Long> getShareIdListByFileIdList(List<Long> allAvailableFileIdList) {
        QueryWrapper<PanShareFile> queryWrapper = Wrappers.query();
        queryWrapper.select("share_id");
        queryWrapper.in("file_id", allAvailableFileIdList);
        return shareFileService.listObjs(queryWrapper, value -> (Long) value);
    }

    /**
     * 执行分享文件下载的动作
     * 委托文件模块去做
     *
     * @param context 上下文实体
     */
    private void doDownload(ShareFileDownloadContext context) {
        FileDownloadContext fileDownloadContext = new FileDownloadContext();
        fileDownloadContext.setFileId(context.getFileId());
        fileDownloadContext.setResponse(context.getResponse());
        fileDownloadContext.setUserId(context.getUserId());
        userFileService.downloadWithoutCheckUser(fileDownloadContext);
    }

    /**
     * 执行保存到我的网盘动作
     *
     * @param context 上下文实体
     */
    private void doSaveFiles(ShareSaveContext context) {
        CopyFileContext copyFileContext = new CopyFileContext();
        copyFileContext.setUserId(context.getUserId());
        copyFileContext.setTargetParentId(context.getTargetParentId());
        copyFileContext.setFileIdList(context.getFileIdList());
        userFileService.copyFile(copyFileContext);
    }

    /**
     * 校验文件 ID 列表是否属于某一个分享
     *
     * @param shareId    分享 ID
     * @param fileIdList 文件 ID 列表
     */
    private void checkFileIdOnShareStatus(Long shareId, List<Long> fileIdList) {
        checkFileIdOnShareStatusAndGetAllShareUserFiles(shareId, fileIdList);

    }

    /**
     * 校验文件是否处于分享的状态，返回该分享的所有文件列表
     *
     * @param shareId    分享的ID
     * @param fileIdList 文件 ID 集合
     * @return List<UserFileVO>
     */
    private List<UserFileVO> checkFileIdOnShareStatusAndGetAllShareUserFiles(Long shareId, List<Long> fileIdList) {
        List<Long> shareFileIdList = getShareFileIdList(shareId);
        if (CollectionUtils.isEmpty(shareFileIdList)) {
            return Lists.newArrayList();
        }
        List<TPanUserFile> allFileRecords = userFileService.findAllFileRecordsByFileIdList(shareFileIdList);
        if (CollectionUtils.isEmpty(allFileRecords)) {
            return Lists.newArrayList();
        }

        allFileRecords = allFileRecords.stream()
                .filter(Objects::nonNull)
                .filter(record -> Objects.equals(record.getDelFlag(), DelFlagEnum.NO.getCode()))
                .collect(Collectors.toList());

        List<Long> allFileIdList = allFileRecords.stream().map(TPanUserFile::getFileId).collect(Collectors.toList());

        if (new HashSet<>(allFileIdList).containsAll(fileIdList)) {
            return userFileService.transferVOList(allFileRecords);
        }

        throw new PanBusinessException(ResponseCode.SHARE_FILE_MISS);
    }

    /**
     * 拼装分享简单详情的分享者信息
     *
     * @param context 上下文实体
     */
    private void assembleShareSimpleUserInfo(QueryShareSimpleDetailContext context) {
        PanUser record = userService.getById(context.getRecord().getCreateUser());
        if (Objects.isNull(record)) {
            throw new PanBusinessException("分享者信息查询失败");
        }
        ShareUserInfoVO shareUserInfoVO = new ShareUserInfoVO();
        shareUserInfoVO.setUserId(record.getUserId());
        shareUserInfoVO.setUsername(encryptUsername(record.getUsername()));

        context.getVo().setShareUserInfoVO(shareUserInfoVO);
    }

    /**
     * 填充分享简单主体信息
     *
     * @param context 上下文实体
     */
    private void assembleMainShareSimpleInfo(QueryShareSimpleDetailContext context) {
        PanShare record = context.getRecord();
        ShareSimpleDetailVO vo = context.getVo();
        vo.setShareId(record.getShareId());
        vo.setShareName(record.getShareName());
    }

    /**
     * 初始化分享简单 VO
     *
     * @param context 上下文实体
     */
    private void initShareSimpleVO(QueryShareSimpleDetailContext context) {
        ShareSimpleDetailVO vo = new ShareSimpleDetailVO();
        context.setVo(vo);
    }

    /**
     * 查询分享者的信息
     *
     * @param context 上下文实体
     */
    private void assembleShareUserInfo(QueryShareDetailContext context) {
        PanUser record = userService.getById(context.getRecord().getCreateUser());
        if (Objects.isNull(record)) {
            throw new PanBusinessException("分享者信息查询失败");
        }
        ShareUserInfoVO shareUserInfoVO = new ShareUserInfoVO();
        shareUserInfoVO.setUserId(record.getUserId());
        shareUserInfoVO.setUsername(encryptUsername(record.getUsername()));

        context.getVo().setShareUserInfoVO(shareUserInfoVO);
    }

    /**
     * 加密用户名称
     *
     * @param username 用户名
     * @return String
     */
    private String encryptUsername(String username) {
        StringBuffer stringBuffer = new StringBuffer(username);
        stringBuffer.replace(PanConstants.TWO_INT, username.length() - PanConstants.TWO_INT, PanConstants.COMMON_ENCRYPT_STR);
        return stringBuffer.toString();
    }

    /**
     * 查询分享的文件列表
     * 1、查询分享对应的文件 ID 集合
     * 2、根据文件 ID 查询文件列表信息
     *
     * @param context 上下文实体
     */
    private void assembleShareFilesInfo(QueryShareDetailContext context) {
        List<Long> fileIdList = getShareFileIdList(context.getShareId());

        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setUserId(context.getRecord().getCreateUser());
        queryFileListContext.setDelFlag(DelFlagEnum.NO.getCode());
        queryFileListContext.setFileIdList(fileIdList);
        List<UserFileVO> userFileVOList = userFileService.getFileList(queryFileListContext);
        context.getVo().setUserFileVOList(userFileVOList);
    }

    /**
     * 查询分享的文件 ID 集合
     *
     * @param shareId 分享的 ID
     * @return 文件 ID 集合
     */
    private List<Long> getShareFileIdList(Long shareId) {
        if (Objects.isNull(shareId)) {
            return Lists.newArrayList();
        }
        QueryWrapper<PanShareFile> queryWrapper = Wrappers.query();
        queryWrapper.select("file_id");
        queryWrapper.eq("share_id", shareId);
        return shareFileService.listObjs(queryWrapper, value -> (Long) value);
    }

    /**
     * 查询分享的主体信息
     *
     * @param context 上下文实体
     */
    private void assembleMainShareInfo(QueryShareDetailContext context) {
        PanShare record = context.getRecord();
        ShareDetailVO vo = context.getVo();
        vo.setShareId(record.getShareId());
        vo.setShareName(record.getShareName());
        vo.setShareDay(record.getShareDay());
        vo.setCreateTime(record.getCreateTime());
        vo.setShareEndTime(record.getShareEndTime());
    }

    /**
     * 初始化分享详情的 VO 实体
     *
     * @param context 上下文实体
     */
    private void initShareVO(QueryShareDetailContext context) {
        ShareDetailVO vo = new ShareDetailVO();
        context.setVo(vo);
    }

    /**
     * 生成一个短期的 token
     *
     * @param context 上下文实体
     * @return token
     */
    private String generateShareToken(CheckShareCodeContext context) {
        PanShare record = context.getRecord();
        return JwtUtil.generateToken(UUIDUtil.getUUID(), ShareConstants.SHARE_ID, record.getShareId(), ShareConstants.ONE_HOUR_LONG);
    }

    /**
     * 校验分享码是否正确
     *
     * @param context 上下文实体
     */
    private void doCheckShareCode(CheckShareCodeContext context) {
        PanShare record = context.getRecord();
        if (!Objects.equals(record.getShareCode(), context.getShareCode())) {
            throw new PanBusinessException("分享码错误");
        }
    }

    /**
     * 校验分享的状态是否正常
     *
     * @param shareId 分享的 ID
     * @return 分享的实体记录
     */
    private PanShare checkShareStatus(Long shareId) {
        PanShare record = getById(shareId);
        if (Objects.isNull(record)) {
            throw new PanBusinessException(ResponseCode.SHARE_CANCELLED);
        }

        if (Objects.equals(ShareStatusEnum.FILE_DELETE.getCode(), record.getShareStatus())) {
            throw new PanBusinessException(ResponseCode.SHARE_FILE_MISS);
        }

        if (Objects.equals(ShareDayTypeEnum.PERMANENT_VALIDITY.getCode(), record.getShareDayType())) {
            return record;
        }

        if (record.getShareEndTime().before(new Date())) {
            throw new PanBusinessException(ResponseCode.SHARE_EXPIRE);
        }
        return record;
    }

    /**
     * 删除对应的文件分享关联关系记录
     *
     * @param context 上下文实体
     */
    private void doCancelShareFiles(CancelShareContext context) {
        List<Long> shareIdList = context.getShareIdList();
        Long userId = context.getUserId();
        QueryWrapper<PanShareFile> queryWrapper = Wrappers.query();
        queryWrapper.in("share_id", shareIdList);
        queryWrapper.eq("create_user", userId);
        if (!shareFileService.remove(queryWrapper)) {
            throw new PanBusinessException("取消分享失败");
        }
    }

    /**
     * 删除对应的分享记录
     *
     * @param context 上下文实体
     */
    private void doCancelShare(CancelShareContext context) {
        List<Long> shareIdList = context.getShareIdList();
        if (!removeByIds(shareIdList)) {
            throw new PanBusinessException("取消分享失败");
        }
    }

    /**
     * 校验用户操作权限
     *
     * @param context 上下文实体
     */
    private void checkUserCancelSharePermission(CancelShareContext context) {
        List<Long> shareIdList = context.getShareIdList();
        Long userId = context.getUserId();
        List<PanShare> records = listByIds(shareIdList);
        if (CollectionUtils.isEmpty(records)) {
            throw new PanBusinessException("无权限取消分享");
        }
        for (PanShare record : records) {
            if (!Objects.equals(record.getCreateUser(), userId)) {
                throw new PanBusinessException("无权限取消分享");
            }
        }
    }

    /**
     * 创建分享链接后置处理
     *
     * @param context 上下文实体
     */
    private void afterCreate(CreateShareUrlContext context) {
        BloomFilter<Long> bloomFilter = manager.getFilter(BLOOM_FILTER_NAME);
        if (Objects.isNull(bloomFilter)) {
            return;
        }
        bloomFilter.put(context.getRecord().getShareId());
        log.info("create share, add share id to bloom filter, share id is {}", context.getRecord().getShareId());
    }

    /**
     * 拼装返回实体
     *
     * @param context 上下文实体
     * @return ShareUrlVO
     */
    private ShareUrlVO assembleShareVO(CreateShareUrlContext context) {
        PanShare record = context.getRecord();
        ShareUrlVO shareUrlVO = new ShareUrlVO();
        shareUrlVO.setShareId(record.getShareId());
        shareUrlVO.setShareName(record.getShareName());
        shareUrlVO.setShareUrl(record.getShareUrl());
        shareUrlVO.setShareCode(record.getShareCode());
        shareUrlVO.setShareStatus(record.getShareStatus());
        return shareUrlVO;
    }

    /**
     * 保存分享文件的关联关系
     *
     * @param context 上下文实体
     */
    private void saveShareFiles(CreateShareUrlContext context) {
        SaveShareFilesContext saveShareFilesContext = new SaveShareFilesContext();
        saveShareFilesContext.setShareId(context.getRecord().getShareId());
        saveShareFilesContext.setUserId(context.getUserId());
        saveShareFilesContext.setShareFileIdList(context.getShareFileIdList());
        shareFileService.saveShareFiles(saveShareFilesContext);
    }

    /**
     * 拼装分享实体，保存到数据库
     *
     * @param context 上下文实体
     */
    private void saveShare(CreateShareUrlContext context) {
        PanShare record = new PanShare();
        record.setShareId(IdUtil.get());
        record.setShareName(context.getShareName());
        record.setShareType(context.getShareType());
        record.setShareDayType(context.getShareDayType());
        Integer shareDays = ShareDayTypeEnum.getShareDaysByCode(context.getShareDayType());
        if (Objects.equals(shareDays, PanConstants.MINUS_ONE_INT)) {
            throw new PanBusinessException("分享天数类型非法");
        }
        record.setShareDay(shareDays);
        record.setShareEndTime(DateUtil.offsetDay(new Date(), shareDays));
        record.setShareUrl(createShareUrl(record.getShareId()));
        record.setShareCode(createShareCode());
        record.setShareStatus(ShareStatusEnum.NORMAL.getCode());
        record.setCreateUser(context.getUserId());
        record.setCreateTime(new Date());
        if (!save(record)) {
            throw new PanBusinessException("保存分享信息失败");
        }
        context.setRecord(record);
    }

    /**
     * 创建分享码
     *
     * @return 分享码
     */
    private String createShareCode() {
        return RandomStringUtils.randomAlphabetic(4).toLowerCase();
    }

    /**
     * 创建分享的 URL
     *
     * @param shareId 分享 ID
     * @return 分享链接
     */
    private String createShareUrl(Long shareId) {
        if (Objects.isNull(shareId)) {
            throw new PanBusinessException("分享的ID不能为空");
        }
        String sharePrefix = config.getSharePrefix();
        if (sharePrefix.lastIndexOf(PanConstants.SLASH_STR) == PanConstants.MINUS_ONE_INT) {
            sharePrefix += PanConstants.SLASH_STR;
        }
        String shareUrl = "";
        try {
            shareUrl = sharePrefix + URLEncoder.encode(IdUtil.encrypt(shareId), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return shareUrl;
    }
}
