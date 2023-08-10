package com.tangl.pan.server.modules.user;

import cn.hutool.core.lang.Assert;
import com.tangl.pan.core.exception.TPanBusinessException;
import com.tangl.pan.server.TPanServerLauncher;
import com.tangl.pan.server.modules.user.context.*;
import com.tangl.pan.server.modules.user.service.IUserService;
import com.tangl.pan.server.modules.user.vo.UserInfoVO;
import org.apache.commons.lang3.StringUtils;
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

    private final static String USERNAME = "test";
    private final static String PASSWORD = "123456";

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

    /**
     * 测试登录成功
     */
    @Test
    public void testLoginSuccess() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);
        UserLoginContext userLoginContext = createUserLoginContext();
        String accessToken = userService.login(userLoginContext);
        Assert.isTrue(StringUtils.isNotBlank(accessToken));
    }

    /**
     * 测试用户名不正确
     */
    @Test(expected = TPanBusinessException.class)
    public void testLoginWrongUsername() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);
        UserLoginContext userLoginContext = createUserLoginContext();
        userLoginContext.setUsername(userLoginContext.getUsername() + "_change");
        userService.login(userLoginContext);
    }

    /**
     * 测试密码不正确
     */
    @Test(expected = TPanBusinessException.class)
    public void testLoginWrongPassword() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);
        UserLoginContext userLoginContext = createUserLoginContext();
        userLoginContext.setPassword(createUserLoginContext().getPassword() + "_change");
        userService.login(userLoginContext);
    }

    /**
     * 用户退出登录
     */
    @Test
    public void testExitSuccess() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);
        UserLoginContext userLoginContext = createUserLoginContext();
        userService.login(userLoginContext);
        userService.exit(userId);
    }

    /**
     * 忘记密码-校验用户名-成功
     */
    @Test
    public void testCheckUsernameSuccess() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);
        CheckUsernameContext checkUsernameContext = new CheckUsernameContext();
        checkUsernameContext.setUsername(USERNAME);
        String question = userService.checkUsername(checkUsernameContext);
        Assert.isTrue(StringUtils.isNotBlank(question));
    }

    /**
     * 忘记密码-校验用户名-失败
     */
    @Test(expected = TPanBusinessException.class)
    public void testCheckUsernameWrong() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);
        CheckUsernameContext checkUsernameContext = new CheckUsernameContext();
        checkUsernameContext.setUsername(USERNAME + "change");
        userService.checkUsername(checkUsernameContext);
    }

    /**
     * 忘记密码-校验密保答案-成功
     */
    @Test
    public void testCheckAnswerSuccess() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);
        CheckAnswerContext checkAnswerContext = new CheckAnswerContext();
        checkAnswerContext.setUsername(USERNAME);
        checkAnswerContext.setQuestion("你的手机型号");
        checkAnswerContext.setAnswer("小米11");
        String token = userService.checkAnswer(checkAnswerContext);
        Assert.isTrue(StringUtils.isNotBlank(token));
    }

    /**
     * 忘记密码-校验密保答案-失败
     */
    @Test(expected = TPanBusinessException.class)
    public void testCheckAnswerWrong() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);
        CheckAnswerContext checkAnswerContext = new CheckAnswerContext();
        checkAnswerContext.setUsername(USERNAME);
        checkAnswerContext.setQuestion("你的手机型号");
        checkAnswerContext.setAnswer("");
        userService.checkAnswer(checkAnswerContext);
    }

    /**
     * 在线修改密码-成功
     */
    @Test
    public void testChangePasswordSuccess() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);
        ChangePasswordContext changePasswordContext = new ChangePasswordContext();
        changePasswordContext.setUserId(userId);
        changePasswordContext.setOldPassword(PASSWORD);
        changePasswordContext.setNewPassword(PASSWORD + "_change");
        userService.changePassword(changePasswordContext);
    }

    /**
     * 在线修改密码-失败
     */
    @Test(expected = TPanBusinessException.class)
    public void testChangePasswordWrong() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);
        ChangePasswordContext changePasswordContext = new ChangePasswordContext();
        changePasswordContext.setUserId(userId);
        changePasswordContext.setOldPassword(PASSWORD + "_fail");
        changePasswordContext.setNewPassword(PASSWORD + "_change");
        userService.changePassword(changePasswordContext);
    }

    /**
     * 查询登录用户的基本信息
     */
    @Test
    public void testQueryUserInfo() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);
        UserInfoVO info = userService.info(userId);
        Assert.notNull(info);
    }


    private UserRegisterContext createUserRegisterContext() {
        UserRegisterContext context = new UserRegisterContext();
        context.setUsername("test");
        context.setPassword("123456");
        context.setQuestion("你的手机型号");
        context.setAnswer("小米11");
        return context;
    }

    private UserLoginContext createUserLoginContext() {
        UserLoginContext context = new UserLoginContext();
        context.setUsername(USERNAME);
        context.setPassword(PASSWORD);
        return context;
    }
}
