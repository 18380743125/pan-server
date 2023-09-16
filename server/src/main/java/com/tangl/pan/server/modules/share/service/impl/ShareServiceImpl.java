package com.tangl.pan.server.modules.share.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.tangl.pan.core.constants.TPanConstants;
import com.tangl.pan.core.exception.TPanBusinessException;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.server.common.config.PanServerConfig;
import com.tangl.pan.server.modules.share.context.CreateShareUrlContext;
import com.tangl.pan.server.modules.share.context.SaveShareFilesContext;
import com.tangl.pan.server.modules.share.entity.TPanShare;
import com.tangl.pan.server.modules.share.enums.ShareDayTypeEnum;
import com.tangl.pan.server.modules.share.enums.ShareStatusEnum;
import com.tangl.pan.server.modules.share.service.IShareFileService;
import com.tangl.pan.server.modules.share.service.IShareService;
import com.tangl.pan.server.modules.share.mapper.TPanShareMapper;
import com.tangl.pan.server.modules.share.vo.ShareUrlVO;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
