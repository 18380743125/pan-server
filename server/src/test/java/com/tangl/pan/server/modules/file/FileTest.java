package com.tangl.pan.server.modules.file;

import cn.hutool.core.lang.Assert;
import com.tangl.pan.core.exception.TPanBusinessException;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.server.TPanServerLauncher;
import com.tangl.pan.server.modules.file.context.*;
import com.tangl.pan.server.modules.file.entity.TPanFile;
import com.tangl.pan.server.modules.file.enums.DelFlagEnum;
import com.tangl.pan.server.modules.file.service.IFileService;
import com.tangl.pan.server.modules.file.service.IUserFileService;
import com.tangl.pan.server.modules.file.vo.UserFileVO;
import com.tangl.pan.server.modules.user.context.UserRegisterContext;
import com.tangl.pan.server.modules.user.service.IUserService;
import com.tangl.pan.server.modules.user.vo.UserInfoVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author tangl
 * @description 文件模块单元测试类
 * @create 2023-08-10 21:56
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TPanServerLauncher.class)
@Transactional
public class FileTest {

    @Autowired
    private IUserFileService userFileService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IFileService fileService;

    /**
     * 测试单文件上传成功
     */
    @Test
    public void testUploadSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        FileUploadContext context = new FileUploadContext();
        MultipartFile file = generateMultipartFile();
        context.setFile(file);
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setIdentifier("123456789");
        context.setTotalSize(file.getSize());
        context.setFilename(file.getOriginalFilename());
        userFileService.upload(context);

        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setDelFlag(DelFlagEnum.NO.getCode());
        queryFileListContext.setUserId(userId);
        queryFileListContext.setParentId(userInfoVO.getRootFileId());
        List<UserFileVO> fileList = userFileService.getFileList(queryFileListContext);
        Assert.notEmpty(fileList);
        Assert.isTrue(fileList.size() == 1);
    }

    /**
     * 测试秒传成功
     */
    @Test
    public void testSecUploadSuccess() {
        Long userId = register();
        UserInfoVO userInfo = info(userId);

        // 已有的文件
        String identifier = "123456789";
        TPanFile tPanFile = new TPanFile();
        tPanFile.setFileId(IdUtil.get());
        tPanFile.setFilename("filename");
        tPanFile.setRealPath("realpath");
        tPanFile.setFileSize("fileSize");
        tPanFile.setFileSizeDesc("fileSizeDesc");
        tPanFile.setFilePreviewContentType("");
        tPanFile.setIdentifier(identifier);
        tPanFile.setCreateUser(userId);
        tPanFile.setCreateTime(new Date());
        fileService.save(tPanFile);

        SecUploadContext context = new SecUploadContext();
        context.setIdentifier(identifier);
        context.setFilename("文件名");
        context.setParentId(userInfo.getRootFileId());
        context.setUserId(userId);

        boolean result = userFileService.secUpload(context);
        Assert.isTrue(result);
    }

    /**
     * 测试秒传失败
     */
    @Test
    public void testSecUploadFail() {
        Long userId = register();
        UserInfoVO userInfo = info(userId);

        // 已有的文件
        String identifier = "123456789";
        TPanFile tPanFile = new TPanFile();
        tPanFile.setFileId(IdUtil.get());
        tPanFile.setFilename("filename");
        tPanFile.setRealPath("realpath");
        tPanFile.setFileSize("fileSize");
        tPanFile.setFileSizeDesc("fileSizeDesc");
        tPanFile.setFilePreviewContentType("");
        tPanFile.setIdentifier(identifier);
        tPanFile.setCreateUser(userId);
        tPanFile.setCreateTime(new Date());
        fileService.save(tPanFile);

        SecUploadContext context = new SecUploadContext();
        context.setIdentifier(identifier + 1);
        context.setFilename("文件名");
        context.setParentId(userInfo.getRootFileId());
        context.setUserId(userId);

        boolean result = userFileService.secUpload(context);
        Assert.isFalse(result);
    }

    /**
     * 校验文件删除失败-非法的文件ID
     */
    @Test(expected = TPanBusinessException.class)
    public void testDeleteFileFailByWrongFileId() {
        Long userId = register();
        UserInfoVO userInfo = info(userId);

        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(userInfo.getRootFileId());
        createFolderContext.setFolderName("测试文件夹");
        createFolderContext.setUserId(userId);
        Long fileId = userFileService.createFolder(createFolderContext);

        DeleteFileContext deleteFileContext = new DeleteFileContext();
        deleteFileContext.setUserId(userId);
        List<Long> fileIdList = new ArrayList<>();
        fileIdList.add(fileId + 1);
        deleteFileContext.setFileIdList(fileIdList);
        userFileService.deleteFile(deleteFileContext);
    }

    /**
     * 校验文件删除失败-非法的用户ID
     */
    @Test(expected = TPanBusinessException.class)
    public void testDeleteFileFailByWrongUserId() {
        Long userId = register();
        UserInfoVO userInfo = info(userId);

        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(userInfo.getRootFileId());
        createFolderContext.setFolderName("测试文件夹");
        createFolderContext.setUserId(userId);
        Long fileId = userFileService.createFolder(createFolderContext);

        DeleteFileContext deleteFileContext = new DeleteFileContext();
        deleteFileContext.setUserId(userId + 1);
        List<Long> fileIdList = new ArrayList<>();
        fileIdList.add(fileId);
        deleteFileContext.setFileIdList(fileIdList);
        userFileService.deleteFile(deleteFileContext);
    }

    /**
     * 删除用户文件-成功
     */
    @Test
    public void testDeleteFileSuccess() {
        Long userId = register();
        UserInfoVO userInfo = info(userId);

        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(userInfo.getRootFileId());
        createFolderContext.setFolderName("测试文件夹");
        createFolderContext.setUserId(userId);
        Long fileId = userFileService.createFolder(createFolderContext);

        DeleteFileContext deleteFileContext = new DeleteFileContext();
        deleteFileContext.setUserId(userId);
        List<Long> fileIdList = new ArrayList<>();
        fileIdList.add(fileId);
        deleteFileContext.setFileIdList(fileIdList);
        userFileService.deleteFile(deleteFileContext);
    }

    /**
     * 无效的 fileId
     */
    @Test(expected = TPanBusinessException.class)
    public void testUpdateFilenameFailByWrongFileId() {
        Long userId = register();
        UserInfoVO userInfo = info(userId);

        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(userInfo.getRootFileId());
        createFolderContext.setFolderName("测试文件夹");
        createFolderContext.setUserId(userId);
        Long fileId = userFileService.createFolder(createFolderContext);

        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();
        updateFilenameContext.setFileId(fileId + 1);
        updateFilenameContext.setUserId(userId);
        updateFilenameContext.setNewFilename("新的测试文件夹");

        userFileService.updateFilename(updateFilenameContext);
    }

    /**
     * 无权修改的 userId
     */
    @Test(expected = TPanBusinessException.class)
    public void testUpdateFilenameFailByWrongUserId() {
        Long userId = register();
        UserInfoVO userInfo = info(userId);

        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(userInfo.getRootFileId());
        createFolderContext.setFolderName("测试文件夹");
        createFolderContext.setUserId(userId);
        Long fileId = userFileService.createFolder(createFolderContext);

        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();
        updateFilenameContext.setFileId(fileId);
        updateFilenameContext.setUserId(userId + 1);
        updateFilenameContext.setNewFilename("新的测试文件夹");

        userFileService.updateFilename(updateFilenameContext);
    }

    /**
     * 新旧文件名称一致
     */
    @Test(expected = TPanBusinessException.class)
    public void testUpdateFilenameFailByWrongFilename() {
        Long userId = register();
        UserInfoVO userInfo = info(userId);

        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(userInfo.getRootFileId());
        createFolderContext.setFolderName("测试文件夹");
        createFolderContext.setUserId(userId);
        Long fileId = userFileService.createFolder(createFolderContext);

        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();
        updateFilenameContext.setFileId(fileId);
        updateFilenameContext.setUserId(userId);
        updateFilenameContext.setNewFilename("测试文件夹");

        userFileService.updateFilename(updateFilenameContext);
    }

    /**
     * 该文件夹下已有该文件名称
     */
    @Test(expected = TPanBusinessException.class)
    public void testUpdateFilenameFailByRepeatFileName() {
        Long userId = register();
        UserInfoVO userInfo = info(userId);

        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(userInfo.getRootFileId());
        createFolderContext.setFolderName("测试文件夹");
        createFolderContext.setUserId(userId);
        Long fileId = userFileService.createFolder(createFolderContext);

        // 第二个文件名称
        createFolderContext.setFolderName("测试文件夹1");
        userFileService.createFolder(createFolderContext);

        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();
        updateFilenameContext.setFileId(fileId);
        updateFilenameContext.setUserId(userId);
        updateFilenameContext.setNewFilename("测试文件夹1");
        userFileService.updateFilename(updateFilenameContext);
    }

    /**
     * 重命名文件成功
     */
    @Test
    public void testUpdateFilenameSuccess() {
        // 注册及获取用户信息
        Long userId = register();
        UserInfoVO userInfo = info(userId);

        // 创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(userInfo.getRootFileId());
        createFolderContext.setFolderName("测试文件夹");
        createFolderContext.setUserId(userId);
        Long fileId = userFileService.createFolder(createFolderContext);

        // 更新文件名称
        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();
        updateFilenameContext.setFileId(fileId);
        updateFilenameContext.setUserId(userId);
        updateFilenameContext.setNewFilename("测试文件夹1");
        userFileService.updateFilename(updateFilenameContext);
    }

    /**
     * 测试用户查询文件列表成功
     */
    @Test
    public void testQueryUserFileSuccess() {
        Long userId = register();
        UserInfoVO userInfo = info(userId);

        QueryFileListContext context = new QueryFileListContext();
        context.setParentId(userInfo.getRootFileId());
        context.setUserId(userId);
        context.setDelFlag(DelFlagEnum.NO.getCode());
        List<UserFileVO> fileList = userFileService.getFileList(context);
        System.out.println("fileList = " + fileList);
    }

    /**
     * 测试创建文件夹
     */
    @Test
    public void testCreateFolderSuccess() {
        Long userId = register();
        UserInfoVO userInfo = info(userId);

        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(userInfo.getRootFileId());
        createFolderContext.setFolderName("测试文件夹");
        createFolderContext.setUserId(userId);
        userFileService.createFolder(createFolderContext);
    }

    /**
     * 生成模拟的网络文件实体
     *
     * @return MultipartFile
     */
    private MultipartFile generateMultipartFile() {
        MultipartFile file = null;
        try {
            file = new MockMultipartFile("file",
                    "test.txt",
                    "multipart/form-data",
                    "test upload context".getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
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
