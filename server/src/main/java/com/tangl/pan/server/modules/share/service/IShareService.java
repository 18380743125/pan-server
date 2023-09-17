package com.tangl.pan.server.modules.share.service;

import com.tangl.pan.server.modules.file.vo.UserFileVO;
import com.tangl.pan.server.modules.share.context.*;
import com.tangl.pan.server.modules.share.entity.TPanShare;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tangl.pan.server.modules.share.vo.ShareDetailVO;
import com.tangl.pan.server.modules.share.vo.ShareSimpleDetailVO;
import com.tangl.pan.server.modules.share.vo.ShareUrlListVO;
import com.tangl.pan.server.modules.share.vo.ShareUrlVO;

import java.util.List;

/**
 * @author 25050
 * @description 针对表【t_pan_share(用户分享表)】的数据库操作Service
 * @createDate 2023-07-23 23:42:53
 */
public interface IShareService extends IService<TPanShare> {

    /**
     * 创建分享链接
     *
     * @param context 上下文实体
     * @return ShareUrlVO
     */
    ShareUrlVO create(CreateShareUrlContext context);

    /**
     * 查询用户已有的分享链接列表
     *
     * @param context 上下文实体
     * @return List<ShareUrlListVO>
     */
    List<ShareUrlListVO> getShares(QueryShareListContext context);

    /**
     * 取消分享
     *
     * @param context 上下文实体
     */
    void cancelShare(CancelShareContext context);

    /**
     * 校验分享码
     *
     * @param context 上下文实体
     * @return token
     */
    String checkShareCode(CheckShareCodeContext context);

    /**
     * 查询分享的详情
     *
     * @param context 上下文实体
     * @return ShareDetailVO
     */
    ShareDetailVO detail(QueryShareDetailContext context);

    /**
     * 查询分享的简单详情
     *
     * @param context 上下文实体
     * @return ShareSimpleDetailVO
     */
    ShareSimpleDetailVO simpleDetail(QueryShareSimpleDetailContext context);

    /**
     * 获取下一级文件列表
     *
     * @param context 上下文实体
     * @return List<UserFileVO>
     */
    List<UserFileVO> fileList(QueryChildFileListContext context);

    /**
     * 转存到我的网盘
     *
     * @param context 上下文实体
     */
    void saveFiles(ShareSaveContext context);

    /**
     * 分享文件下载
     *
     * @param context 上下文实体
     */
    void download(ShareFileDownloadContext context);

    /**
     * 刷新受影响的对应分享的状态
     *
     * @param allAvailableFileIdList fileIdList
     */
    void refreshShareStatus(List<Long> allAvailableFileIdList);
}
