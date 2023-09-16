package com.tangl.pan.server.modules.share;

import cn.hutool.core.lang.Assert;
import com.google.common.collect.Lists;
import com.tangl.pan.server.TPanServerLauncher;
import com.tangl.pan.server.modules.file.context.CreateFolderContext;
import com.tangl.pan.server.modules.file.service.IUserFileService;
import com.tangl.pan.server.modules.share.context.CreateShareUrlContext;
import com.tangl.pan.server.modules.share.enums.ShareDayTypeEnum;
import com.tangl.pan.server.modules.share.enums.ShareTypeEnum;
import com.tangl.pan.server.modules.share.service.IShareService;
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

import java.util.Objects;

/**
 * @author tangl
 * @description
 * @create 2023-09-16 18:19
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TPanServerLauncher.class)
@Transactional
public class ShareTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserFileService userFileService;

    @Autowired
    private IShareService shareService;

    /**
     * 创建分享链接成功
     */
    @Test
    public void createShareUrlSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name");

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
     * 查询登录用户的基本信息
     *
     * @return 用户基本信息
     */
    private UserInfoVO info(Long userId) {
        return userService.info(userId);
    }

    /**
     * 注册用户
     *
     * @return userId
     */
    private Long register() {
        UserRegisterContext context = createUserRegisterContext();
        return userService.register(context);
    }

    /**
     * 创建注册用户的上下文实体
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
