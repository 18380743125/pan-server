package com.tangl.pan.server.modules.recycle;

import cn.hutool.core.lang.Assert;
import com.google.common.collect.Lists;
import com.tangl.pan.core.exception.TPanBusinessException;
import com.tangl.pan.server.TPanServerLauncher;
import com.tangl.pan.server.modules.file.context.CreateFolderContext;
import com.tangl.pan.server.modules.file.context.DeleteFileContext;
import com.tangl.pan.server.modules.file.service.IUserFileService;
import com.tangl.pan.server.modules.file.vo.UserFileVO;
import com.tangl.pan.server.modules.recycle.context.DeleteContext;
import com.tangl.pan.server.modules.recycle.context.QueryRecycleFileListContext;
import com.tangl.pan.server.modules.recycle.context.RestoreContext;
import com.tangl.pan.server.modules.recycle.service.IRecycleService;
import com.tangl.pan.server.modules.user.context.UserRegisterContext;
import com.tangl.pan.server.modules.user.service.IUserService;
import com.tangl.pan.server.modules.user.vo.UserInfoVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tangl
 * @description 回收站模块单元测试类
 * @create 2023-09-15 22:27
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TPanServerLauncher.class)
@Transactional
public class RecycleTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private IRecycleService recycleService;

    @Autowired
    private IUserFileService userFileService;

    /**
     * 文件彻底删除 - 成功
     */
    @Test
    public void testDeleteFileSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        // 创建一个文件夹
        Long folderId = createFolder(userId, userInfoVO);

        // 删除该文件夹
        deleteFile(userId, folderId);

        DeleteContext context = new DeleteContext();
        context.setUserId(userId);
        context.setFileIdList(Lists.newArrayList(folderId));
        recycleService.delete(context);
    }

    /**
     * 文件彻底删除 - 失败
     */
    @Test(expected = TPanBusinessException.class)
    public void testDeleteFileWrongWithUserId() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        // 创建一个文件夹
        Long folderId = createFolder(userId, userInfoVO);

        // 删除该文件夹
        deleteFile(userId, folderId);

        DeleteContext context = new DeleteContext();
        context.setUserId(userId + 1);
        context.setFileIdList(Lists.newArrayList(folderId));
        recycleService.delete(context);
    }

    /**
     * 测试文件还原失败 - 无权限访问
     */
    @Test(expected = TPanBusinessException.class)
    public void testRestoreFileWrongWithNoPermission() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        // 创建一个文件夹
        Long folderId = createFolder(userId, userInfoVO);

        // 删除该文件夹
        deleteFile(userId, folderId);

        // 文件还原
        RestoreContext restoreContext = new RestoreContext();
        restoreContext.setUserId(userId + 1);
        restoreContext.setFileIdList(Lists.newArrayList(folderId));

        recycleService.restore(restoreContext);
    }

    /**
     * 测试文件还原失败 - 同名文件 - 还原列表
     */
    @Test(expected = TPanBusinessException.class)
    public void testRestoreFileWrongWithSameFilename1() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        // 创建一个文件夹
        Long folderId1 = createFolder(userId, userInfoVO);
        // 删除该文件夹
        deleteFile(userId, folderId1);

        // 创建一个文件夹
        Long folderId2 = createFolder(userId, userInfoVO);
        deleteFile(userId, folderId2);

        // 文件还原
        RestoreContext restoreContext = new RestoreContext();
        restoreContext.setUserId(userId);
        restoreContext.setFileIdList(Lists.newArrayList(folderId1, folderId2));

        recycleService.restore(restoreContext);
    }

    /**
     * 测试文件还原失败 - 同名文件 - 父文件夹中已存在该名
     */
    @Test(expected = TPanBusinessException.class)
    public void testRestoreFileWrongWithSameFilename2() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        // 创建一个文件夹
        Long folderId1 = createFolder(userId, userInfoVO);
        // 删除该文件夹
        deleteFile(userId, folderId1);

        // 创建一个文件夹
        createFolder(userId, userInfoVO);

        // 文件还原
        RestoreContext restoreContext = new RestoreContext();
        restoreContext.setUserId(userId);
        restoreContext.setFileIdList(Lists.newArrayList(folderId1));

        recycleService.restore(restoreContext);
    }

    /**
     * 测试文件还原成功
     */
    @Test
    public void testRestoreFileSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        // 创建一个文件夹
        Long folderId = createFolder(userId, userInfoVO);

        // 删除该文件夹
        deleteFile(userId, folderId);

        // 文件还原
        RestoreContext restoreContext = new RestoreContext();
        restoreContext.setUserId(userId);
        restoreContext.setFileIdList(Lists.newArrayList(folderId));

        recycleService.restore(restoreContext);
    }

    /**
     * 测试查询回收站列表
     */
    @Test
    public void testQueryRecycles() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        // 创建一个文件夹
        Long folderId = createFolder(userId, userInfoVO);

        // 删除该文件夹
        deleteFile(userId, folderId);

        // 查询回收站文件列表
        QueryRecycleFileListContext context = new QueryRecycleFileListContext();
        context.setUserId(userId);
        List<UserFileVO> recycles = recycleService.recycles(context);
        Assert.isTrue(recycles.size() == 1);
    }

    /**
     * 删除文件夹
     *
     * @param userId 用户 ID
     * @param fileId 文件夹 ID
     */
    private void deleteFile(Long userId, Long fileId) {
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        deleteFileContext.setUserId(userId);
        List<Long> fileIdList = new ArrayList<>();
        fileIdList.add(fileId);
        deleteFileContext.setFileIdList(fileIdList);
        userFileService.deleteFile(deleteFileContext);
    }

    /**
     * 创建文件夹
     */
    private Long createFolder(Long userId, UserInfoVO userInfoVO) {

        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(userInfoVO.getRootFileId());
        createFolderContext.setFolderName("测试文件夹");
        createFolderContext.setUserId(userId);
        return userFileService.createFolder(createFolderContext);
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
