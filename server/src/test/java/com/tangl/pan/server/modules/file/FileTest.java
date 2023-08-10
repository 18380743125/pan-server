package com.tangl.pan.server.modules.file;

import com.tangl.pan.server.TPanServerLauncher;
import com.tangl.pan.server.modules.file.context.CreateFolderContext;
import com.tangl.pan.server.modules.file.context.QueryFileListContext;
import com.tangl.pan.server.modules.file.service.IUserFileService;
import com.tangl.pan.server.modules.file.vo.UserFileVO;
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

        // 查询文件列表
        QueryFileListContext context = new QueryFileListContext();
        context.setParentId(userInfo.getRootFileId());
        context.setUserId(userId);
        List<UserFileVO> fileList = userFileService.getFileList(context);
        System.out.println("fileList = " + fileList);
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
