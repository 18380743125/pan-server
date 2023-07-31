package com.tangl.pan.server.modules.user;

import cn.hutool.core.lang.Assert;
import com.tangl.pan.core.exception.TPanBusinessException;
import com.tangl.pan.server.TPanServerLauncher;
import com.tangl.pan.server.modules.user.context.UserRegisterContext;
import com.tangl.pan.server.modules.user.service.IUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author tangl
 * @description 用户模块单元测试类
 * @create 2023-07-30 22:09
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TPanServerLauncher.class)
@Transactional
public class UserTest {
    @Autowired
    private IUserService userService;

    /**
     * 测试成功注册用户信息
     */
    @Test
    public void testRegisterUser() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);
    }

    /**
     * 测试重复用户名称注册幂等
     */
    @Test(expected = TPanBusinessException.class)
    public void testRegisterDuplicateUsername() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);
        userService.register(context);
    }

    private UserRegisterContext createUserRegisterContext() {
        UserRegisterContext context = new UserRegisterContext();
        context.setUsername("test");
        context.setPassword("123456");
        context.setQuestion("你的手机型号");
        context.setAnswer("小米11");
        return context;
    }
}
