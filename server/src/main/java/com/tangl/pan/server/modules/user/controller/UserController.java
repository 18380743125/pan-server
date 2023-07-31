package com.tangl.pan.server.modules.user.controller;

import com.tangl.pan.core.response.R;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.server.modules.user.context.UserRegisterContext;
import com.tangl.pan.server.modules.user.converter.UserConverter;
import com.tangl.pan.server.modules.user.po.UserRegisterPO;
import com.tangl.pan.server.modules.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tangl
 * @description 用户模块的控制器实体
 * @create 2023-07-28 21:10
 */
@Api("用户模块")
@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private IUserService userService;

    @Autowired
    private UserConverter userConverter;

    @ApiOperation(
            value = "用户注册接口",
            notes = "该接口提供了用户注册的功能, 实现了幂等性注册的逻辑, 可以放心多并发调用",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PostMapping("register")
    public R register(@Validated @RequestBody UserRegisterPO userRegisterPO) {
        UserRegisterContext userRegisterContext = userConverter.userRegisterPO2UserRegisterContext(userRegisterPO);
        Long userId = userService.register(userRegisterContext);
        return R.data(IdUtil.encrypt(userId));
    }
}
