package com.tangl.pan.server.modules.user;

import cn.hutool.core.lang.Assert;
import com.tangl.pan.core.exception.TPanBusinessException;
import com.tangl.pan.server.TPanServerLauncher;
import com.tangl.pan.server.modules.user.constants.UserConstants;
import com.tangl.pan.server.modules.user.context.*;
import com.tangl.pan.server.modules.user.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

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
     * 忘记密码-校验重置密码-成功
     */
    @Test
    public void testResetPasswordSuccess() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);
        CheckAnswerContext checkAnswerContext = new CheckAnswerContext();
        checkAnswerContext.setUsername(USERNAME);
        checkAnswerContext.setQuestion("你的手机型号");
        checkAnswerContext.setAnswer("小米11");
        String token = userService.checkAnswer(checkAnswerContext);
        Assert.isTrue(StringUtils.isNotBlank(token));
        ResetPasswordContext resetPasswordContext = new ResetPasswordContext();
        resetPasswordContext.setToken(token);
        resetPasswordContext.setPassword("123123");
        resetPasswordContext.setUsername(USERNAME);
        userService.resetPassword(resetPasswordContext);
    }

    /**
     * 忘记密码-校验重置密码-失败
     */
    @Test(expected = TPanBusinessException.class)
    public void testResetPasswordWrong() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);
        CheckAnswerContext checkAnswerContext = new CheckAnswerContext();
        checkAnswerContext.setUsername(USERNAME);
        checkAnswerContext.setQuestion("你的手机型号");
        checkAnswerContext.setAnswer("小米11");
        String token = userService.checkAnswer(checkAnswerContext);
        Assert.isTrue(StringUtils.isNotBlank(token));
        ResetPasswordContext resetPasswordContext = new ResetPasswordContext();
        resetPasswordContext.setToken(token + "_change");
        resetPasswordContext.setPassword("123123");
        resetPasswordContext.setUsername(USERNAME);
        userService.resetPassword(resetPasswordContext);
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
