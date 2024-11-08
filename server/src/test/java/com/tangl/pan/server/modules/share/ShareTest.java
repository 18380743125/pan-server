package com.tangl.pan.server.modules.share;

import cn.hutool.core.lang.Assert;
import com.google.common.collect.Lists;
import com.tangl.pan.core.exception.PanBusinessException;
import com.tangl.pan.server.PanServerLauncher;
import com.tangl.pan.server.modules.file.context.CreateFolderContext;
import com.tangl.pan.server.modules.file.service.IUserFileService;
import com.tangl.pan.server.modules.file.vo.UserFileVO;
import com.tangl.pan.server.modules.share.context.*;
import com.tangl.pan.server.modules.share.enums.ShareDayTypeEnum;
import com.tangl.pan.server.modules.share.enums.ShareTypeEnum;
import com.tangl.pan.server.modules.share.service.IShareService;
import com.tangl.pan.server.modules.share.vo.ShareDetailVO;
import com.tangl.pan.server.modules.share.vo.ShareSimpleDetailVO;
import com.tangl.pan.server.modules.share.vo.ShareUrlListVO;
import com.tangl.pan.server.modules.share.vo.ShareUrlVO;
import com.tangl.pan.server.modules.user.context.UserRegisterContext;
import com.tangl.pan.server.modules.user.service.IUserService;
import com.tangl.pan.server.modules.user.vo.UserInfoVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 分享模块的单元测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PanServerLauncher.class)
@Transactional
public class ShareTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserFileService userFileService;

    @Autowired
    private IShareService shareService;

    // 初始化数据
    @Test
    public void init() {
        CreateShareUrlContext context = new CreateShareUrlContext();
        context.setUserId(1705488558484058112L);
        context.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        context.setShareDayType(ShareDayTypeEnum.PERMANENT_VALIDITY.getCode());
        context.setShareFileIdList(Lists.newArrayList(1705493127851110400L));
//        for (int i = 0; i < 10000000; i++) {
//            context.setShareName("测试分享" + i);
//            shareService.create(context);
//        }
    }

    /**
     * 测试查询分享下一级的文件列表
     */

    @Test
    public void testQueryShareFileListSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("测试文件夹");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();
        createShareUrlContext.setShareName("分享名称");
        createShareUrlContext.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        createShareUrlContext.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        createShareUrlContext.setUserId(userId);
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(userInfoVO.getRootFileId()));
        ShareUrlVO vo = shareService.create(createShareUrlContext);

        QueryChildFileListContext queryChildFileListContext = new QueryChildFileListContext();
        queryChildFileListContext.setShareId(vo.getShareId());
        queryChildFileListContext.setParentId(userInfoVO.getRootFileId());

        List<UserFileVO> userFileVOList = shareService.fileList(queryChildFileListContext);
        Assert.notEmpty(userFileVOList);
        System.out.println(userFileVOList);
    }

    /**
     * 测试查询简单分享成功
     */
    @Test
    public void testQueryShareSimpleDetailSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("测试文件夹");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();
        createShareUrlContext.setShareName("分享名称");
        createShareUrlContext.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        createShareUrlContext.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        createShareUrlContext.setUserId(userId);
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(fileId));
        ShareUrlVO vo = shareService.create(createShareUrlContext);

        QueryShareSimpleDetailContext queryShareDetailContext = new QueryShareSimpleDetailContext();
        queryShareDetailContext.setShareId(vo.getShareId());
        ShareSimpleDetailVO shareSimpleDetailVO = shareService.simpleDetail(queryShareDetailContext);
        Assert.notNull(shareSimpleDetailVO);
        System.out.println(shareSimpleDetailVO);
    }

    /**
     * 测试查询分享详情成功
     */
    @Test
    public void testQueryShareDetailSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("测试文件夹");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();
        createShareUrlContext.setShareName("分享名称");
        createShareUrlContext.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        createShareUrlContext.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        createShareUrlContext.setUserId(userId);
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(fileId));
        ShareUrlVO vo = shareService.create(createShareUrlContext);

        QueryShareDetailContext queryShareDetailContext = new QueryShareDetailContext();
        queryShareDetailContext.setShareId(vo.getShareId());
        ShareDetailVO detail = shareService.detail(queryShareDetailContext);
        Assert.notNull(detail);
        System.out.println(detail);
    }

    /**
     * 测试校验分享码成功
     */
    @Test
    public void testCheckShareCodeSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("测试文件夹");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();
        createShareUrlContext.setShareName("分享名称");
        createShareUrlContext.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        createShareUrlContext.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        createShareUrlContext.setUserId(userId);
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(fileId));
        ShareUrlVO vo = shareService.create(createShareUrlContext);

        CheckShareCodeContext checkShareCodeContext = new CheckShareCodeContext();
        checkShareCodeContext.setShareId(vo.getShareId());
        checkShareCodeContext.setShareCode(vo.getShareCode());
        String token = shareService.checkShareCode(checkShareCodeContext);
        Assert.isTrue(Objects.nonNull(token));
    }

    /**
     * 测试校验分享码失败 - 校验码错误
     */
    @Test(expected = PanBusinessException.class)
    public void testCheckShareCodeByWrongShareCode() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("测试文件夹");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();
        createShareUrlContext.setShareName("分享名称");
        createShareUrlContext.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        createShareUrlContext.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        createShareUrlContext.setUserId(userId);
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(fileId));
        ShareUrlVO vo = shareService.create(createShareUrlContext);

        CheckShareCodeContext checkShareCodeContext = new CheckShareCodeContext();
        checkShareCodeContext.setShareId(vo.getShareId());
        checkShareCodeContext.setShareCode(vo.getShareCode() + 1);
        shareService.checkShareCode(checkShareCodeContext);
    }

    /**
     * 测试校验分享码失败 - 分享被取消
     */
    @Test(expected = PanBusinessException.class)
    public void testCheckShareCodeFailByCancelled() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("测试文件夹");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();
        createShareUrlContext.setShareName("分享名称");
        createShareUrlContext.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        createShareUrlContext.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        createShareUrlContext.setUserId(userId);
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(fileId));
        ShareUrlVO vo = shareService.create(createShareUrlContext);

        CancelShareContext cancelShareContext = new CancelShareContext();
        cancelShareContext.setUserId(userId);
        cancelShareContext.setShareIdList(Lists.newArrayList(vo.getShareId()));
        shareService.cancelShare(cancelShareContext);

        CheckShareCodeContext checkShareCodeContext = new CheckShareCodeContext();
        checkShareCodeContext.setShareId(vo.getShareId());
        checkShareCodeContext.setShareCode(vo.getShareCode());
        shareService.checkShareCode(checkShareCodeContext);
    }

    /**
     * 测试取消分享 - 成功
     */
    @Test
    public void testCancelShareSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("测试文件夹");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();
        createShareUrlContext.setShareName("分享名称");
        createShareUrlContext.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        createShareUrlContext.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        createShareUrlContext.setUserId(userId);
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(fileId));
        ShareUrlVO vo = shareService.create(createShareUrlContext);

        CancelShareContext cancelShareContext = new CancelShareContext();
        cancelShareContext.setUserId(userId);
        cancelShareContext.setShareIdList(Lists.newArrayList(vo.getShareId()));
        shareService.cancelShare(cancelShareContext);

        QueryShareListContext queryShareListContext = new QueryShareListContext();
        queryShareListContext.setUserId(userId);
        List<ShareUrlListVO> shareUrlListVOS = shareService.getShares(queryShareListContext);
        Assert.isTrue(shareUrlListVOS.isEmpty());
    }

    /**
     * 测试取消分享 - 失败 - 错误的用户 ID
     */
    @Test(expected = PanBusinessException.class)
    public void testCancelShareFailByWrongUserId() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("测试文件夹");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();
        createShareUrlContext.setShareName("分享名称");
        createShareUrlContext.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        createShareUrlContext.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        createShareUrlContext.setUserId(userId);
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(fileId));
        ShareUrlVO vo = shareService.create(createShareUrlContext);

        CancelShareContext cancelShareContext = new CancelShareContext();
        cancelShareContext.setUserId(userId + 1);
        cancelShareContext.setShareIdList(Lists.newArrayList(vo.getShareId()));
        shareService.cancelShare(cancelShareContext);
    }

    /**
     * 测试查询分享列表成功
     */
    @Test
    public void testQueryShareUrlSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("测试文件夹");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();
        createShareUrlContext.setShareName("分享名称");
        createShareUrlContext.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        createShareUrlContext.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        createShareUrlContext.setUserId(userId);
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(fileId));
        shareService.create(createShareUrlContext);

        QueryShareListContext queryShareListContext = new QueryShareListContext();
        queryShareListContext.setUserId(userId);
        List<ShareUrlListVO> shares = shareService.getShares(queryShareListContext);
        Assert.isTrue(shares.size() == 1);
    }

    /**
     * 测试创建分享链接成功
     */
    @Test
    public void testCreateShareUrlSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("测试文件夹");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();
        createShareUrlContext.setShareName("分享名称");
        createShareUrlContext.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        createShareUrlContext.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        createShareUrlContext.setUserId(userId);
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(fileId));
        ShareUrlVO vo = shareService.create(createShareUrlContext);
        Assert.isTrue(Objects.nonNull(vo));
    }

    /**
     * 测试查询登录用户的基本信息
     *
     * @return 用户基本信息
     */
    private UserInfoVO info(Long userId) {
        return userService.info(userId);
    }

    /**
     * 测试注册用户
     *
     * @return userId
     */
    private Long register() {
        UserRegisterContext context = createUserRegisterContext();
        return userService.register(context);
    }

    /**
     * 测试创建注册用户的上下文实体
     *
     * @return UserRegisterContext
     */
    private UserRegisterContext createUserRegisterContext() {
        UserRegisterContext context = new UserRegisterContext();
        context.setUsername("test");
        context.setPassword("123456");
        context.setQuestion("你的手机型号");
        context.setAnswer("小米11");
        return context;
    }
}
