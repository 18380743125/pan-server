package com.tangl.pan.server.modules.file;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.google.common.collect.Lists;
import com.tangl.pan.core.exception.TPanBusinessException;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.server.TPanServerLauncher;
import com.tangl.pan.server.modules.file.context.*;
import com.tangl.pan.server.modules.file.entity.TPanFile;
import com.tangl.pan.server.modules.file.entity.TPanFileChunk;
import com.tangl.pan.server.modules.file.enums.DelFlagEnum;
import com.tangl.pan.server.modules.file.enums.MergeFlagEnum;
import com.tangl.pan.server.modules.file.service.IFileChunkService;
import com.tangl.pan.server.modules.file.service.IFileService;
import com.tangl.pan.server.modules.file.service.IUserFileService;
import com.tangl.pan.server.modules.file.vo.*;
import com.tangl.pan.server.modules.user.context.UserRegisterContext;
import com.tangl.pan.server.modules.user.service.IUserService;
import com.tangl.pan.server.modules.user.vo.UserInfoVO;
import lombok.AllArgsConstructor;
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
import java.util.concurrent.CountDownLatch;

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

    @Autowired
    private IFileChunkService fileChunkService;

    /**
     * 测试查询文件面包屑导航列表
     */
    @Test
    public void testGetBreadcrumbs() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("com");
        userFileService.createFolder(context);

        context.setFolderName("haha");
        Long fileId = userFileService.createFolder(context);

        QueryBreadcrumbsContext queryBreadcrumbsContext = new QueryBreadcrumbsContext();
        queryBreadcrumbsContext.setUserId(userId);
        queryBreadcrumbsContext.setFileId(fileId);
        List<BreadcrumbsVO> breadcrumbs = userFileService.getBreadcrumbs(queryBreadcrumbsContext);
        System.out.println(breadcrumbs);
        Assert.notNull(breadcrumbs);
        Assert.isTrue(breadcrumbs.size() == 2);
    }

    /**
     * 测试搜索文件
     */
    @Test
    public void testSearch() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("com");
        userFileService.createFolder(context);

        context.setFolderName("haha");
        userFileService.createFolder(context);

        FileSearchContext fileSearchContext = new FileSearchContext();
        fileSearchContext.setUserId(userId);
        fileSearchContext.setKeyword("com");
        fileSearchContext.setFileTypesArray(Lists.newArrayList());
        userFileService.search(fileSearchContext);
        List<FileSearchResultVO> fileSearchResultVOList = userFileService.search(fileSearchContext);
        System.out.println(fileSearchResultVOList);
    }

    /**
     * 测试复制文件成功
     */
    @Test
    public void testCopyFileSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("com");
        Long fileId1 = userFileService.createFolder(context);

        context.setFolderName("haha");
        context.setParentId(fileId1);
        Long fileId2 = userFileService.createFolder(context);

        context.setFolderName("tangl");
        context.setParentId(fileId1);
        Long fileId3 = userFileService.createFolder(context);

        context.setFolderName("pan");
        context.setParentId(fileId3);
        Long fileId4 = userFileService.createFolder(context);

        CopyFileContext copyFileContext = new CopyFileContext();
        copyFileContext.setUserId(userId);
        copyFileContext.setTargetParentId(fileId1);
        copyFileContext.setFileIdList(Lists.newArrayList(fileId4));
        userFileService.copyFile(copyFileContext);

        QueryFolderTreeContext queryFolderTreeContext = new QueryFolderTreeContext();
        queryFolderTreeContext.setUserId(userId);
        List<FolderTreeNodeVO> folderTree = userFileService.getFolderTree(queryFolderTreeContext);
        Assert.isTrue(folderTree.size() == 1);
        folderTree.forEach(FolderTreeNodeVO::print);
    }

    /**
     * 测试复制文件失败
     */
    @Test(expected = TPanBusinessException.class)
    public void testCopyFileFail() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("com");
        Long fileId1 = userFileService.createFolder(context);

        context.setFolderName("haha");
        context.setParentId(fileId1);
        Long fileId2 = userFileService.createFolder(context);

        context.setFolderName("tangl");
        context.setParentId(fileId1);
        Long fileId3 = userFileService.createFolder(context);

        context.setFolderName("pan");
        context.setParentId(fileId3);
        Long fileId4 = userFileService.createFolder(context);

        CopyFileContext copyFileContext = new CopyFileContext();
        copyFileContext.setUserId(userId);
        copyFileContext.setTargetParentId(fileId2);
        copyFileContext.setFileIdList(Lists.newArrayList(fileId2, fileId4));
        userFileService.copyFile(copyFileContext);

        QueryFolderTreeContext queryFolderTreeContext = new QueryFolderTreeContext();
        queryFolderTreeContext.setUserId(userId);
        List<FolderTreeNodeVO> folderTree = userFileService.getFolderTree(queryFolderTreeContext);
        Assert.isTrue(folderTree.size() == 1);
        folderTree.forEach(FolderTreeNodeVO::print);
    }

    /**
     * 测试转移文件成功
     */
    @Test
    public void testTransferFileSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("com");
        Long fileId1 = userFileService.createFolder(context);

        context.setFolderName("haha");
        context.setParentId(fileId1);
        Long fileId2 = userFileService.createFolder(context);

        context.setFolderName("tangl");
        context.setParentId(fileId1);
        Long fileId3 = userFileService.createFolder(context);

        context.setFolderName("pan");
        context.setParentId(fileId3);
        Long fileId4 = userFileService.createFolder(context);

        TransferFileContext transferFileContext = new TransferFileContext();
        transferFileContext.setUserId(userId);
        transferFileContext.setTargetParentId(fileId1);
        transferFileContext.setFileIdList(Lists.newArrayList(fileId4));
        userFileService.transferFile(transferFileContext);

        QueryFolderTreeContext queryFolderTreeContext = new QueryFolderTreeContext();
        queryFolderTreeContext.setUserId(userId);
        List<FolderTreeNodeVO> folderTree = userFileService.getFolderTree(queryFolderTreeContext);
        Assert.isTrue(folderTree.size() == 1);
        folderTree.forEach(FolderTreeNodeVO::print);
    }

    /**
     * 测试转移文件失败
     * 目标文件夹是要转移文件夹以及子文件夹
     */
    @Test(expected = TPanBusinessException.class)
    public void testTransferFileFail() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("com");
        Long fileId1 = userFileService.createFolder(context);

        context.setFolderName("haha");
        context.setParentId(fileId1);
        Long fileId2 = userFileService.createFolder(context);

        context.setFolderName("tangl");
        context.setParentId(fileId1);
        Long fileId3 = userFileService.createFolder(context);

        context.setFolderName("pan");
        context.setParentId(fileId3);
        Long fileId4 = userFileService.createFolder(context);

        TransferFileContext transferFileContext = new TransferFileContext();
        transferFileContext.setUserId(userId);
        transferFileContext.setTargetParentId(fileId2);
        transferFileContext.setFileIdList(Lists.newArrayList(fileId4, fileId2));
        userFileService.transferFile(transferFileContext);

        QueryFolderTreeContext queryFolderTreeContext = new QueryFolderTreeContext();
        queryFolderTreeContext.setUserId(userId);
        List<FolderTreeNodeVO> folderTree = userFileService.getFolderTree(queryFolderTreeContext);
        Assert.isTrue(folderTree.size() == 1);
        folderTree.forEach(FolderTreeNodeVO::print);
    }

    /**
     * 测试查询文件夹树
     */
    @Test
    public void testQueryFolderTreeSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("com");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        context.setFolderName("haha");
        context.setParentId(fileId);
        userFileService.createFolder(context);
        Assert.notNull(fileId);

        context.setFolderName("tangl");
        context.setParentId(fileId);
        fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        context.setFolderName("pan");
        context.setParentId(fileId);
        fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        QueryFolderTreeContext queryFolderTreeContext = new QueryFolderTreeContext();
        queryFolderTreeContext.setUserId(userId);
        List<FolderTreeNodeVO> folderTree = userFileService.getFolderTree(queryFolderTreeContext);
        Assert.isTrue(folderTree.size() == 1);
        folderTree.forEach(FolderTreeNodeVO::print);
    }

    /**
     * 文件分片上传器
     */
    @AllArgsConstructor
    private static class ChunkUploader extends Thread {
        private CountDownLatch countDownLatch;

        private Integer chunk;

        private Integer chunks;

        private IUserFileService userFileService;

        private Long userId;

        private Long parentId;

        /**
         * 1、上传文件分片
         * 2、根据上传的结果调用文件分片合并
         */
        @Override
        public void run() {
            super.run();
            MultipartFile file = generateMultipartFile();
            Long totalSize = file.getSize() * chunks;
            String filename = "text.txt";
            String identifier = "123456789";
            FileChunkUploadContext context = new FileChunkUploadContext();
            context.setFilename(filename);
            context.setFile(file);
            context.setChunkNumber(chunk);
            context.setTotalChunks(chunks);
            context.setUserId(userId);
            context.setCurrentChunkSize(file.getSize());
            context.setTotalSize(totalSize);
            context.setIdentifier(identifier);

            FileChunkUploadVO fileChunkUploadVO = userFileService.chunkUpload(context);

            if (fileChunkUploadVO.getMergeFlag().equals(MergeFlagEnum.READY.getCode())) {
                System.out.println("分片" + chunk + "监测到文件可以合并");

                FileChunkMergeContext fileChunkMergeContext = new FileChunkMergeContext();
                fileChunkMergeContext.setFilename(filename);
                fileChunkMergeContext.setIdentifier(identifier);
                fileChunkMergeContext.setTotalSize(totalSize);
                fileChunkMergeContext.setParentId(parentId);
                fileChunkMergeContext.setUserId(userId);

                userFileService.mergeFile(fileChunkMergeContext);
                countDownLatch.countDown();
            } else {
                countDownLatch.countDown();
            }
        }
    }

    /**
     * 测试文件分片上传成功
     */
    @Test
    public void uploadWithChunkTestSuccess() throws InterruptedException {
        Long userId = register();
        UserInfoVO userInfoVO = userService.info(userId);

        CountDownLatch countDownLatch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            new ChunkUploader(countDownLatch, i + 1, 10, userFileService, userId, userInfoVO.getRootFileId()).start();
        }

        countDownLatch.await();
    }

    /**
     * 测试查询用户已上传的文件分片信息列表成功
     */
    @Test
    public void testQueryUploadedChunksSuccess() {
        Long userId = register();

        String identifier = "123456789";

        TPanFileChunk record = new TPanFileChunk();
        record.setId(IdUtil.get());
        record.setIdentifier(identifier);
        record.setRealPath("realPath");
        record.setChunkNumber(1);
        record.setExpirationTime(DateUtil.offsetDay(new Date(), 1));
        record.setCreateUser(userId);
        record.setCreateTime(new Date());
        boolean save = fileChunkService.save(record);
        Assert.isTrue(save);

        QueryUploadedChunksContext context = new QueryUploadedChunksContext();
        context.setIdentifier(identifier);
        context.setUserId(userId);

        UploadedChunksVO vo = userFileService.getUploadedChunks(context);

        Assert.notNull(vo);
        Assert.notEmpty(vo.getUploadedChunks());
    }

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

        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(userInfo.getRootFileId());
        createFolderContext.setFolderName("测试文件夹");
        createFolderContext.setUserId(userId);
        userFileService.createFolder(createFolderContext);

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
    private static MultipartFile generateMultipartFile() {
        MultipartFile file = null;
        try {
            file = new MockMultipartFile("file", "test.txt", "multipart/form-data", "test upload context".getBytes(StandardCharsets.UTF_8));
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
