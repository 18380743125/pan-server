package com.tangl.pan.server.modules.user.controller;

import com.tangl.pan.core.response.R;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.server.common.annotation.LoginIgnore;
import com.tangl.pan.server.common.utils.UserIdUtil;
import com.tangl.pan.server.modules.user.context.*;
import com.tangl.pan.server.modules.user.converter.UserConverter;
import com.tangl.pan.server.modules.user.po.*;
import com.tangl.pan.server.modules.user.service.IUserService;
import com.tangl.pan.server.modules.user.vo.UserInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户模块的控制器
 */
@Api(tags = "用户模块")
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private UserConverter userConverter;

    @ApiOperation(value = "用户注册接口", notes = "该接口提供了用户注册的功能, 实现了幂等性注册的逻辑, 可以放心多并发调用", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @LoginIgnore
    @PostMapping("register")
    public R<?> register(@Validated @RequestBody UserRegisterPO userRegisterPO) {
        UserRegisterContext context = userConverter.userRegisterPO2UserRegisterContext(userRegisterPO);
        Long userId = userService.register(context);
        return R.data(IdUtil.encrypt(userId));
    }

    @ApiOperation(value = "用户登录接口", notes = "该接口提供了用户登录的功能, 成功登录之后, 会返回有时效性的 accessToken 供后续服务使用", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @LoginIgnore
    @PostMapping("login")
    public R<?> login(@Validated @RequestBody UserLoginPO userLoginPO) {
        UserLoginContext context = userConverter.userLoginPO2UserLoginContext(userLoginPO);
        String accessToken = userService.login(context);
        return R.data(accessToken);
    }

    @ApiOperation(value = "用户登出接口", notes = "该接口提供了用户登出的功能", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("exit")
    public R<?> exit() {
        userService.exit(UserIdUtil.get());
        return R.success();
    }

    @LoginIgnore
    @ApiOperation(value = "用户忘记密码-校验用户名", notes = "该接口提供了用户忘记密码-校验用户名的功能", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("username/check")
    public R<?> checkUsername(@Validated @RequestBody CheckUsernamePO checkUsernamePO) {
        CheckUsernameContext context = userConverter.checkUsernamePO2CheckUsernameContext(checkUsernamePO);
        String question = userService.checkUsername(context);
        return R.data(question);
    }

    @LoginIgnore
    @ApiOperation(value = "用户忘记密码-校验密保答案", notes = "该接口提供了用户忘记密码-校验密保答案的功能", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("answer/check")
    public R<?> checkAnswer(@Validated @RequestBody CheckAnswerPO checkAnswerPO) {
        CheckAnswerContext context = userConverter.checkAnswerPO2CheckAnswerContext(checkAnswerPO);
        String token = userService.checkAnswer(context);
        return R.data(token);
    }

    @LoginIgnore
    @ApiOperation(value = "用户忘记密码-重置用户密码", notes = "该接口提供了用户忘记密码-重置用户密码的功能", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("password/reset")
    public R<?> resetPassword(@Validated @RequestBody ResetPasswordPO resetPasswordPO) {
        ResetPasswordContext context = userConverter.resetPasswordPO2ResetPasswordContext(resetPasswordPO);
        userService.resetPassword(context);
        return R.success();
    }

    @ApiOperation(value = "用户在线修改密码", notes = "该接口提供了用户在线修改密码的功能", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("password/change")
    public R<?> changePassword(@Validated @RequestBody ChangePasswordPO changePasswordPO) {
        ChangePasswordContext context = userConverter.changePasswordPO2ChangePasswordContext(changePasswordPO);
        context.setUserId(UserIdUtil.get());
        userService.changePassword(context);
        return R.success();
    }

    @ApiOperation(value = "查询登录用户的基本信息", notes = "该接口提供了查询登录用户的基本信息的功能", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/")
    public R<UserInfoVO> info() {
        UserInfoVO vo = userService.info(UserIdUtil.get());
        return R.data(vo);
    }
}
