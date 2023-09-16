package com.tangl.pan.server.modules.share.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.tangl.pan.core.constants.TPanConstants;
import com.tangl.pan.core.exception.TPanBusinessException;
import com.tangl.pan.core.response.ResponseCode;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.core.utils.JwtUtil;
import com.tangl.pan.core.utils.UUIDUtil;
import com.tangl.pan.server.common.config.PanServerConfig;
import com.tangl.pan.server.modules.share.constants.ShareConstants;
import com.tangl.pan.server.modules.share.context.*;
import com.tangl.pan.server.modules.share.entity.TPanShare;
import com.tangl.pan.server.modules.share.entity.TPanShareFile;
import com.tangl.pan.server.modules.share.enums.ShareDayTypeEnum;
import com.tangl.pan.server.modules.share.enums.ShareStatusEnum;
import com.tangl.pan.server.modules.share.service.IShareFileService;
import com.tangl.pan.server.modules.share.service.IShareService;
import com.tangl.pan.server.modules.share.mapper.TPanShareMapper;
import com.tangl.pan.server.modules.share.vo.ShareUrlListVO;
import com.tangl.pan.server.modules.share.vo.ShareUrlVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author 25050
 * @description 针对表【t_pan_share(用户分享表)】的数据库操作Service实现
 * @createDate 2023-07-23 23:42:53
 */
@Service
public class ShareServiceImpl extends ServiceImpl<TPanShareMapper, TPanShare> implements IShareService {

    @Autowired
    private PanServerConfig config;

    @Autowired
    private IShareFileService shareFileService;

    /**
     * 创建分享链接
     * 1、拼装分享实体，保存到数据库
     * 2、保存分享和对应文件的关联关系
     * 3、拼装返回实体并返回
     *
     * @param context 上下文实体
     * @return ShareUrlVO
     */
    @Transactional(rollbackFor = TPanBusinessException.class)
    @Override
    public ShareUrlVO create(CreateShareUrlContext context) {
        saveShare(context);
        saveShareFiles(context);
        return assembleShareVO(context);
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
    @Transactional(rollbackFor = TPanBusinessException.class)
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
        TPanShare record = checkShareStatus(context.getShareId());
        context.setRecord(record);
        doCheckShareCode(context);
        return generateShareToken(context);
    }

    /**
     * 生成一个短期的 token
     *
     * @param context 上下文实体
     * @return token
     */
    private String generateShareToken(CheckShareCodeContext context) {
        TPanShare record = context.getRecord();
        return JwtUtil.generateToken(UUIDUtil.getUUID(), ShareConstants.SHARE_ID, record.getShareId(), ShareConstants.ONE_HOUR_LONG);
    }

    /**
     * 校验分享码是否正确
     *
     * @param context 上下文实体
     */
    private void doCheckShareCode(CheckShareCodeContext context) {
        TPanShare record = context.getRecord();
        if (!Objects.equals(record.getShareCode(), context.getShareCode())) {
            throw new TPanBusinessException("分享码错误");
        }
    }

    /**
     * 校验分享的状态是否正常
     *
     * @param shareId 分享的 ID
     * @return 分享的实体记录
     */
    private TPanShare checkShareStatus(Long shareId) {
        TPanShare record = getById(shareId);
        if (Objects.isNull(record)) {
            throw new TPanBusinessException(ResponseCode.SHARE_CANCELLED);
        }

        if (Objects.equals(ShareStatusEnum.FILE_DELETE.getCode(), record.getShareStatus())) {
            throw new TPanBusinessException(ResponseCode.SHARE_FILE_MISS);
        }

        if (Objects.equals(ShareDayTypeEnum.PERMANENT_VALIDITY.getCode(), record.getShareDayType())) {
            return record;
        }

        if (record.getShareEndTime().before(new Date())) {
            throw new TPanBusinessException(ResponseCode.SHARE_EXPIRE);
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
        QueryWrapper<TPanShareFile> queryWrapper = Wrappers.query();
        queryWrapper.in("share_id", shareIdList);
        queryWrapper.eq("create_user", userId);
        if (!shareFileService.remove(queryWrapper)) {
            throw new TPanBusinessException("取消分享失败");
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
            throw new TPanBusinessException("取消分享失败");
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
        List<TPanShare> records = listByIds(shareIdList);
        if (CollectionUtils.isEmpty(records)) {
            throw new TPanBusinessException("无权限取消分享");
        }
        for (TPanShare record : records) {
            if (!Objects.equals(record.getCreateUser(), userId)) {
                throw new TPanBusinessException("无权限取消分享");
            }
        }
    }

    /**
     * 拼装返回实体
     *
     * @param context 上下文实体
     * @return ShareUrlVO
     */
    private ShareUrlVO assembleShareVO(CreateShareUrlContext context) {
        TPanShare record = context.getRecord();
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
        TPanShare record = new TPanShare();
        record.setShareId(IdUtil.get());
        record.setShareName(context.getShareName());
        record.setShareType(context.getShareType());
        record.setShareDayType(context.getShareDayType());
        Integer shareDays = ShareDayTypeEnum.getShareDaysByCode(context.getShareDayType());
        if (Objects.equals(shareDays, TPanConstants.MINUS_ONE_INT)) {
            throw new TPanBusinessException("分享天数类型非法");
        }
        record.setShareDay(shareDays);
        record.setShareEndTime(DateUtil.offsetDay(new Date(), shareDays));
        record.setShareUrl(createShareUrl(record.getShareId()));
        record.setShareCode(createShareCode());
        record.setShareStatus(ShareStatusEnum.NORMAL.getCode());
        record.setCreateUser(context.getUserId());
        record.setCreateTime(new Date());
        if (!save(record)) {
            throw new TPanBusinessException("保存分享信息失败");
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
            throw new TPanBusinessException("分享的ID不能为空");
        }
        String sharePrefix = config.getSharePrefix();
        if (sharePrefix.lastIndexOf(TPanConstants.SLASH_STR) == TPanConstants.MINUS_ONE_INT) {
            sharePrefix += TPanConstants.SLASH_STR;
        }
        return sharePrefix + shareId;
    }
}
